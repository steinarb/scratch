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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.oldalbum.services.OldAlbumException;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class OldAlbumUrlInitDatabaseTest {
    private static final int EXPECTED_NUMBER_OF_ALBUMENTRIES = 21;
    DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();

    @Test
    void testInsertData() throws Exception {
        var sqlUrl = "https://gist.githubusercontent.com/steinarb/8a1de4e37f82d4d5eeb97778b0c8d459/raw/6cddf18f12e98d704e85af6264d81867f68a097c/dumproutes.sql";
        var environment = mock(Environment.class);
        when(environment.getEnv(anyString())).thenReturn(sqlUrl);
        var dumproutesSql = OldAlbumUrlInitDatabaseTest.class.getClassLoader().getResourceAsStream("dumproutes.sql");
        var connection = mock(HttpURLConnection.class);
        when(connection.getInputStream()).thenReturn(dumproutesSql);
        when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(connection.getContentType()).thenReturn("application/sql");
        var connectionFactory = mock(HttpConnectionFactory.class);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        var datasource = createDataSource("oldalbum_urldata");
        var logservice = new MockLogService();
        addSchemaToDatasource(datasource, logservice);
        var component = new OldAlbumUrlInitDatabase();
        component.setEnvironment(environment);
        component.setConnectionFactory(connectionFactory);
        component.setLogService(logservice);
        component.setDatasource(datasource);
        component.activate();
        assertDummyDataAsExpected(datasource);
    }

    @Test
    void testInsertDataWhenReturnedDataNotSql() throws Exception {
        var sqlUrl = "https://gist.githubusercontent.com/steinarb/8a1de4e37f82d4d5eeb97778b0c8d459/raw/6cddf18f12e98d704e85af6264d81867f68a097c/dumproutes.sql";
        var environment = mock(Environment.class);
        when(environment.getEnv(anyString())).thenReturn(sqlUrl);
        var dumproutesSql = OldAlbumUrlInitDatabaseTest.class.getClassLoader().getResourceAsStream("404.html");
        var connection = mock(HttpURLConnection.class);
        when(connection.getInputStream()).thenReturn(dumproutesSql);
        when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(connection.getContentType()).thenReturn("text/html");
        var connectionFactory = mock(HttpConnectionFactory.class);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        var datasource = createDataSource("oldalbum_urldata_not_html");
        var logservice = new MockLogService();
        addSchemaToDatasource(datasource, logservice);
        var component = new OldAlbumUrlInitDatabase();
        component.setEnvironment(environment);
        component.setConnectionFactory(connectionFactory);
        component.setLogService(logservice);
        component.setDatasource(datasource);
        assertThrows(OldAlbumException.class, () -> component.activate());
    }

    @Test
    void testInsertDataWhenDataNotFound() throws Exception {
        var sqlUrl = "https://gist.githubusercontent.com/steinarb/8a1de4e37f82d4d5eeb97778b0c8d459/raw/6cddf18f12e98d704e85af6264d81867f68a097c/dumproutes.sql";
        var environment = mock(Environment.class);
        when(environment.getEnv(anyString())).thenReturn(sqlUrl);
        var connection = mock(HttpURLConnection.class);
        var errorHtml = OldAlbumUrlInitDatabaseTest.class.getClassLoader().getResourceAsStream("404.html");
        when(connection.getInputStream()).thenReturn(errorHtml);
        when(connection.getContentType()).thenReturn("text/html");
        when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);
        var connectionFactory = mock(HttpConnectionFactory.class);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        var datasource = createDataSource("oldalbum_urldata");
        var logservice = new MockLogService();
        addSchemaToDatasource(datasource, logservice);
        var component = new OldAlbumUrlInitDatabase();
        component.setEnvironment(environment);
        component.setConnectionFactory(connectionFactory);
        component.setLogService(logservice);
        component.setDatasource(datasource);
        assertThrows(OldAlbumException.class, () -> component.activate());
    }

    @Test
    void testInsertDataWithInternalServerError() throws Exception {
        var sqlUrl = "https://gist.githubusercontent.com/steinarb/8a1de4e37f82d4d5eeb97778b0c8d459/raw/6cddf18f12e98d704e85af6264d81867f68a097c/dumproutes.sql";
        var environment = mock(Environment.class);
        when(environment.getEnv(anyString())).thenReturn(sqlUrl);
        var connection = mock(HttpURLConnection.class);
        var errorHtml = OldAlbumUrlInitDatabaseTest.class.getClassLoader().getResourceAsStream("500.html");
        when(connection.getInputStream()).thenReturn(errorHtml);
        when(connection.getContentType()).thenReturn("text/html");
        when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);
        var connectionFactory = mock(HttpConnectionFactory.class);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        var datasource = createDataSource("oldalbum_urldata");
        var logservice = new MockLogService();
        addSchemaToDatasource(datasource, logservice);
        var component = new OldAlbumUrlInitDatabase();
        component.setEnvironment(environment);
        component.setConnectionFactory(connectionFactory);
        component.setLogService(logservice);
        component.setDatasource(datasource);
        assertThrows(OldAlbumException.class, () -> component.activate());
    }

    @Test
    void testInsertDataWithLiquibaseException() throws Exception {
        var sqlUrl = "https://gist.githubusercontent.com/steinarb/8a1de4e37f82d4d5eeb97778b0c8d459/raw/6cddf18f12e98d704e85af6264d81867f68a097c/dumproutes.sql";
        var environment = mock(Environment.class);
        when(environment.getEnv(anyString())).thenReturn(sqlUrl);
        var dumproutesSql = OldAlbumUrlInitDatabaseTest.class.getClassLoader().getResourceAsStream("dumproutes.sql");
        var connection = mock(HttpURLConnection.class);
        when(connection.getInputStream()).thenReturn(dumproutesSql);
        var connectionFactory = mock(HttpConnectionFactory.class);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        var datasource = createDataSource("dbwithoutschema"); // An empty database that has no schema, will cause LiquibaseException when attempting to insert
        var logservice = new MockLogService();
        var component = new OldAlbumUrlInitDatabase();
        component.setEnvironment(environment);
        component.setConnectionFactory(connectionFactory);
        component.setLogService(logservice);
        component.setDatasource(datasource);
        assertThrows(OldAlbumException.class, () -> component.activate());
    }

    @Test
    void testInsertDataWithMalformedUrl() throws Exception {
        var sqlUrl = "xxx";
        var environment = mock(Environment.class);
        when(environment.getEnv(anyString())).thenReturn(sqlUrl);
        var datasource = createDataSource("dbwithoutschema"); // An empty database that has no schema, will cause LiquibaseException when attempting to insert
        var logservice = new MockLogService();
        var component = new OldAlbumUrlInitDatabase();
        component.setEnvironment(environment);
        component.setLogService(logservice);
        component.setDatasource(datasource);
        assertThrows(OldAlbumException.class, () -> component.activate());
    }

    @Test
    void testInsertDataWithUrlNotFound() throws Exception {
        var sqlUrl = "http://localhost/missing";
        var environment = mock(Environment.class);
        when(environment.getEnv(anyString())).thenReturn(sqlUrl);
        var datasource = createDataSource("dbwithoutschema"); // An empty database that has no schema, will cause LiquibaseException when attempting to insert
        var logservice = new MockLogService();
        var component = new OldAlbumUrlInitDatabase();
        component.setEnvironment(environment);
        component.setLogService(logservice);
        component.setDatasource(datasource);
        assertThrows(OldAlbumException.class, () -> component.activate());
    }

    @Test
    void testInsertDataWithNoInitialDataUrl() {
        var datasource = mock(DataSource.class);
        var logservice = new MockLogService();
        var component = new OldAlbumUrlInitDatabase();
        component.setLogService(logservice);
        component.setDatasource(datasource);
        assertThrows(OldAlbumException.class, () -> component.activate());
    }

    private void addSchemaToDatasource(DataSource datasource, MockLogService logservice) throws SQLException {
        var scheme = new OldAlbumScheme();
        scheme.setLogService(logservice);
        scheme.activate();
        scheme.prepare(datasource);
    }

    @Test
    void testIsNullOrEmpty() {
        assertTrue(OldAlbumUrlInitDatabase.nullOrEmpty(null));
        assertTrue(OldAlbumUrlInitDatabase.nullOrEmpty(""));
        assertFalse(OldAlbumUrlInitDatabase.nullOrEmpty("http://localhost"));
    }

    private void assertDummyDataAsExpected(DataSource datasource) throws Exception {
        var connection = datasource.getConnection();
        var countSql = "select count(*) from albumentries";
        try(var countStatement = connection.createStatement()) {
            try(var results = countStatement.executeQuery(countSql)) {
                if (results.next()) {
                    var numberOfRows = results.getInt(1);
                    assertEquals(EXPECTED_NUMBER_OF_ALBUMENTRIES, numberOfRows);
                } else {
                    fail("Unable to count the rows in albumentries");
                }
            }
        }
    }

    private DataSource createDataSource(String dbname) throws Exception {
        var properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + ";create=true");
        return derbyDataSourceFactory.createDataSource(properties);
    }

}
