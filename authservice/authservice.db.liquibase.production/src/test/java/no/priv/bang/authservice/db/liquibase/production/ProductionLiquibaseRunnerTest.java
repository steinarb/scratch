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
package no.priv.bang.authservice.db.liquibase.production;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.karaf.derby.embedded.EmbeddedDerbyDataSourceFactory;

class ProductionLiquibaseRunnerTest {
    DataSourceFactory derbyDataSourceFactory = new EmbeddedDerbyDataSourceFactory();

    @Test
    void testCreateSchema() throws Exception {
        var runner = new ProductionLiquibaseRunner();
        runner.activate();
        var database = createDataSource("authservice1");
        runner.prepare(database);
        try(var connection = database.getConnection()) {
            try(var statment = connection.prepareStatement("select * from users")) {
                var results = statment.executeQuery();
                int usercount = 0;
                while(results.next()) {
                    ++usercount;
                }

                assertEquals(1, usercount);
            }
        }

        try(var connection = database.getConnection()) {
            try(var statment = connection.prepareStatement("select * from roles")) {
                var results = statment.executeQuery();
                int rolecount = 0;
                while(results.next()) {
                    ++rolecount;
                }

                assertEquals(1, rolecount);
            }
        }
    }

    @Test
    void testCreateSchemaWhenSQLExceptionIsThrown() throws Exception {
        var runner = new ProductionLiquibaseRunner();
        var connection = mock(Connection.class);
        var datasource = mock(DataSource.class);
        when(connection.getMetaData()).thenThrow(SQLException.class);
        when(datasource.getConnection()).thenReturn(connection);

        runner.activate();
        var ex = assertThrows(
            AuthserviceException.class,
            () -> runner.prepare(datasource));
        assertThat(ex.getMessage()).startsWith("Failed to create schema in authservice postgresql database");
    }

    @Test
    void testFailWhenInsertingData() throws Exception {
        var runner = new ProductionLiquibaseRunner();
        var datasource = spy(createDataSource("authservice2"));
        var connection = mock(Connection.class);
        when(connection.getMetaData()).thenThrow(SQLException.class);
        when(datasource.getConnection()).thenCallRealMethod().thenReturn(connection);

        runner.activate();
        var ex = assertThrows(
            AuthserviceException.class,
            () -> runner.prepare(datasource));
        assertThat(ex.getMessage()).startsWith("Failed to create schema in authservice postgresql database");
    }

    @Test
    void testFailWhenUpdatingSchema() throws Exception {
        var runner = new ProductionLiquibaseRunner();
        var datasource = spy(createDataSource("authservice3"));
        var connection = mock(Connection.class);
        when(connection.getMetaData()).thenThrow(SQLException.class);
        when(datasource.getConnection()).thenCallRealMethod().thenCallRealMethod().thenReturn(connection);

        runner.activate();
        var ex = assertThrows(
            AuthserviceException.class,
            () -> runner.prepare(datasource));
        assertThat(ex.getMessage()).startsWith("Failed to update schma in authservice postgresql database");
    }

    Connection createMockConnection() throws Exception {
        var connection = mock(Connection.class);
        var metadata = mock(DatabaseMetaData.class);
        when(metadata.getDatabaseProductName()).thenReturn("mockdb");
        when(metadata.getSQLKeywords()).thenReturn("insert, select, delete");
        when(metadata.getURL()).thenReturn("jdbc:mock:///authservice");
        var tables = mock(ResultSet.class);
        when(metadata.getTables(anyString(), anyString(), anyString(), any(String[].class))).thenReturn(tables);
        var stmnt = mock(Statement.class);
        var results = mock(ResultSet.class);
        when(results.next()).thenReturn(true).thenReturn(false);
        when(stmnt.executeQuery(anyString())).thenReturn(results);
        when(stmnt.getUpdateCount()).thenReturn(-1);
        when(connection.createStatement()).thenReturn(stmnt);
        when(connection.getMetaData()).thenReturn(metadata);
        return connection;
    }

    private DataSource createDataSource(String dbname) throws SQLException {
        var properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + ";create=true");
        return derbyDataSourceFactory.createDataSource(properties);
    }

}
