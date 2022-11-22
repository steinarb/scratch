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
package no.priv.bang.oldalbum.db.liquibase.production;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class OldAlbumProductionDatabaseTest {
    private static final int EXPECTED_NUMBER_OF_ALBUMENTRIES = 1;
    DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();

    @Test
    void testPrepare() throws Exception {
        DataSource datasource = createDataSource("oldalbum");
        MockLogService logservice = new MockLogService();
        OldAlbumProductionDatabase hook = new OldAlbumProductionDatabase();
        hook.setLogService(logservice);
        hook.activate();
        hook.prepare(datasource);
        assertDummyDataAsExpected(datasource);
    }

    @Test
    void testCreateInitialSchemaWithLiquibaseException() throws Exception {
        DataSource datasource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(connection.getMetaData()).thenThrow(SQLException.class);
        when(datasource.getConnection()).thenReturn(connection);
        MockLogService logservice = new MockLogService();
        OldAlbumProductionDatabase hook = new OldAlbumProductionDatabase();
        hook.setLogService(logservice);
        hook.activate();
        hook.createInitialSchema(datasource);
        assertEquals(1, logservice.getLogmessages().size());
    }

    @Test
    void testInsertMockDataWithLiquibaseException() throws Exception {
        DataSource datasource = createDataSource("dbwithoutschema"); // An empty database that has no schema, will cause LiquibaseException when attempting to insert
        MockLogService logservice = new MockLogService();
        OldAlbumProductionDatabase hook = new OldAlbumProductionDatabase();
        hook.setLogService(logservice);
        hook.insertInitialData(datasource);
        assertEquals(1, logservice.getLogmessages().size());
    }

    private void assertDummyDataAsExpected(DataSource datasource) throws Exception {
        try(var connection = datasource.getConnection()) {
            String countSql = "select count(*) from albumentries";
            try(var countStatement = connection.createStatement()) {
                try(ResultSet results = countStatement.executeQuery(countSql)) {
                    if (results.next()) {
                        int numberOfRows = results.getInt(1);
                        assertEquals(EXPECTED_NUMBER_OF_ALBUMENTRIES, numberOfRows);
                    } else {
                        fail("Unable to count the rows in albumentries");
                    }
                }
            }

            // The single existing entry will have require_login=false and count will be 0
            String countWithRequireLoginSql = "select count(*) from albumentries where require_login=true";
            try(var countStatement = connection.createStatement()) {
                try(ResultSet results = countStatement.executeQuery(countWithRequireLoginSql)) {
                    if (results.next()) {
                        int numberOfRows = results.getInt(1);
                        assertEquals(0, numberOfRows);
                    } else {
                        fail("Unable to count the rows with require_login in albumentries");
                    }
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
