/*
 * Copyright 2019 Steinar Bang
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
package no.priv.bang.osgiservice.database;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.osgi.service.jdbc.DataSourceFactory;

class DatabaseServiceBaseTest {

    @Test
    void testGetConnection() throws Exception {
        Connection connection = mock(Connection.class);
        DataSource datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenReturn(connection);
        DatabaseService service = new DatabaseServiceBase() {

                @Override
                public DataSource getDatasource() {
                    return datasource;
                }
            };
        assertEquals(connection, service.getConnection());
    }

    @Test
    void testGetConnectionWithNoDataSource() throws Exception {
        DatabaseService service = new DatabaseServiceBase() {

                @Override
                public DataSource getDatasource() {
                    return null;
                }
            };

        assertThrows(DatabaseServiceException.class, () -> {
                service.getConnection();
            });
    }

    @Test
    void testCreateDatabaseConnectionProperties() {
        Properties connectionProperties1 = DatabaseServiceBase.createDatabaseConnectionProperties("jdbc:postgresql:///ukelonn", "karaf", "karaf");
        assertEquals("jdbc:postgresql:///ukelonn", connectionProperties1.get(DataSourceFactory.JDBC_URL));
        assertEquals("karaf", connectionProperties1.get(DataSourceFactory.JDBC_USER));
        assertEquals("karaf", connectionProperties1.get(DataSourceFactory.JDBC_PASSWORD));
        Properties connectionProperties2 = DatabaseServiceBase.createDatabaseConnectionProperties("jdbc:postgresql:///ukelonn", "", "");
        assertEquals("jdbc:postgresql:///ukelonn", connectionProperties2.get(DataSourceFactory.JDBC_URL));
        assertNull(connectionProperties2.get(DataSourceFactory.JDBC_USER));
        assertNull(connectionProperties2.get(DataSourceFactory.JDBC_PASSWORD));
    }

}
