package no.priv.bang.jdbc.sqldumper;
/*
 * Copyright 2023 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import liquibase.Scope;
import liquibase.Scope.ScopedRunner;
import liquibase.changelog.ChangeLogParameters;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DatabaseChangelogCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.sdk.resource.MockResourceAccessor;
import no.priv.bang.jdbc.sqldumper.beans.AlbumEntry;
import no.priv.bang.oldalbum.db.liquibase.OldAlbumLiquibase;
import no.priv.bang.oldalbum.db.liquibase.test.OldAlbumDerbyTestDatabase;

class ResultSetSqlDumperTest {
    DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();

    @Test
    void testDumpResultSetOnOldalbum() throws Exception {
        var changesetId = "sb:album_paths";
        var sqldumper = new ResultSetSqlDumper();
        var oldalbumDatasource = createOldalbumDbWithData("oldalbum1");
        Path tempfile = Files.createTempFile(findTempdirAsTargetSubdir(), "oldalbum", "sql");
        Files.delete(tempfile);
        try(var outputstream = Files.newOutputStream(tempfile)) {
            var sql = "select * from albumentries";
            try(var connection = oldalbumDatasource.getConnection()) {
                try(var statement = connection.createStatement()) {
                    try(var resultset = statement.executeQuery(sql)) {
                        sqldumper.dumpResultSetAsSql(changesetId, resultset, outputstream);
                    }
                }
            }
        }

        var dumpedsql = Files.readString(tempfile);
        assertThat(dumpedsql)
            .startsWith("--liquibase formatted sql")
            .contains("--changeset sb:saved_albumentries")
            .contains("insert into ALBUMENTRIES (ALBUMENTRY_ID, PARENT, LOCALPATH, ALBUM, TITLE, DESCRIPTION, IMAGEURL, THUMBNAILURL, SORT, LASTMODIFIED, CONTENTTYPE, CONTENTLENGTH, REQUIRE_LOGIN) values")
            .contains("1, 0, '/', true, 'Picture archive', '', '', '', 0, null, null, null")
            .contains("11, 4, '/moto/vfr96/acirc3', false, '', 'My VFR 750F at the arctic circle.', 'https://www.bang.priv.no/sb/pics/moto/vfr96/acirc3.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc3.gif', 3, '1996-10-04 18:28:58.0', 'image/jpeg', 57732");

        var restoredOldalbumDatasource = createOldalbumDbWithouthData("oldalbum2");
        assertEmptyAlbumentries(restoredOldalbumDatasource);
        setDatabaseContentAsLiquibaseChangelog(restoredOldalbumDatasource, dumpedsql);
        assertAlbumentriesNotEmpty(restoredOldalbumDatasource);
        var originalAlbumEntries = findAllAlbumentries(oldalbumDatasource);
        var restoredAlbumEntries = findAllAlbumentries(restoredOldalbumDatasource);
        assertThat(restoredAlbumEntries).containsExactlyElementsOf(originalAlbumEntries);
    }

    @Test
    void testFindSchema() throws Exception {
        var sqldumper = new ResultSetSqlDumper();
        var oldalbumDatasource = createOldalbumDbWithData("oldalbum1");
        var sql = "select * from albumentries";
        try(var connection = oldalbumDatasource.getConnection()) {
            try(var statement = connection.createStatement()) {
                try(var resultset = statement.executeQuery(sql)) {
                    List<String> columnames = sqldumper.findColumnNames(resultset);
                    assertThat(columnames).hasSize(13);
                    Map<String, Integer> columntypes = sqldumper.findColumntypes(resultset);
                    assertThat(columntypes).hasSize(columnames.size());
                    String tablename = sqldumper.findTableName(resultset);
                    assertEquals("ALBUMENTRIES", tablename);
                }
            }
        }
    }

    private void assertEmptyAlbumentries(DataSource oldalbumDatasource) throws Exception {
        var sql = "select * from albumentries";
        try(var connection = oldalbumDatasource.getConnection()) {
            try(var statement = connection.createStatement()) {
                try(var resultset = statement.executeQuery(sql)) {
                    assertFalse(resultset.next(), "Expected albumentries table to be empty");
                }
            }
        }
    }

    private void assertAlbumentriesNotEmpty(DataSource oldalbumDatasource) throws Exception {
        var sql = "select * from albumentries";
        try(var connection = oldalbumDatasource.getConnection()) {
            try(var statement = connection.createStatement()) {
                try(var resultset = statement.executeQuery(sql)) {
                    assertTrue(resultset.next(), "Expected albumentries table not to be empty");
                }
            }
        }
    }

    private void setDatabaseContentAsLiquibaseChangelog(DataSource datasource, String contentLiquibaseChangelog) throws Exception {
        Map<String, String> contentByFileName = new HashMap<>();
        contentByFileName.put("dumproutes.sql", contentLiquibaseChangelog);
        try(var connection = datasource.getConnection()) {
            try(var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection))) {
                Map<String, Object> scopeObjects = Map.of(
                    Scope.Attr.database.name(), database,
                    Scope.Attr.resourceAccessor.name(), new MockResourceAccessor(contentByFileName));

                Scope.child(scopeObjects, (ScopedRunner<?>) () -> new CommandScope("update")
                            .addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database)
                            .addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, "dumproutes.sql")
                            .addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, new ChangeLogParameters(database))
                            .execute());
            }
        }
    }

    private List<AlbumEntry> findAllAlbumentries(DataSource datasource) throws Exception {
        List<AlbumEntry> allroutes = new ArrayList<>();

        String sql = "select * from albumentries";
        try (var connection = datasource.getConnection()) {
            try (var statement = connection.createStatement()) {
                try (var results = statement.executeQuery(sql)) {
                    while (results.next()) {
                        AlbumEntry route = unpackAlbumEntry(results);
                        allroutes.add(route);
                    }
                }
            }
        }

        return allroutes;
    }

    private AlbumEntry unpackAlbumEntry(ResultSet results) throws Exception {
        return AlbumEntry.with()
            .id(results.getInt("albumentry_id"))
            .parent(results.getInt("parent"))
            .path(results.getString("localpath"))
            .album(results.getBoolean("album"))
            .title(results.getString("title"))
            .description(results.getString("description"))
            .imageUrl(results.getString("imageurl"))
            .thumbnailUrl(results.getString("thumbnailurl"))
            .sort(results.getInt("sort"))
            .lastModified(timestampToDate(results.getTimestamp("lastmodified")))
            .contentType(results.getString("contenttype"))
            .contentLength(results.getInt("contentlength"))
            .requireLogin(results.getBoolean("require_login"))
            .childcount(findChildCount(results))
            .build();
    }

    private int findChildCount(ResultSet results) throws Exception {
        int columncount = results.getMetaData().getColumnCount();
        return columncount > 13 ? results.getInt(14) : 0;
    }

    private Date timestampToDate(Timestamp lastmodifiedTimestamp) {
        return lastmodifiedTimestamp != null ? Date.from(lastmodifiedTimestamp.toInstant()) : null;
    }

    private DataSource createOldalbumDbWithData(String dbname) throws Exception {
        var oldalbumDatasource = createDatasource(dbname);
        var oldalbumSchemaAndData = new OldAlbumDerbyTestDatabase();
        oldalbumSchemaAndData.activate();
        oldalbumSchemaAndData.prepare(oldalbumDatasource);
        return oldalbumDatasource;
    }

    private DataSource createOldalbumDbWithouthData(String dbname) throws Exception {
        var oldalbumDatasource = createDatasource(dbname);
        var oldalbumSchema = new OldAlbumLiquibase();
        try (var connection = oldalbumDatasource.getConnection()) {
            oldalbumSchema.createInitialSchema(connection);
        }

        try (var connection = oldalbumDatasource.getConnection()) {
            oldalbumSchema.updateSchema(connection);
        }

        return oldalbumDatasource;
    }

    private DataSource createDatasource(String dbname) throws Exception {
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + ";create=true");
        return derbyDataSourceFactory.createDataSource(properties);
    }

    private Path findTempdirAsTargetSubdir() throws Exception {
        try(var inputstream = getClass().getClassLoader().getResourceAsStream("properties-from-pom.properties")) {
            var properties = new Properties();
            properties.load(inputstream);
            var tempdir = Paths.get((String)properties.get("project-target-dir"), "temp");
            if (!Files.exists(tempdir)) {
                Files.createDirectory(tempdir);
            }

            return tempdir;
        }
    }
}
