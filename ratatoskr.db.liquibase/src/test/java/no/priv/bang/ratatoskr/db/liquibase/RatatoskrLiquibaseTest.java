/*
 * Copyright 2023-2026 Steinar Bang
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
import java.time.Instant;
import java.time.ZonedDateTime;
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

        var profiles1 = assertjConnection.table("profiles").build();
        assertThat(profiles1).exists().isEmpty();

        var profileId = addProfile(
            datasource,
            "https://kenzoishii.example.com",
            "kenzoishii",
            "石井健蔵",
            "この方はただの例です");

        var profiles2 = assertjConnection.request("SELECT p.*, u.* FROM profiles p JOIN urls u ON p.url_id = u.url_id").build();
        assertThat(profiles2).hasNumberOfRows(1).row(0)
            .value("profile_id").isEqualTo(profileId)
            .value("url").isEqualTo("https://kenzoishii.example.com")
            .value("username").isEqualTo("kenzoishii")
            .value("display_name").isEqualTo("石井健蔵")
            .value("description").isEqualTo("この方はただの例です");

        var anotherProfileId = addProfile(
            datasource,
            "https://sally.example.com",
            "sally",
            "Sally Smith",
            "Someone important");
        addFollower(datasource, profileId, anotherProfileId);

        var follows1 = assertjConnection
            .request("select * from follows where follows_id=? and followed_id=?")
            .parameters(profileId, anotherProfileId)
            .build();
        assertThat(follows1).hasNumberOfRows(1).row(0)
            .value("follows_id").isEqualTo(profileId)
            .value("followed_id").isEqualTo(anotherProfileId);

        var posts1 = assertjConnection.table("posts").build();
        assertThat(posts1).exists().isEmpty();
        var articleId = addArticle(datasource, "https://sally.example.com/posts/123", "What a Crazy Day I Had", "<div>... you will never believe ...</div>", anotherProfileId);
        assertThat(articleId).isGreaterThan(0);

        var posts2 = assertjConnection.table("posts").build();
        assertThat(posts2).hasNumberOfRowsGreaterThan(0)
            .row(0)
            .value("title").isEqualTo("What a Crazy Day I Had")
            .value("content").isEqualTo("<div>... you will never believe ...</div>")
            .value("author_id").isEqualTo(anotherProfileId);

        // Try adding article with attributed_to not matching actor to verify constraint
        assertThrows(SQLIntegrityConstraintViolationException.class, () -> addArticle(datasource, "xxxyz", "foo", "bars", 357));

        var groupId = addGroup(datasource, "Project XYZ Working Group");
        assertThat(groupId).isGreaterThan(-1);

        var published = ZonedDateTime.now().toInstant();
        var likeid = addLike(datasource, "https://sally.example.com/likes/123", groupId, articleId, published);
        var like = assertjConnection.request("select l.*, u.* from likes l join urls u on l.url_id=u.url_id where like_id=?").parameters(likeid).build();
        assertThat(like).hasNumberOfRows(1)
            .row(0)
            .value("url").isEqualTo("https://sally.example.com/likes/123")
            .value("profile_id").isNull()
            .value("post_id").isEqualTo(articleId)
            .value("published_time").isEqualTo(Timestamp.from(published));

        var boostid = addBoost(datasource, "https://sally.example.com/likes/123", groupId, articleId, published);
        var boost = assertjConnection.request("select b.*, u.* from boosts b join urls u on b.url_id=u.url_id where boost_id=?").parameters(boostid).build();
        assertThat(boost).hasNumberOfRows(1)
            .row(0)
            .value("url").isEqualTo("https://sally.example.com/likes/123")
            .value("profile_id").isNull()
            .value("post_id").isEqualTo(articleId)
            .value("published_time").isEqualTo(Timestamp.from(published));

        var creationTime = ZonedDateTime.now().toInstant();
        var activityid = addActivity(datasource, "https://sally.example.com/outbox/234", "https://sally.example.com/people/sally", "https://sally.example.com/people/sally/posts/3", creationTime);
        assertThat(activityid).isGreaterThan(-1);
        var activites = assertjConnection.table("activities").build();
        assertThat(activites).hasNumberOfRows(1)
            .row(0)
            .value("url_id").isGreaterThan(-1)
            .value("actor_url").isGreaterThan(-1)
            .value("actor_id").isNull()
            .value("object_url").isGreaterThan(-1)
            .value("object_id").isNull()
            .value("creation_time").isEqualTo(Timestamp.from(creationTime));

        var receivedTime = ZonedDateTime.now().toInstant();
        int inboxEntryId = receiveActivity(datasource, activityid, profileId, receivedTime);
        var inbox = assertjConnection.table("inbox").build();
        assertThat(inbox).hasNumberOfRows(1)
            .row(0)
            .value("inbox_id").isEqualTo(inboxEntryId)
            .value("activity_id").isEqualTo(activityid)
            .value("profile_id").isEqualTo(profileId)
            .value("received_time").isEqualTo(Timestamp.from(receivedTime));

        // Check inbox constraints
        assertThrows(SQLIntegrityConstraintViolationException.class, () -> receiveActivity(datasource, 100, profileId, receivedTime));
        assertThrows(SQLIntegrityConstraintViolationException.class, () -> receiveActivity(datasource, activityid, 100, receivedTime));

        var sentTime = ZonedDateTime.now().toInstant();
        int outboxEntryId = sendActivity(datasource, activityid, profileId, sentTime);
        var outbox = assertjConnection.table("outbox").build();
        assertThat(outbox).hasNumberOfRows(1)
            .row(0)
            .value("outbox_id").isEqualTo(outboxEntryId)
            .value("activity_id").isEqualTo(activityid)
            .value("profile_id").isEqualTo(profileId)
            .value("sent_time").isEqualTo(Timestamp.from(sentTime));

        // Check outbox constraints
        assertThrows(SQLIntegrityConstraintViolationException.class, () -> sendActivity(datasource, 100, profileId, sentTime));
        assertThrows(SQLIntegrityConstraintViolationException.class, () -> sendActivity(datasource, activityid, 100, sentTime));

        ratatoskrLiquibase.updateSchema(datasource.getConnection());
    }

    private int receiveActivity(DataSource datasource, int activityid, int profileId, Instant receivedTime) throws Exception {
        try(var connection = datasource.getConnection()) {
            var sql = "insert into inbox (activity_id, profile_id, received_time) values (?, ?, ?)";
            try(var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, activityid);
                statement.setInt(2, profileId);
                statement.setTimestamp(3, receivedTime != null ? Timestamp.from(receivedTime) : null);
                statement.executeUpdate();
            }

            try (var statement = connection.prepareStatement("select inbox_id from inbox order by inbox_id desc")) {
                try(var results = statement.executeQuery()) {
                    while(results.next()) {
                        return results.getInt("inbox_id");
                    }
                }
            }
        }

        return -1;
    }

    private int sendActivity(DataSource datasource, int activityid, int profileId, Instant sentTime) throws Exception {
        try(var connection = datasource.getConnection()) {
            var sql = "insert into outbox (activity_id, profile_id, sent_time) values (?, ?, ?)";
            try(var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, activityid);
                statement.setInt(2, profileId);
                statement.setTimestamp(3, sentTime != null ? Timestamp.from(sentTime) : null);
                statement.executeUpdate();
            }

            try (var statement = connection.prepareStatement("select outbox_id from outbox order by outbox_id desc")) {
                try(var results = statement.executeQuery()) {
                    while(results.next()) {
                        return results.getInt("outbox_id");
                    }
                }
            }
        }

        return -1;
    }

    private int addActivity(DataSource datasource, String idUrl, String actorUrl, String postUrl, Instant creationTime) throws Exception {
        try(var connection = datasource.getConnection()) {
            var urlId = addUrl(connection, idUrl);
            var actorUrlId = addUrl(connection, actorUrl);
            var postUrlId = addUrl(connection, postUrl);
            var sql = "insert into activities (url_id, actor_url, object_url, creation_time) values (?, ?, ?, ?)";
            try(var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, urlId);
                statement.setInt(2, actorUrlId);
                statement.setInt(3, postUrlId);
                statement.setTimestamp(4, creationTime != null ? Timestamp.from(creationTime) : null);
                statement.executeUpdate();
            }

            try (var statement = connection.prepareStatement("select activity_id from activities where url_id=?")) {
                statement.setInt(1, urlId);
                try(var results = statement.executeQuery()) {
                    while(results.next()) {
                        return results.getInt("activity_id");
                    }
                }
            }
        }

        return -1;
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
        try (var realConnection = createConnection("ratatoskr2")) {
            var connection = spy(realConnection);
            doNothing().when(connection).setAutoCommit(anyBoolean());
            doThrow(Exception.class).when(connection).close(); // Note: the underlying connection of the spy must be closed after the test

            var ratatoskrLiquibase = new RatatoskrLiquibase();

            var ex = assertThrows(
                LiquibaseException.class,
                () -> ratatoskrLiquibase.createInitialSchema(connection));
            assertThat(ex.getMessage()).startsWith("java.lang.Exception");
        }
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

    private int addProfile(DataSource datasource, String idUrl, String preferredUsername, String name, String summary) throws Exception {
        try(var connection = datasource.getConnection()) {
            int urlId = addUrl(connection, idUrl);
            var sql = "insert into profiles (url_id, local, type, username, display_name, description) values (?, ?, ?, ?, ?, ?)";
            try(var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, urlId);
                statement.setBoolean(2, false);
                statement.setString(3, "Person");
                statement.setString(4, preferredUsername);
                statement.setString(5, name);
                statement.setString(6, summary);
                statement.executeUpdate();
            }

            var profileId = findProfileId(connection, urlId);
            resolveProfileUrl(connection, urlId, profileId);
            return profileId;
        }
    }

    private void resolveProfileUrl(Connection connection, int urlId, int profileId) throws Exception {
        var sql = "update urls set profile_id=?, resolved=? where url_id=?";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, profileId);
            statement.setBoolean(2, true);
            statement.setInt(3, urlId);
            statement.executeUpdate();
        }
    }

    private int addUrl(Connection connection, String idUrl) throws Exception {
        var urlId = findUrlId(connection, idUrl);
        if (urlId > 0) {
            return urlId;
        }

        var sql = "insert into urls (url) values (?)";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setString(1, idUrl);
            statement.executeUpdate();
        }

        return findUrlId(connection, idUrl);
    }

    private int findUrlId(Connection connection, String idUrl) throws SQLException {
        var sql2 = "select url_id from urls where url=?";
        try(var statement = connection.prepareStatement(sql2)) {
            statement.setString(1, idUrl);
            try(var results = statement.executeQuery()) {
                while(results.next()) {
                    return results.getInt("url_id");
                }
            }
        }
        return -1;
    }

    private int addGroup(DataSource datasource, String name) throws Exception {
        try(var connection = datasource.getConnection()) {
            var sql = "insert into profiles (type, display_name) values ('Group', ?)";
            try(var statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                statement.executeUpdate();
            }

            return findNewestGroupId(connection);
        }
    }

    private void addFollower(DataSource datasource, int actorId, int anotherActorId) throws Exception {
        try(var connection = datasource.getConnection()) {
            var sql = "insert into follows (follows_id, followed_id) values (?, ?)";
            try(var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, actorId);
                statement.setInt(2, anotherActorId);
                statement.executeUpdate();
            }
        }
    }

    private int addArticle(DataSource datasource, String idUrl, String name, String content, int actorAttributedTo) throws Exception {
        try(var connection = datasource.getConnection()) {
            int urlId = addUrl(connection, idUrl);
            var sql = "insert into posts (url_id, type, title, content, author_id) values (?, ?, ?, ?, ?)";
            try(var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, urlId);
                statement.setString(2, "Article");
                statement.setString(3, name);
                statement.setString(4, content);
                statement.setInt(5, actorAttributedTo);
                statement.executeUpdate();
            }

            var postId = findMostRecentlyAddedPost(connection);
            resolvePostUrl(connection, urlId, postId);
            return postId;
        }
    }

    private void resolvePostUrl(Connection connection, int urlId, int postId) throws Exception {
        var sql = "update urls set post_id=?, resolved=? where url_id=?";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, postId);
            statement.setBoolean(2, true);
            statement.setInt(3, urlId);
            statement.executeUpdate();
        }
    }

    private int findMostRecentlyAddedPost(Connection connection) throws SQLException {
        try (var statement = connection.createStatement()) {
            try(var results = statement.executeQuery("select post_id from posts order by post_id desc")) {
                while(results.next()) {
                    return results.getInt("post_id");
                }
            }
        }
        return -1;
    }

    private int addLike(DataSource datasource, String idUrl, Integer groupId, int articleId, Instant published) throws Exception {
        try(var connection = datasource.getConnection()) {
            int urlId = addUrl(connection, idUrl);
            var sql = "insert into likes (url_id, actor_id, post_id, published_time) values (?, ?, ?, ?)";
            try(var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, urlId);
                if (groupId != null) {
                    statement.setInt(2, groupId);
                } else {
                    statement.setNull(2, Types.INTEGER);
                }
                statement.setInt(3, articleId);
                statement.setTimestamp(4, published != null ? Timestamp.from(published) : null);
                statement.executeUpdate();
            }

            try (var statement = connection.prepareStatement("select like_id from likes where url_id=?")) {
                statement.setInt(1, urlId);
                try(var results = statement.executeQuery()) {
                    while(results.next()) {
                        return results.getInt("like_id");
                    }
                }
            }
        }

        return -1;
    }

    private int addBoost(DataSource datasource, String idUrl, int profileId, int articleId, Instant published) throws Exception {
        try(var connection = datasource.getConnection()) {
            int urlId = addUrl(connection, idUrl);
            var sql = "insert into boosts (url_id, actor_id, post_id, published_time) values (?, ?, ?, ?)";
            try(var statement = connection.prepareStatement(sql)) {
                statement.setInt(1, urlId);
                statement.setInt(2, profileId);
                statement.setInt(3, articleId);
                statement.setTimestamp(4, published != null ? Timestamp.from(published) : null);
                statement.executeUpdate();
            }

            try (var statement = connection.prepareStatement("select boost_id from boosts where url_id=?")) {
                statement.setInt(1, urlId);
                try(var results = statement.executeQuery()) {
                    while(results.next()) {
                        return results.getInt("boost_id");
                    }
                }
            }
        }

        return -1;
    }

    private int findProfileId(Connection connection, int urlId) throws Exception {
        var sql = "select profile_id from profiles where url_id=?";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, urlId);
            try(var results = statement.executeQuery()) {
                while(results.next()) {
                    return results.getInt("profile_id");
                }
            }
        }
        return -1;
    }

    private int findNewestGroupId(Connection connection) throws Exception {
        var sql = "select profile_id from profiles where type='Group' order by profile_id desc";
        try(var statement = connection.createStatement()) {
            try(var results = statement.executeQuery(sql)) {
                while(results.next()) {
                    return results.getInt("profile_id");
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
