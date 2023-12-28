/*
 * Copyright 2020-2022 Steinar Bang
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
package no.priv.bang.oldalbum.db.liquibase;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import liquibase.exception.LiquibaseException;

class OldAlbumLiquibaseTest {
    DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();

    @Test
    void testCreateAndUpdateSchema() throws Exception {
        var datasource = createDatasource("oldalbum");
        OldAlbumLiquibase oldAlbumLiquibase = new OldAlbumLiquibase();
        try(var connection = datasource.getConnection()) {
            oldAlbumLiquibase.createInitialSchema(connection);
        }
        try(var connection = datasource.getConnection()) {
            addAlbumEntries(connection);
        }
        try(var connection = datasource.getConnection()) {
            assertAlbumEntries(connection);
            assertAlbumEntriesDontHaveRequireLoginFlag(connection);
            assertAlbumEntriesDontHaveGroupByYearFlag(connection);
        }
        try(var connection = datasource.getConnection()) {
            oldAlbumLiquibase.updateSchema(connection);
        }
        try(var connection = datasource.getConnection()) {
            assertAlbumEntries(connection);
            assertAlbumEntriesHasRequireLoginFlag(connection);
            assertAlbumEntriesHasGroupByYearFlag(connection);
        }
    }

    @Test
    void testCreateSchemaWithDatabaseFailure() throws Exception {
        var realdb = createDatasource("oldalbum1");
        var connection = spy(realdb.getConnection());
        // Wrapping Connection in a spy() makes it throw SQLException on setAutoCommit()

        OldAlbumLiquibase oldAlbumLiquibase = new OldAlbumLiquibase();
        var e = assertThrows(
            LiquibaseException.class,
            () -> oldAlbumLiquibase.createInitialSchema(connection));
        assertThat(e.getMessage()).startsWith("java.sql.SQLException: Cannot set Autocommit On when in a nested connection");
    }

    @Test
    void testCreateSchemaWithAuthoClosableCloseFailure() throws Exception {
        var realdb = createDatasource("oldalbum2");
        var connection = spy(realdb.getConnection());
        doNothing().when(connection).setAutoCommit(anyBoolean());
        doThrow(Exception.class).when(connection).close();

        OldAlbumLiquibase oldAlbumLiquibase = new OldAlbumLiquibase();
        var e = assertThrows(
            LiquibaseException.class,
            () -> oldAlbumLiquibase.createInitialSchema(connection));
        assertThat(e.getMessage()).startsWith("Error closing resource");
    }

    private void assertAlbumEntries(Connection connection) throws Exception {
        assertAlbumEntry(connection, 1, 0, "/album/", true, "Album", "This is an album", null, null, 1, null, null, 0);
        assertAlbumEntry(connection, 2, 1, "/album/bilde01", false, "VFR at Arctic Circle", "This is the VFR up north", "https://www.bang.priv.no/sb/pics/moto/vfr96/acirc1.jpg", "https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc1.gif", 2, new Date(800275785000L), "image/jpeg", 128186);
    }

    private void assertAlbumEntry(Connection connection, int id, int parent, String path, boolean album, String title, String description, String imageUrl, String thumbnailUrl, int sort, Date lastmodified, String contenttype, int size) throws Exception {
        String sql = "select * from albumentries where albumentry_id = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try(ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    assertEquals(parent, result.getInt(2));
                    assertEquals(path, result.getString(3));
                    assertEquals(album, result.getBoolean(4));
                    assertEquals(title, result.getString(5));
                    assertEquals(description, result.getString(6));
                    assertEquals(imageUrl, result.getString(7));
                    assertEquals(thumbnailUrl, result.getString(8));
                    assertEquals(sort, result.getInt(9));
                    assertEquals(lastmodified, result.getTimestamp(10) != null ? Date.from(result.getTimestamp(10).toInstant()) : null);
                    assertEquals(contenttype, result.getString(11));
                    assertEquals(size, result.getInt(12));
                } else {
                    fail(String.format("Didn't find albumentry with id=d", id));
                }
            }
        }
    }

    private void assertAlbumEntriesDontHaveRequireLoginFlag(Connection connection) throws Exception {
        assertAlbumEntryDoesntHaveRequireLoginFlag(connection, 1);
        assertAlbumEntryDoesntHaveRequireLoginFlag(connection, 2);
    }

    private void assertAlbumEntriesDontHaveGroupByYearFlag(Connection connection) throws Exception {
        assertAlbumEntryDoesntHaveGroupByYearFlag(connection, 1);
        assertAlbumEntryDoesntHaveGroupByYearFlag(connection, 2);
    }

    private void assertAlbumEntryDoesntHaveRequireLoginFlag(Connection connection, int id) throws Exception {
        String sql = "select * from albumentries where albumentry_id = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try(ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    assertThrows(SQLException.class, () -> result.getBoolean("require_login"));
                } else {
                    fail(String.format("Didn't find albumentry with id=d", id));
                }
            }
        }
    }

    private void assertAlbumEntryDoesntHaveGroupByYearFlag(Connection connection, int id) throws Exception {
        String sql = "select * from albumentries where albumentry_id = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try(ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    assertThrows(SQLException.class, () -> result.getBoolean("group_by_year"));
                } else {
                    fail(String.format("Didn't find albumentry with id=d", id));
                }
            }
        }
    }

    private void assertAlbumEntriesHasRequireLoginFlag(Connection connection) throws Exception {
        assertAlbumEntryHasRequireLoginFlag(connection, 1, false);
        assertAlbumEntryHasRequireLoginFlag(connection, 2, false);
    }

    private void assertAlbumEntriesHasGroupByYearFlag(Connection connection) throws Exception {
        assertAlbumEntryHasGroupByYearFlag(connection, 1, false);
        assertAlbumEntryHasGroupByYearFlag(connection, 2, false);
    }

    private void assertAlbumEntryHasRequireLoginFlag(Connection connection, int id, boolean requireLogin) throws Exception {
        String sql = "select * from albumentries where albumentry_id = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try(ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    assertEquals(requireLogin, result.getBoolean("require_login"));
                } else {
                    fail(String.format("Didn't find albumentry with id=d", id));
                }
            }
        }
    }

    private void assertAlbumEntryHasGroupByYearFlag(Connection connection, int id, boolean requireLogin) throws Exception {
        String sql = "select * from albumentries where albumentry_id = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try(ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    assertEquals(requireLogin, result.getBoolean("group_by_year"));
                } else {
                    fail(String.format("Didn't find albumentry with id=d", id));
                }
            }
        }
    }

    private void addAlbumEntries(Connection connection) throws Exception {
        addAlbumEntry(connection, 0, "/album/", true, "Album", "This is an album", null, null, 1, null, null, 0);
        addAlbumEntry(connection, 1, "/album/bilde01", false, "VFR at Arctic Circle", "This is the VFR up north", "https://www.bang.priv.no/sb/pics/moto/vfr96/acirc1.jpg", "https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc1.gif", 2, new Date(800275785000L), "image/jpeg", 128186);
    }

    private void addAlbumEntry(Connection connection, int parent, String path, boolean album, String title, String description, String imageUrl, String thumbnailUrl, int sort, Date lastmodified, String contenttype, int size) throws Exception {
        String sql = "insert into albumentries (parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, parent);
            statement.setString(2, path);
            statement.setBoolean(3, album);
            statement.setString(4, title);
            statement.setString(5, description);
            statement.setString(6, imageUrl);
            statement.setString(7, thumbnailUrl);
            statement.setInt(8, sort);
            statement.setTimestamp(9, lastmodified != null ? Timestamp.from(Instant.ofEpochMilli(lastmodified.getTime())) : null);
            statement.setString(10, contenttype);
            statement.setInt(11, size);
            statement.executeUpdate();
        }
    }

    private DataSource createDatasource(String dbname) throws Exception {
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + ";create=true");
        return derbyDataSourceFactory.createDataSource(properties);
    }
}
