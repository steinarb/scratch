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
package no.priv.bang.oldalbum.db.liquibase.urlinit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class OldAlbumUrlInitDatabaseTest {
    private static final int EXPECTED_NUMBER_OF_ALBUMENTRIES = 21;
    DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();

    @Test
    void testInsertData() throws Exception {
        String sqlUrl = "https://gist.githubusercontent.com/steinarb/8a1de4e37f82d4d5eeb97778b0c8d459/raw/6cddf18f12e98d704e85af6264d81867f68a097c/dumproutes.sql";
        Environment environment = mock(Environment.class);
        when(environment.getEnv(anyString())).thenReturn(sqlUrl);
        InputStream dumproutesSql = OldAlbumUrlInitDatabaseTest.class.getClassLoader().getResourceAsStream("dumproutes.sql");
        HttpURLConnection connection = mock(HttpURLConnection.class);
        when(connection.getInputStream()).thenReturn(dumproutesSql);
        when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(connection.getContentType()).thenReturn("application/sql");
        HttpConnectionFactory connectionFactory = mock(HttpConnectionFactory.class);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        DataSource datasource = createDataSource("oldalbum_urldata");
        MockLogService logservice = new MockLogService();
        addSchemaToDatasource(datasource, logservice);
        OldAlbumUrlInitDatabase component = new OldAlbumUrlInitDatabase();
        component.setEnvironment(environment);
        component.setConnectionFactory(connectionFactory);
        component.setLogService(logservice);
        component.setDatasource(datasource);
        component.activate();
        assertDummyDataAsExpected(datasource);
    }

    @Test
    void testInsertDataWhenReturnedDataNotSql() throws Exception {
        String sqlUrl = "https://gist.githubusercontent.com/steinarb/8a1de4e37f82d4d5eeb97778b0c8d459/raw/6cddf18f12e98d704e85af6264d81867f68a097c/dumproutes.sql";
        Environment environment = mock(Environment.class);
        when(environment.getEnv(anyString())).thenReturn(sqlUrl);
        InputStream dumproutesSql = OldAlbumUrlInitDatabaseTest.class.getClassLoader().getResourceAsStream("404.html");
        HttpURLConnection connection = mock(HttpURLConnection.class);
        when(connection.getInputStream()).thenReturn(dumproutesSql);
        when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(connection.getContentType()).thenReturn("text/html");
        HttpConnectionFactory connectionFactory = mock(HttpConnectionFactory.class);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        DataSource datasource = createDataSource("oldalbum_urldata_not_html");
        MockLogService logservice = new MockLogService();
        addSchemaToDatasource(datasource, logservice);
        OldAlbumUrlInitDatabase component = new OldAlbumUrlInitDatabase();
        component.setEnvironment(environment);
        component.setConnectionFactory(connectionFactory);
        component.setLogService(logservice);
        component.setDatasource(datasource);
        component.activate();
        assertEquals(1, logservice.getLogmessages().size());
    }

    @Test
    void testInsertDataWhenDataNotFound() throws Exception {
        String sqlUrl = "https://gist.githubusercontent.com/steinarb/8a1de4e37f82d4d5eeb97778b0c8d459/raw/6cddf18f12e98d704e85af6264d81867f68a097c/dumproutes.sql";
        Environment environment = mock(Environment.class);
        when(environment.getEnv(anyString())).thenReturn(sqlUrl);
        HttpURLConnection connection = mock(HttpURLConnection.class);
        InputStream errorHtml = OldAlbumUrlInitDatabaseTest.class.getClassLoader().getResourceAsStream("404.html");
        when(connection.getInputStream()).thenReturn(errorHtml);
        when(connection.getContentType()).thenReturn("text/html");
        when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);
        HttpConnectionFactory connectionFactory = mock(HttpConnectionFactory.class);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        DataSource datasource = createDataSource("oldalbum_urldata");
        MockLogService logservice = new MockLogService();
        addSchemaToDatasource(datasource, logservice);
        OldAlbumUrlInitDatabase component = new OldAlbumUrlInitDatabase();
        component.setEnvironment(environment);
        component.setConnectionFactory(connectionFactory);
        component.setLogService(logservice);
        component.setDatasource(datasource);
        component.activate();
        assertEquals(1, logservice.getLogmessages().size());
    }

    @Test
    void testInsertDataWithInternalServerError() throws Exception {
        String sqlUrl = "https://gist.githubusercontent.com/steinarb/8a1de4e37f82d4d5eeb97778b0c8d459/raw/6cddf18f12e98d704e85af6264d81867f68a097c/dumproutes.sql";
        Environment environment = mock(Environment.class);
        when(environment.getEnv(anyString())).thenReturn(sqlUrl);
        HttpURLConnection connection = mock(HttpURLConnection.class);
        InputStream errorHtml = OldAlbumUrlInitDatabaseTest.class.getClassLoader().getResourceAsStream("500.html");
        when(connection.getInputStream()).thenReturn(errorHtml);
        when(connection.getContentType()).thenReturn("text/html");
        when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_INTERNAL_ERROR);
        HttpConnectionFactory connectionFactory = mock(HttpConnectionFactory.class);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        DataSource datasource = createDataSource("oldalbum_urldata");
        MockLogService logservice = new MockLogService();
        addSchemaToDatasource(datasource, logservice);
        OldAlbumUrlInitDatabase component = new OldAlbumUrlInitDatabase();
        component.setEnvironment(environment);
        component.setConnectionFactory(connectionFactory);
        component.setLogService(logservice);
        component.setDatasource(datasource);
        component.activate();
        assertEquals(1, logservice.getLogmessages().size());
    }

    @Test
    void testInsertDataWithLiquibaseException() throws Exception {
        String sqlUrl = "https://gist.githubusercontent.com/steinarb/8a1de4e37f82d4d5eeb97778b0c8d459/raw/6cddf18f12e98d704e85af6264d81867f68a097c/dumproutes.sql";
        Environment environment = mock(Environment.class);
        when(environment.getEnv(anyString())).thenReturn(sqlUrl);
        InputStream dumproutesSql = OldAlbumUrlInitDatabaseTest.class.getClassLoader().getResourceAsStream("dumproutes.sql");
        HttpURLConnection connection = mock(HttpURLConnection.class);
        when(connection.getInputStream()).thenReturn(dumproutesSql);
        HttpConnectionFactory connectionFactory = mock(HttpConnectionFactory.class);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        DataSource datasource = createDataSource("dbwithoutschema"); // An empty database that has no schema, will cause LiquibaseException when attempting to insert
        MockLogService logservice = new MockLogService();
        OldAlbumUrlInitDatabase component = new OldAlbumUrlInitDatabase();
        component.setEnvironment(environment);
        component.setConnectionFactory(connectionFactory);
        component.setLogService(logservice);
        component.setDatasource(datasource);
        component.activate();
        assertEquals(1, logservice.getLogmessages().size());
    }

    @Test
    void testInsertDataWithMalformedUrl() throws Exception {
        String sqlUrl = "xxx";
        Environment environment = mock(Environment.class);
        when(environment.getEnv(anyString())).thenReturn(sqlUrl);
        DataSource datasource = createDataSource("dbwithoutschema"); // An empty database that has no schema, will cause LiquibaseException when attempting to insert
        MockLogService logservice = new MockLogService();
        OldAlbumUrlInitDatabase component = new OldAlbumUrlInitDatabase();
        component.setEnvironment(environment);
        component.setLogService(logservice);
        component.setDatasource(datasource);
        component.activate();
        assertEquals(1, logservice.getLogmessages().size());
    }

    @Test
    void testInsertDataWithUrlNotFound() throws Exception {
        String sqlUrl = "http://localhost/missing";
        Environment environment = mock(Environment.class);
        when(environment.getEnv(anyString())).thenReturn(sqlUrl);
        DataSource datasource = createDataSource("dbwithoutschema"); // An empty database that has no schema, will cause LiquibaseException when attempting to insert
        MockLogService logservice = new MockLogService();
        OldAlbumUrlInitDatabase component = new OldAlbumUrlInitDatabase();
        component.setEnvironment(environment);
        component.setLogService(logservice);
        component.setDatasource(datasource);
        component.activate();
        assertEquals(1, logservice.getLogmessages().size());
    }

    @Test
    void testInsertDataWithNoInitialDataUrl() throws Exception {
        DataSource datasource = mock(DataSource.class);
        MockLogService logservice = new MockLogService();
        OldAlbumUrlInitDatabase component = new OldAlbumUrlInitDatabase();
        component.setLogService(logservice);
        component.setDatasource(datasource);
        component.activate();
        assertEquals(1, logservice.getLogmessages().size());
    }

    private void addSchemaToDatasource(DataSource datasource, MockLogService logservice) throws SQLException {
        OldAlbumScheme scheme = new OldAlbumScheme();
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
        Connection connection = datasource.getConnection();
        String countSql = "select count(*) from albumentries";
        try(Statement countStatement = connection.createStatement()) {
            try(ResultSet results = countStatement.executeQuery(countSql)) {
                if (results.next()) {
                    int numberOfRows = results.getInt(1);
                    assertEquals(EXPECTED_NUMBER_OF_ALBUMENTRIES, numberOfRows);
                } else {
                    fail("Unable to count the rows in albumentries");
                }
            }
        }
    }

    private DataSource createDataSource(String dbname) throws Exception {
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + ";create=true");
        return derbyDataSourceFactory.createDataSource(properties);
    }

}
