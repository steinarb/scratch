/*
 * Copyright 2020 Steinar Bang
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

class OldAlbumLiquibaseTest {
    DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();

    @Test
    void testCreateSchema() throws Exception {
        Connection connection = createConnection();
        OldAlbumLiquibase oldAlbumLiquibase = new OldAlbumLiquibase();
        oldAlbumLiquibase.createInitialSchema(connection);
        addAlbumEntries(connection);
        assertAlbumEntries(connection);
    }

    private void assertAlbumEntries(Connection connection) throws Exception {
        assertAlbumEntry(connection, 1, "/album/", true, "Album", "This is an album", null, null);
        assertAlbumEntry(connection, 2, "/album/bilde01", false, "VFR at Arctic Circle", "This is the VFR up north", "https://www.bang.priv.no/sb/pics/moto/vfr96/acirc1.jpg", "https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc1.gif");
    }

    private void assertAlbumEntry(Connection connection, int id, String path, boolean album, String title, String description, String imageUrl, String thumbnailUrl) throws Exception {
        String sql = "select * from albumentries where albumentry_id = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try(ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    assertEquals(path, result.getString(2));
                    assertEquals(album, result.getBoolean(3));
                    assertEquals(title, result.getString(4));
                    assertEquals(description, result.getString(5));
                    assertEquals(imageUrl, result.getString(6));
                    assertEquals(thumbnailUrl, result.getString(7));
                } else {
                    fail(String.format("Didn't find albumentry with id=d", id));
                }
            }
        }
    }

    private void addAlbumEntries(Connection connection) throws Exception {
        addAlbumEntry(connection, "/album/", true, "Album", "This is an album", null, null);
        addAlbumEntry(connection, "/album/bilde01", false, "VFR at Arctic Circle", "This is the VFR up north", "https://www.bang.priv.no/sb/pics/moto/vfr96/acirc1.jpg", "https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc1.gif");
    }

    private void addAlbumEntry(Connection connection, String path, boolean album, String title, String description, String imageUrl, String thumbnailUrl) throws Exception {
        String sql = "insert into albumentries (localpath, album, title, description, imageurl, thumbnailurl) values (?, ?, ?, ?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, path);
            statement.setBoolean(2, album);
            statement.setString(3, title);
            statement.setString(4, description);
            statement.setString(5, imageUrl);
            statement.setString(6, thumbnailUrl);
            statement.executeUpdate();
        }
    }

    private Connection createConnection() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:oldalbum;create=true");
        DataSource dataSource = derbyDataSourceFactory.createDataSource(properties);
        return dataSource.getConnection();
    }

}
