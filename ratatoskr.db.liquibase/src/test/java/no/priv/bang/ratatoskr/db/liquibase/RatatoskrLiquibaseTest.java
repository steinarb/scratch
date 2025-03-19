/*
 * Copyright 2023-2025 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package no.priv.bang.ratatoskr.db.liquibase;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.db.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.Properties;

import javax.sql.DataSource;

import org.assertj.db.type.AssertDbConnectionFactory;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import liquibase.exception.LiquibaseException;

class RatatoskrLiquibaseTest {
    DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();

    @Test
    void testCreateSchema() throws Exception {
        var ratatoskrLiquibase = new RatatoskrLiquibase();
        var datasource = createDataSource("ratatoskr");
        var assertjConnection = AssertDbConnectionFactory.of(datasource).create();

        ratatoskrLiquibase.createInitialSchema(datasource.getConnection());

        var accounts1 = assertjConnection.table("ratatoskr_accounts").build();
        assertThat(accounts1).exists().isEmpty();

        addAccounts(datasource);

        var accounts2 = assertjConnection.table("ratatoskr_accounts").build();
        assertThat(accounts2).hasNumberOfRows(1);

        var actors1 = assertjConnection.table("actors").build();
        assertThat(actors1).exists().isEmpty();

        var actorId = addActor(
            datasource,
            "https://kenzoishii.example.com",
            "kenzoishii",
            "石井健蔵",
            "この方はただの例です",
            "https://kenzoishii.example.com/inbox.json",
            "https://kenzoishii.example.com/following.json",
            "https://kenzoishii.example.com/followers.json",
            "https://kenzoishii.example.com/liked.json",
            "https://kenzoishii.example.com/image/165987aklre4");

        var actors2 = assertjConnection.table("actors").build();
        assertThat(actors2).exists().hasNumberOfRows(1).row(0)
            .value("actor_id").isEqualTo(actorId)
            .value("id").isEqualTo("https://kenzoishii.example.com")
            .value("preferred_username").isEqualTo("kenzoishii")
            .value("name").isEqualTo("石井健蔵")
            .value("summary").isEqualTo("この方はただの例です")
            .value("inbox").isEqualTo("https://kenzoishii.example.com/inbox.json")
            .value("following").isEqualTo("https://kenzoishii.example.com/following.json")
            .value("followers").isEqualTo("https://kenzoishii.example.com/followers.json")
            .value("liked").isEqualTo("https://kenzoishii.example.com/liked.json")
            .value("icon").isEqualTo("https://kenzoishii.example.com/image/165987aklre4");

        var anotherActorId = addActor(
            datasource,
            "https://sally.example.com",
            "sally",
            "Sally Smith",
            "Someone important",
            "https://sally.example.com/inbox.json",
            "https://sally.example.com/following.json",
            "https://sally.example.com/followers.json",
            "https://sally.example.com/liked.json",
            "http://localhost:8181/ratatoskr/image/165987aklre6");
        addFollower(datasource, actorId, anotherActorId);

        var followers1 = assertjConnection
            .request("select * from followers where followed=? and follower=?")
            .parameters(actorId, anotherActorId)
            .build();
        assertThat(followers1).hasNumberOfRows(1).row(0)
            .value("followed").isEqualTo(actorId)
            .value("follower").isEqualTo(anotherActorId);

        var articles1 = assertjConnection.table("articles").build();
        assertThat(articles1).exists().isEmpty();
        var articleId = addArticle(datasource, "https://sally.example.com/posts/123", "What a Crazy Day I Had", "<div>... you will never believe ...</div>", anotherActorId);
        assertThat(articleId).isGreaterThan(0);

        var articles2 = assertjConnection.table("articles").build();
        assertThat(articles2).hasNumberOfRowsGreaterThan(0)
            .row(0)
            .value("name").isEqualTo("What a Crazy Day I Had")
            .value("content").isEqualTo("<div>... you will never believe ...</div>")
            .value("attributed_to").isEqualTo(anotherActorId);

        // Try adding article with the same id as an existing
        assertThrows(SQLIntegrityConstraintViolationException.class, () -> addArticle(datasource, "https://sally.example.com/posts/123", "Not the same", "Different", anotherActorId));
        // Try adding article with attributed_to not matching actor to verify constraint
        assertThrows(SQLIntegrityConstraintViolationException.class, () -> addArticle(datasource, "xxxyz", "foo", "bars", 357));

        var groupId = addGroup(datasource, "Project XYZ Working Group");
        assertThat(groupId).isGreaterThan(-1);

        var published = new Date();
        var likeid = addLike(datasource, "https://sally.example.com/likes/123", "Sally liked an article", groupId, anotherActorId, articleId, published);
        var like = assertjConnection.request("select * from likes where like_id=?").parameters(likeid).build();
        assertThat(like).hasNumberOfRows(1)
            .row(0)
            .value("id").isEqualTo("https://sally.example.com/likes/123")
            .value("summary").isEqualTo("Sally liked an article")
            .value("audience").isEqualTo(groupId)
            .value("actor").isEqualTo(anotherActorId)
            .value("article").isEqualTo(articleId)
            .value("published").isEqualTo(Timestamp.from(published.toInstant()));

        // Check that illegal group breaks constraints
        assertThrows(SQLIntegrityConstraintViolationException.class, () -> addLike(datasource, "https://sally.example.com/likes/124", "Sally liked an article", groupId+1, anotherActorId, articleId, published));
        // Check that null group is ok
        assertDoesNotThrow(() -> addLike(datasource, "https://sally.example.com/likes/125", "Sally liked an article", null, anotherActorId, articleId, published));

        // Check that null id is allowed (duplicates ar not allowed)
        assertDoesNotThrow(() -> addLike(datasource, null, "Sally liked an article", groupId, anotherActorId, articleId, published));
        // Check that a second null id is still allowed (duplicates ar not allowed but duplicate nulls are OK)
        assertDoesNotThrow(() -> addLike(datasource, null, "Sally liked an article", groupId, anotherActorId, articleId, published));
        // Check that null published date is allowed
        assertDoesNotThrow(() -> addLike(datasource, "https://sally.example.com/likes/126", "Sally liked an article", groupId, anotherActorId, articleId, null));

        ratatoskrLiquibase.updateSchema(datasource.getConnection());
    }

    @Test
    void testCreateSchemaAndFail() throws Exception {
        var connection = spy(createConnection("ratatoskr1"));
        // A Derby JDBC connection wrapped in a Mockito spy() fails om Connection.setAutoClosable()

        var ratatoskrLiquibase = new RatatoskrLiquibase();

        var ex = assertThrows(
            LiquibaseException.class,
            () -> ratatoskrLiquibase.createInitialSchema(connection));
        assertThat(ex.getMessage()).startsWith("java.sql.SQLException: Cannot set Autocommit On when in a nested connection");
    }

    @Test
    void testCreateSchemaAndFailOnConnectionClose() throws Exception {
        var connection = spy(createConnection("ratatoskr2"));
        doNothing().when(connection).setAutoCommit(anyBoolean());
        doThrow(Exception.class).when(connection).close();

        var ratatoskrLiquibase = new RatatoskrLiquibase();

        var ex = assertThrows(
            LiquibaseException.class,
            () -> ratatoskrLiquibase.createInitialSchema(connection));
        assertThat(ex.getMessage()).startsWith("java.lang.Exception");
    }

    private void addAccounts(DataSource datasource) throws Exception {
        try(var connection = datasource.getConnection()) {
            addAccount(connection, "admin");
        }
    }

    private int addAccount(Connection connection, String username) throws Exception {
        var sql = "insert into ratatoskr_accounts (username) values (?)";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.executeUpdate();
        }

        return findAccountId(connection, username);
    }

    private int findAccountId(Connection connection, String username) throws Exception {
        var sql = "select account_id from ratatoskr_accounts where username=?";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try(var results = statement.executeQuery()) {
                if (results.next()) {
                    return results.getInt(1);
                }
            }
        }

        return -1;
    }

    private int addActor(DataSource datasource, String id, String preferredUsername, String name, String summary, String inbox, String following, String followers, String liked, String icon) throws Exception {
        try(var connection = datasource.getConnection()) {
            var sql = "insert into actors (id, preferred_username, name, summary, inbox, following, followers, liked, icon) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try(var statement = connection.prepareStatement(sql)) {
                statement.setString(1, id);
                statement.setString(2, preferredUsername);
                statement.setString(3, name);
                statement.setString(4, summary);
                statement.setString(5, inbox);
                statement.setString(6, following);
                statement.setString(7, followers);
                statement.setString(8, liked);
                statement.setString(9, icon);
                statement.executeUpdate();
            }

            return findActorId(connection, id);
        }
    }

    private int addGroup(DataSource datasource, String name) throws Exception {
        try(var connection = datasource.getConnection()) {
            var sql = "insert into groups (name) values (?)";
            try(var statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                statement.executeUpdate();
            }

            return findNewestGroupId(connection);
        }
    }

    private void addFollower(DataSource datasource, int actorId, int anotherActorId) throws Exception {
        try(var connection = datasource.getConnection()) {
            var sql = "insert into followers (followed, follower) values (?, ?)";
            try(var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, actorId);
                statement.setInt(2, anotherActorId);
                statement.executeUpdate();
            }
        }
    }

    private int addArticle(DataSource datasource, String id, String name, String content, int actorAttributedTo) throws Exception {
        try(var connection = datasource.getConnection()) {
            var sql = "insert into articles (id, name, content, attributed_to) values (?, ?, ?, ?)";
            try(var statement = connection.prepareStatement(sql)) {
                statement.setString(1, id);
                statement.setString(2, name);
                statement.setString(3, content);
                statement.setInt(4, actorAttributedTo);
                statement.executeUpdate();
            }

            try (var statement = connection.createStatement()) {
                try(var results = statement.executeQuery("select article_id from articles order by article_id desc")) {
                    while(results.next()) {
                        return results.getInt("article_id");
                    }
                }
            }
        }

        return -1;
    }

    private int addLike(DataSource datasource, String id, String summary, Integer groupId, int actorId, int articleId, Date published) throws Exception {
        try(var connection = datasource.getConnection()) {
            var sql = "insert into likes (id, summary, audience, actor, article, published) values (?, ?, ?, ?, ?, ?)";
            try(var statement = connection.prepareStatement(sql)) {
                statement.setString(1, id);
                statement.setString(2, summary);
                if (groupId != null) {
                    statement.setInt(3, groupId);
                } else {
                    statement.setNull(3, Types.INTEGER);
                }
                statement.setInt(4, actorId);
                statement.setInt(5, articleId);
                statement.setTimestamp(6, published != null ? Timestamp.from(published.toInstant()) : null);
                statement.executeUpdate();
            }

            try (var statement = connection.prepareStatement("select like_id from likes where id=?")) {
                statement.setString(1, id);
                try(var results = statement.executeQuery()) {
                    while(results.next()) {
                        return results.getInt("like_id");
                    }
                }
            }
        }

        return -1;
    }

    private int findActorId(Connection connection, String id) throws Exception {
        var sql = "select actor_id from actors where id=?";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try(var results = statement.executeQuery()) {
                while(results.next()) {
                    return results.getInt("actor_id");
                }
            }
        }
        return -1;
    }

    private int findNewestGroupId(Connection connection) throws Exception {
        var sql = "select group_id from groups order by group_id desc";
        try(var statement = connection.createStatement()) {
            try(var results = statement.executeQuery(sql)) {
                while(results.next()) {
                    return results.getInt("group_id");
                }
            }
        }

        return -1;
    }

    private Connection createConnection(String dbname) throws Exception {
        var dataSource = createDataSource(dbname);
        return dataSource.getConnection();
    }

    private DataSource createDataSource(String dbname) throws SQLException {
        var properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + ";create=true");
        return derbyDataSourceFactory.createDataSource(properties);
    }

}
