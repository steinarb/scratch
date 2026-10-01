package no.priv.bang.jdbc.sqldumper;
/*
 * Copyright 2023-2026 Steinar Bang
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
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.assertj.db.type.AssertDbConnectionFactory;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import com.mockrunner.mock.jdbc.MockResultSet;

import liquibase.Scope;
import liquibase.Scope.ScopedRunner;
import liquibase.changelog.ChangeLogParameters;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DatabaseChangelogCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.sdk.resource.MockResourceAccessor;
import no.priv.bang.oldalbum.db.liquibase.OldAlbumLiquibase;
import no.priv.bang.oldalbum.db.liquibase.test.OldAlbumDerbyTestDatabase;

class ResultSetSqlDumperTest {
    DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();

    @Test
    void testDumpResultSetAsSqlOnOldalbum() throws Exception {
        var changesetId = "sb:album_paths";
        var sqldumper = new ResultSetSqlDumper();
        var oldalbumDatasource = createOldalbumDbWithData("oldalbum");
        var writer = new StringWriter();
        var sql = "select * from albumentries";
        try(var connection = oldalbumDatasource.getConnection()) {
            try(var statement = connection.createStatement()) {
                try(var resultset = statement.executeQuery(sql)) {
                    sqldumper.dumpResultSetAsSql(changesetId, resultset, writer);
                }
            }
        }

        var dumpedsql = writer.toString();
        assertThat(dumpedsql)
            .startsWith("--liquibase formatted sql")
            .contains("--changeset sb:saved_albumentries")
            .contains("insert into ALBUMENTRIES (ALBUMENTRY_ID, PARENT, LOCALPATH, ALBUM, TITLE, DESCRIPTION, IMAGEURL, THUMBNAILURL, SORT, LASTMODIFIED, CONTENTTYPE, CONTENTLENGTH, REQUIRE_LOGIN, GROUP_BY_YEAR) values")
            .contains("1, 0, '/', true, 'Picture archive', '', '', '', 0, null, null, null")
            .contains("11, 4, '/moto/vfr96/acirc3', false, '', 'My VFR 750F at the arctic circle.', 'https://www.bang.priv.no/sb/pics/moto/vfr96/acirc3.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc3.gif', 3, '1996-08-06 18:28:58.0', 'image/jpeg', 57732");

        // Use dumped SQL to populate an empty database and compare with original
        var restoredOldalbumDatasource = createOldalbumDbWithouthData("oldalbum2");
        var restoredOldalbumAssertjConnection = AssertDbConnectionFactory.of(restoredOldalbumDatasource).create();
        var albumentriesBeforeRestore = restoredOldalbumAssertjConnection.table("albumentries").build();
        assertThat(albumentriesBeforeRestore).exists().isEmpty();
        setDatabaseContentAsLiquibaseChangelog(restoredOldalbumDatasource, dumpedsql);
        var albumentriesAfterRestore = restoredOldalbumAssertjConnection.table("albumentries").build();
        assertThat(albumentriesAfterRestore).exists().hasNumberOfRowsGreaterThan(0);
        var originalOldalbumAssertjConnection = AssertDbConnectionFactory.of(oldalbumDatasource).create();
        var originalAlbumEntries = originalOldalbumAssertjConnection.table("albumentries").build();
        assertThat(albumentriesAfterRestore.getRowsList())
            .usingRecursiveComparison()
            .isEqualTo(originalAlbumEntries.getRowsList());
    }

    @Test
    void testDumpResultSetAsSqlWithSqlExceptionThrown() throws Exception {
        var sqldumper = new ResultSetSqlDumper();
        var resultset = mock(ResultSet.class);
        when(resultset.getMetaData()).thenThrow(SQLException.class);
        var nullwriter = Writer.nullWriter();
        var e = assertThrows(ResultsetSqlDumperException.class, () -> { sqldumper.dumpResultSetAsSql("id", resultset, nullwriter);});
        assertThat(e.getMessage()).startsWith("Error dumping JDBC ResultSet as SQL insert statements");
    }

    @SuppressWarnings("removal")
    @Test
    void testDumpResultSetAsSqlToOutputStreamOnOldalbum() throws Exception {
        var changesetId = "sb:album_paths";
        var sqldumper = new ResultSetSqlDumper();
        var oldalbumDatasource = createOldalbumDbWithData("oldalbum3");
        var tempfile = Files.createTempFile(findTempdirAsTargetSubdir(), "oldalbum", "sql");
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
            .contains("insert into ALBUMENTRIES (ALBUMENTRY_ID, PARENT, LOCALPATH, ALBUM, TITLE, DESCRIPTION, IMAGEURL, THUMBNAILURL, SORT, LASTMODIFIED, CONTENTTYPE, CONTENTLENGTH, REQUIRE_LOGIN, GROUP_BY_YEAR) values")
            .contains("1, 0, '/', true, 'Picture archive', '', '', '', 0, null, null, null")
            .contains("11, 4, '/moto/vfr96/acirc3', false, '', 'My VFR 750F at the arctic circle.', 'https://www.bang.priv.no/sb/pics/moto/vfr96/acirc3.jpg', 'https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc3.gif', 3, '1996-08-06 18:28:58.0', 'image/jpeg', 57732");

        // Use dumped SQL to populate an empty database and compare with original
        var restoredOldalbumDatasource = createOldalbumDbWithouthData("oldalbum4");
        var restoredOldalbumAssertjConnection = AssertDbConnectionFactory.of(restoredOldalbumDatasource).create();
        var albumentriesBeforeRestore = restoredOldalbumAssertjConnection.table("albumentries").build();
        assertThat(albumentriesBeforeRestore).exists().isEmpty();
        setDatabaseContentAsLiquibaseChangelog(restoredOldalbumDatasource, dumpedsql);
        var albumentriesAfterRestore = restoredOldalbumAssertjConnection.table("albumentries").build();
        assertThat(albumentriesAfterRestore).exists().hasNumberOfRowsGreaterThan(0);
        var originalOldalbumAssertjConnection = AssertDbConnectionFactory.of(oldalbumDatasource).create();
        var originalAlbumEntries = originalOldalbumAssertjConnection.table("albumentries").build();
        assertThat(albumentriesAfterRestore.getRowsList())
            .usingRecursiveComparison()
            .isEqualTo(originalAlbumEntries.getRowsList());
    }

    @Test
    void testDumpResultSetAsCsvOnOldalbum() throws Exception {
        var sqldumper = new ResultSetSqlDumper();
        var oldalbumDatasource = createOldalbumDbWithData("oldalbum1");
        var writer = new StringWriter();
        var sql = "select * from albumentries";
        try(var connection = oldalbumDatasource.getConnection()) {
            try(var statement = connection.createStatement()) {
                try(var resultset = statement.executeQuery(sql)) {
                    sqldumper.dumpResultSetAsCsv(resultset, writer);
                }
            }
        }

        assertThat(writer.toString())
            .startsWith("ALBUMENTRY_ID,PARENT,LOCALPATH,ALBUM,TITLE,DESCRIPTION,IMAGEURL,THUMBNAILURL,SORT,LASTMODIFIED,CONTENTTYPE,CONTENTLENGTH,REQUIRE_LOGIN,GROUP_BY_YEAR")
            .contains("1,0,\"/\",1,\"Picture archive\",\"\",\"\",\"\",0,,,")
            .contains("11,4,\"/moto/vfr96/acirc3\",0,\"\",\"My VFR 750F at the arctic circle.\",\"https://www.bang.priv.no/sb/pics/moto/vfr96/acirc3.jpg\",\"https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc3.gif\",3,1996-08-06 18:28:58.0,\"image/jpeg\",57732");
    }

    @Test
    void testDumpResultSetAsCsvWithSqlExceptionThrown() throws Exception {
        var sqldumper = new ResultSetSqlDumper();
        var resultset = mock(ResultSet.class);
        when(resultset.getMetaData()).thenThrow(SQLException.class);
        var nullWriter = Writer.nullWriter();
        var e = assertThrows(ResultsetSqlDumperException.class, () -> { sqldumper.dumpResultSetAsCsv(resultset, nullWriter); });
        assertThat(e.getMessage()).startsWith("Error dumping JDBC ResultSet as CSV file");
    }

    @Test
    void testDumpResultSetAsJsonOnOldalbum() throws Exception {
        var sqldumper = new ResultSetSqlDumper();
        var oldalbumDatasource = createOldalbumDbWithData("oldalbum1");
        var writer = new StringWriter();
        var sql = "select * from albumentries";
        try(var connection = oldalbumDatasource.getConnection()) {
            try(var statement = connection.createStatement()) {
                try(var resultset = statement.executeQuery(sql)) {
                    sqldumper.dumpResultSetAsJson(resultset, writer);
                }
            }
        }

        assertThat(writer.toString())
            .startsWith("[{ \"albumentryId\": 1, \"parent\": 0, \"localpath\": \"/\", \"album\": true, \"title\": \"Picture archive\", \"description\": \"\", \"imageurl\": \"\", \"thumbnailurl\": \"\", \"sort\": 0, \"lastmodified\": null, \"contenttype\": null, \"contentlength\": null, \"requireLogin\": false, \"groupByYear\": false }")
            .contains(" { \"albumentryId\": 11, \"parent\": 4, \"localpath\": \"/moto/vfr96/acirc3\", \"album\": false, \"title\": \"\", \"description\": \"My VFR 750F at the arctic circle.\", \"imageurl\": \"https://www.bang.priv.no/sb/pics/moto/vfr96/acirc3.jpg\", \"thumbnailurl\": \"https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc3.gif\", \"sort\": 3, \"lastmodified\": \"1996-08-06 18:28:58.0\", \"contenttype\": \"image/jpeg\", \"contentlength\": 57732, \"requireLogin\": false, \"groupByYear\": null }")
            .endsWith(" }]" + System.lineSeparator());
        assertThatJson(writer.toString())
            .isArray()
            .hasSize(26);
    }

    @Test
    void testDumpResultSetAsJsonWithEmptyResultSet() {
        var sqldumper = new ResultSetSqlDumper();
        var resultset = new MockResultSet("dummy");
        var writer = new StringWriter();
        sqldumper.dumpResultSetAsJson(resultset, writer);
        assertThatJson(writer.toString())
            .isArray()
            .hasSize(0);
    }

    @Test
    void testDumpResultSetAsJsonWithSqlExceptionThrown() throws Exception {
        var sqldumper = new ResultSetSqlDumper();
        var resultset = mock(ResultSet.class);
        when(resultset.getMetaData()).thenThrow(SQLException.class);
        var nullWriter = Writer.nullWriter();
        var e = assertThrows(ResultsetSqlDumperException.class, () -> { sqldumper.dumpResultSetAsJson(resultset, nullWriter); });
        assertThat(e.getMessage()).startsWith("Error dumping JDBC ResultSet as JSON file");
    }

    @Test
    void testPrettyPrintResultSet() throws Exception {
        var sqldumper = new ResultSetSqlDumper();
        var oldalbumDatasource = createOldalbumDbWithData("oldalbum1");
        String prettyPrintedResultSet = null;

        var sql = "select * from albumentries";
        try(var connection = oldalbumDatasource.getConnection()) {
            try(var statement = connection.createStatement()) {
                try(var resultset = statement.executeQuery(sql)) {
                    prettyPrintedResultSet = sqldumper.prettyPrintResultSet(resultset);
                }
            }
        }

        assertThat(prettyPrintedResultSet)
            .startsWith("[ ALBUMENTRY_ID=1 PARENT=0 LOCALPATH=")
            .endsWith(System.lineSeparator());
    }

    @Test
    void testPrettyPrintResultSetWithSqlExceptionThrown() throws Exception {
        var sqldumper = new ResultSetSqlDumper();
        var resultset = mock(ResultSet.class);
        when(resultset.getMetaData()).thenThrow(SQLException.class);
        var e = assertThrows(ResultsetSqlDumperException.class, () -> sqldumper.prettyPrintResultSet(resultset));
        assertThat(e.getMessage()).startsWith("Error pretty printing JDBC ResultSet");
    }

    @Test
    void testPrettyPrintResultSetRow() throws Exception {
        var sqldumper = new ResultSetSqlDumper();
        var oldalbumDatasource = createOldalbumDbWithData("oldalbum1");
        String prettyPrintedResultSetRow = null;

        var sql = "select * from albumentries";
        try(var connection = oldalbumDatasource.getConnection()) {
            try(var statement = connection.createStatement()) {
                try(var resultset = statement.executeQuery(sql)) {
                    resultset.next();
                    prettyPrintedResultSetRow = sqldumper.prettyPrintResultSetRow(resultset);
                }
            }
        }

        assertThat(prettyPrintedResultSetRow)
            .startsWith("[ ALBUMENTRY_ID=1 PARENT=0 LOCALPATH=")
            .endsWith(" ]");
    }

    @Test
    void testPrettyPrintResultSetRowWithSqlExceptionThrown() throws Exception {
        var sqldumper = new ResultSetSqlDumper();
        var resultset = mock(ResultSet.class);
        when(resultset.getMetaData()).thenThrow(SQLException.class);
        var e = assertThrows(ResultsetSqlDumperException.class, () -> sqldumper.prettyPrintResultSetRow(resultset));
        assertThat(e.getMessage()).startsWith("Error pretty printing JDBC ResultSet row");
    }

    @Test
    void testPrettyPrintSqlQuery() throws Exception {
        var sqldumper = new ResultSetSqlDumper();
        var oldalbumDatasource = createOldalbumDbWithData("oldalbum1");

        var sql = "select * from albumentries";
        String prettyPrintedResultSet = sqldumper.prettyPrintSqlQuery(oldalbumDatasource, sql);

        assertThat(prettyPrintedResultSet)
            .startsWith("[ ALBUMENTRY_ID=1 PARENT=0 LOCALPATH=");
    }

    @Test
    void testPrettyPrintSqlQueryWithSqlExceptionThrown() throws Exception {
        var sqldumper = new ResultSetSqlDumper();
        var datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenThrow(SQLException.class);
        var e = assertThrows(ResultsetSqlDumperException.class, () -> sqldumper.prettyPrintSqlQuery(datasource, "select * from dummy"));
        assertThat(e.getMessage()).startsWith("Error pretty printing SQL query result");
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
                    assertThat(columnames).hasSize(14);
                    Map<String, Integer> columntypes = sqldumper.findColumntypes(resultset);
                    assertThat(columntypes).hasSize(columnames.size());
                    String tablename = sqldumper.findTableName(resultset);
                    assertEquals("ALBUMENTRIES", tablename);
                }
            }
        }
    }

    @Test
    void testColumnameToJsonPropertyName() {
        assertThat(ResultSetSqlDumper.columnameToJsonPropertyName("ALBUMENTRY_ID")).isEqualTo("albumentryId");
        assertThat(ResultSetSqlDumper.columnameToJsonPropertyName("albumentry_id")).isEqualTo("albumentryId");
        assertThat(ResultSetSqlDumper.columnameToJsonPropertyName("LOCALPATH")).isEqualTo("localpath");
        assertThat(ResultSetSqlDumper.columnameToJsonPropertyName("localpath")).isEqualTo("localpath");
        assertThat(ResultSetSqlDumper.columnameToJsonPropertyName("SOME_COLUMN_VALUE")).isEqualTo("someColumnValue");
        assertThat(ResultSetSqlDumper.columnameToJsonPropertyName("some_column_value")).isEqualTo("someColumnValue");
    }

    @Test
    void testEscapeJsonString() {
        var testStringWithNamedQuotes = """
        Will quotes be "quoted"?
         """;
        assertThat(ResultSetSqlDumper.escapeJsonString(testStringWithNamedQuotes)).startsWith("Will quotes be \\\"quoted\\\"?");
        var testStringWithWindowsPath = """
          C:\\backslash\\separator
          """;
        assertThat(ResultSetSqlDumper.escapeJsonString(testStringWithWindowsPath)).startsWith("C:\\\\backslash\\\\separator");
        var testStringWithLineShift = """
First line
Second line
        """;
        assertThat(ResultSetSqlDumper.escapeJsonString(testStringWithLineShift)).startsWith("First line\\nSecond line");
    }

    @Test
    void testCsvQuotedStringOrNull() throws Exception {
        var dumper = new ResultSetSqlDumper();
        var resultset = mock(ResultSet.class);
        when(resultset.getString(anyString())).thenReturn("Text not needing quote expansion");
        assertThat(dumper.csvQuotedStringOrNull(resultset, "dummy")).isEqualTo("\"Text not needing quote expansion\"");
        when(resultset.getString(anyString())).thenReturn("Text with \"quotes\" that must be tripled");
        assertThat(dumper.csvQuotedStringOrNull(resultset, "dummy")).isEqualTo("\"Text with \"\"\"quotes\"\"\" that must be tripled\"");
    }

    private void setDatabaseContentAsLiquibaseChangelog(DataSource datasource, String contentLiquibaseChangelog) throws Exception {
        var contentByFileName = new HashMap<String, String>();
        contentByFileName.put("dumproutes.sql", contentLiquibaseChangelog);
        try(var connection = datasource.getConnection()) {
            try(var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection))) {
                Map<String, Object> scopeObjects = Map.of(
                    Scope.Attr.database.name(), database,
                    Scope.Attr.resourceAccessor.name(), new MockResourceAccessor(contentByFileName));

                Scope.child(scopeObjects, (ScopedRunner<?>) () -> new CommandScope("update")
                    .addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, database)
                    .addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, "dumproutes.sql")
                    .addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, new ChangeLogParameters(database))
                    .execute());
            }
        }
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
        var properties = new Properties();
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
