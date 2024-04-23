/*
 * Copyright 2019-2024 Steinar Bang
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
package no.priv.bang.handlereg.db.liquibase.production;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.handlereg.services.HandleregException;
import no.priv.bang.karaf.derby.embedded.EmbeddedDerbyDataSourceFactory;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class HandleregProductionDbLiquibaseRunnerTest {

    @Test
    void testCreateAndVerifySomeDataInSomeTables() throws Exception {
        var dataSourceFactory = new EmbeddedDerbyDataSourceFactory();
        var properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:handlereg;create=true");
        var datasource = dataSourceFactory.createDataSource(properties);

        var logservice = new MockLogService();
        var runner = new HandleregProductionDbLiquibaseRunner();
        runner.setLogService(logservice);
        runner.activate();
        runner.prepare(datasource);
        var jdId = addAccount(datasource, "jd");
        assertAccounts(datasource);
        var storeid = addStore(datasource, "Spar Næroset");
        var originalNumberOfTransactions = findNumberOfTransactions(datasource);
        addTransaction(datasource, jdId, storeid, 138);
        var updatedNumberOfTransactions = findNumberOfTransactions(datasource);
        assertEquals(originalNumberOfTransactions + 1, updatedNumberOfTransactions);
    }

    @Test
    void testFailWhenCreatingInitialSchema() throws Exception {
        var realdb = createDataSource("handlereg1");
        var connection = spy(realdb.getConnection());
        // The wrapped JDBC connection throws SQLException on setAutoCommit(anyBoolean());
        var datasource = spy(realdb);
        when(datasource.getConnection())
            .thenReturn(connection)
            .thenCallRealMethod()
            .thenCallRealMethod();

        var logservice = new MockLogService();
        var runner = new HandleregProductionDbLiquibaseRunner();
        runner.setLogService(logservice);
        runner.activate();
        assertThat(logservice.getLogmessages()).isEmpty();
        runner.prepare(datasource);
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[ERROR] Failed to create initial schema of handlereg PostgreSQL database");
    }

    @Test
    void testFailWhenInsertingMockDataBecauseNoSchema() throws Exception {
        var connection = spy(createDataSource("handlereg2").getConnection());

        var logservice = new MockLogService();
        var runner = new HandleregProductionDbLiquibaseRunner();
        runner.setLogService(logservice);
        runner.activate();
        var e = assertThrows(
            HandleregException.class,
            () -> runner.insertMockData(connection));
        assertThat(e.getMessage()).startsWith("Error inserting initial data in handlereg postgresql database");
    }

    @Test
    void testFailWhenUpdatingsSchema() throws Exception {
        var connection = spy(createDataSource("handlereg3").getConnection());
        // The wrapped JDBC connection throws SQLException on setAutoCommit(anyBoolean());
        var datasource = spy(createDataSource("handlereg4"));
        when(datasource.getConnection())
            .thenCallRealMethod()
            .thenCallRealMethod()
            .thenReturn(connection);

        var logservice = new MockLogService();
        var runner = new HandleregProductionDbLiquibaseRunner();
        runner.setLogService(logservice);
        runner.activate();
        assertThat(logservice.getLogmessages()).isEmpty();
        runner.prepare(datasource);
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).startsWith("[ERROR] Failed to update schema of handlereg PostgreSQL database");
    }

    private void assertAccounts(DataSource datasource) throws Exception {
        try (var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select * from accounts")) {
                try (var results = statement.executeQuery()) {
                    assertAccount(results, "jd");
                }
            }
        }
    }

    private void assertAccount(ResultSet results, String username) throws Exception {
        assertTrue(results.next());
        assertEquals(username, results.getString(2)); // column 1 is the id
    }

    private int addAccount(DataSource datasource, String username) throws Exception {
        try (var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("insert into accounts (username) values (?)")) {
                statement.setString(1, username);
                statement.executeUpdate();
            }
        }
        var accountId = -1;
        try (var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select * from accounts where username=?")) {
                statement.setString(1, username);
                try (var results = statement.executeQuery()) {
                    results.next();
                    accountId = results.getInt(1);
                }
            }
        }
        return accountId;
    }

    private int addStore(DataSource datasource, String storename) throws Exception {
        try (var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("insert into stores (store_name) values (?)")) {
                statement.setString(1, storename);
                statement.executeUpdate();
            }
        }
        var storeid = -1;
        try (var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select * from stores where store_name=?")) {
                statement.setString(1, storename);
                try (var results = statement.executeQuery()) {
                    results.next();
                    storeid = results.getInt(1);
                }
            }
        }
        return storeid;
    }

    private void addTransaction(DataSource datasource, int accountid, int storeid, double amount) throws SQLException {
        try (var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("insert into transactions (account_id, store_id, transaction_amount) values (?, ?, ?)")) {
                statement.setInt(1, accountid);
                statement.setInt(2, storeid);
                statement.setDouble(3, amount);
                statement.executeUpdate();
            }
        }
    }

    private int findNumberOfTransactions(DataSource datasource) throws SQLException {
        try (var connection = datasource.getConnection()) {
            try(var statement = connection.prepareStatement("select * from transactions")) {
                try (var results = statement.executeQuery()) {
                    var count = 0;
                    while(results.next()) {
                        ++count;
                    }

                    return count;
                }
            }
        }
    }

    private DataSource createDataSource(String dbname) throws SQLException {
        var dataSourceFactory = new EmbeddedDerbyDataSourceFactory();
        var properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + ";create=true");
        var datasource = dataSourceFactory.createDataSource(properties);
        return datasource;
    }

}
