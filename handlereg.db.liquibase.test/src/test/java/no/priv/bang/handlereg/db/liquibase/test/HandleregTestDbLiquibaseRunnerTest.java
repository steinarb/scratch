/*
 * Copyright 2018-2022 Steinar Bang
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
package no.priv.bang.handlereg.db.liquibase.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.handlereg.services.HandleregException;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class HandleregDerbyTestDatabaseTest {

    @Test
    void testCreateAndVerifySomeDataInSomeTables() throws Exception {
        DataSource datasource = createDataSource("handlereg");

        MockLogService logservice = new MockLogService();
        var runner = new HandleregTestDbLiquibaseRunner();
        runner.setLogService(logservice);
        runner.activate();
        runner.prepare(datasource);
        assertAccounts(datasource);
        int originalNumberOfTransactions = findNumberOfTransactions(datasource);
        addTransaction(datasource, 138);
        int updatedNumberOfTransactions = findNumberOfTransactions(datasource);
        assertEquals(originalNumberOfTransactions + 1, updatedNumberOfTransactions);
    }

    @Test
    void testFailWhenCreatingInitialSchema() throws Exception {
        var realdb = createDataSource("handlereg1");
        Connection connection = spy(realdb.getConnection());
        // The wrapped JDBC connection throws SQLException on setAutoCommit(anyBoolean());
        DataSource datasource = spy(realdb);
        when(datasource.getConnection())
            .thenReturn(connection)
            .thenCallRealMethod()
            .thenCallRealMethod();

        MockLogService logservice = new MockLogService();
        var runner = new HandleregTestDbLiquibaseRunner();
        runner.setLogService(logservice);
        runner.activate();
        assertThat(logservice.getLogmessages()).isEmpty();
        runner.prepare(datasource);
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[ERROR] Error creating initial schema in handlereg test database");
    }

    @Test
    void testFailWhenInsertingMockDataBecauseNoSchema() throws Exception {
        Connection connection = createDataSource("handlereg2").getConnection();

        MockLogService logservice = new MockLogService();
        var runner = new HandleregTestDbLiquibaseRunner();
        runner.setLogService(logservice);
        runner.activate();
        var e = assertThrows(
            HandleregException.class,
            () -> runner.insertMockData(connection));
        assertThat(e.getMessage()).startsWith("Error inserting mock data in handlereg derby test database");
    }

    @Test
    void testFailWhenUpdatingsSchema() throws Exception {
        Connection connection = spy(createDataSource("handlereg3").getConnection());
        // The wrapped JDBC connection throws SQLException on setAutoCommit(anyBoolean());
        DataSource datasource = spy(createDataSource("handlereg4"));
        when(datasource.getConnection())
            .thenCallRealMethod()
            .thenCallRealMethod()
            .thenReturn(connection);

        MockLogService logservice = new MockLogService();
        var runner = new HandleregTestDbLiquibaseRunner();
        runner.setLogService(logservice);
        runner.activate();
        assertThat(logservice.getLogmessages()).isEmpty();
        runner.prepare(datasource);
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[ERROR] Error updating schema in handlereg test database");
    }

    private void assertAccounts(DataSource datasource) throws Exception {
        try (Connection connection = datasource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select * from accounts")) {
                try (ResultSet results = statement.executeQuery()) {
                    assertAccount(results, "jod");
                }
            }
        }
    }

    private void assertAccount(ResultSet results, String username) throws Exception {
        assertTrue(results.next());
        assertEquals(username, results.getString(2)); // column 1 is the id
    }

    private void addTransaction(DataSource database, double amount) throws SQLException {
        try (Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("insert into transactions (account_id, store_id, transaction_amount) values (1, 1, ?)")) {
                statement.setDouble(1, amount);
                statement.executeUpdate();
            }
        }
    }

    private int findNumberOfTransactions(DataSource database) throws SQLException {
        try (Connection connection = database.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select * from transactions")) {
                try (ResultSet results = statement.executeQuery()) {
                    int count = 0;
                    while(results.next()) {
                        ++count;
                    }

                    return count;
                }
            }
        }
    }

    private DataSource createDataSource(String dbname) throws SQLException {
        DataSourceFactory dataSourceFactory = new DerbyDataSourceFactory();
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + ";create=true");
        DataSource datasource = dataSourceFactory.createDataSource(properties);
        return datasource;
    }

}
