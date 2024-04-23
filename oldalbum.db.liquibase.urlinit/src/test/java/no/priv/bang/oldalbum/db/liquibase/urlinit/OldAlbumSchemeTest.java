/*
 * Copyright 2020-2024 Steinar Bang
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
package no.priv.bang.oldalbum.db.liquibase.urlinit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.karaf.derby.embedded.EmbeddedDerbyDataSourceFactory;
import no.priv.bang.oldalbum.services.OldAlbumException;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class OldAlbumSchemeTest {
    DataSourceFactory derbyDataSourceFactory = new EmbeddedDerbyDataSourceFactory();

    @Test
    void testPrepare() throws Exception {
        var sqlUrl = "https://gist.githubusercontent.com/steinarb/8a1de4e37f82d4d5eeb97778b0c8d459/raw/6cddf18f12e98d704e85af6264d81867f68a097c/dumproutes.sql";
        var environment = mock(Environment.class);
        when(environment.getEnv(anyString())).thenReturn(sqlUrl);
        var datasource = createDataSource("oldalbum");
        var logservice = new MockLogService();
        var hook = new OldAlbumScheme();
        hook.setLogService(logservice);
        hook.activate();
        hook.prepare(datasource);
        addAlbumEntries(datasource);
        assertAlbumEntries(datasource);
    }

    @Test
    void testPrepareWhenSQLExceptionIsThrown() throws Exception {
        var sqlUrl = "https://gist.githubusercontent.com/steinarb/8a1de4e37f82d4d5eeb97778b0c8d459/raw/6cddf18f12e98d704e85af6264d81867f68a097c/dumproutes.sql";
        var environment = mock(Environment.class);
        when(environment.getEnv(anyString())).thenReturn(sqlUrl);
        var datasource = spy(createDataSource("oldalbum1"));
        when(datasource.getConnection()).thenCallRealMethod().thenThrow(SQLException.class);
        var logservice = new MockLogService();
        var hook = new OldAlbumScheme();
        hook.setLogService(logservice);
        hook.activate();

        var e = assertThrows(OldAlbumException.class, () -> hook.prepare(datasource));
        assertThat(e.getMessage()).startsWith("Error updating schema for oldalbum database initialized from URL");
    }

    @Test
    void testCreateInitialSchemaWithLiquibaseException() throws Exception {
        var datasource = mock(DataSource.class);
        var connection = mock(Connection.class);
        when(connection.getMetaData()).thenThrow(SQLException.class);
        when(datasource.getConnection()).thenReturn(connection);
        var logservice = new MockLogService();
        var hook = new OldAlbumScheme();
        hook.setLogService(logservice);
        hook.activate();
        assertThrows(OldAlbumException.class, () -> hook.createInitialSchema(datasource));
    }

    private DataSource createDataSource(String dbname) throws Exception {
        var properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + ";create=true");
        return derbyDataSourceFactory.createDataSource(properties);
    }

    private void assertAlbumEntries(DataSource datasource) throws Exception {
        try (var connection = datasource.getConnection()) {
            assertAlbumEntry(connection, 1, 0, "/album/", true, "Album", "This is an album", null, null, 1, null, null, 0);
            assertAlbumEntry(connection, 2, 1, "/album/bilde01", false, "VFR at Arctic Circle", "This is the VFR up north", "https://www.bang.priv.no/sb/pics/moto/vfr96/acirc1.jpg", "https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc1.gif", 2, new Date(800275785000L), "image/jpeg", 128186);
        }
    }

    private void assertAlbumEntry(Connection connection, int id, int parent, String path, boolean album, String title, String description, String imageUrl, String thumbnailUrl, int sort, Date lastmodified, String contenttype, int size) throws Exception {
        var sql = "select * from albumentries where albumentry_id = ?";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try(var result = statement.executeQuery()) {
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

    private void addAlbumEntries(DataSource datasource) throws Exception {
        try (var connection = datasource.getConnection()) {
            addAlbumEntry(connection, 0, "/album/", true, "Album", "This is an album", null, null, 1, null, null, 0);
            addAlbumEntry(connection, 1, "/album/bilde01", false, "VFR at Arctic Circle", "This is the VFR up north", "https://www.bang.priv.no/sb/pics/moto/vfr96/acirc1.jpg", "https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc1.gif", 2, new Date(800275785000L), "image/jpeg", 128186);
        }
    }

    private void addAlbumEntry(Connection connection, int parent, String path, boolean album, String title, String description, String imageUrl, String thumbnailUrl, int sort, Date lastmodified, String contenttype, int size) throws Exception {
        var sql = "insert into albumentries (parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(var statement = connection.prepareStatement(sql)) {
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

}
