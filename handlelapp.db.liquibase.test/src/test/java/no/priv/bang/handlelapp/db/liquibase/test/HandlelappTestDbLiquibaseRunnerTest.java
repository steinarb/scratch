/*
 * Copyright 2021 Steinar Bang
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
package no.priv.bang.handlelapp.db.liquibase.test;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class HandlelappTestDbLiquibaseRunnerTest {

    @Test
    void testCreateAndVerifySomeDataInSomeTables() throws Exception {
        DataSourceFactory dataSourceFactory = new DerbyDataSourceFactory();
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:handlelapp;create=true");
        DataSource datasource = dataSourceFactory.createDataSource(properties);

        MockLogService logservice = new MockLogService();
        HandlelappTestDbLiquibaseRunner runner = new HandlelappTestDbLiquibaseRunner();
        runner.setLogService(logservice);
        runner.activate(Collections.emptyMap());
        runner.prepare(datasource);
        assertAccounts(datasource);
        assertCategories(datasource);
        assertArticles(datasource);
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

    private void assertCategories(DataSource datasource) throws Exception {
        try (Connection connection = datasource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select * from categories")) {
                try (ResultSet results = statement.executeQuery()) {
                    assertCategory(results, "Grønnsaker");
                    assertCategory(results, "Frukt");
                }
            }
        }
    }

    private void assertCategory(ResultSet results, String name) throws Exception {
        assertTrue(results.next(), "Expected a row in table categories but found none");
        assertEquals(name, results.getString(2)); // column 1 is the id
    }

    private void assertArticles(DataSource datasource) throws Exception {
        try (Connection connection = datasource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("select * from articles")) {
                try (ResultSet results = statement.executeQuery()) {
                    assertArticle(results, "Gulrøtter", 1);
                    assertTrue(results.next());
                    assertTrue(results.next());
                    assertTrue(results.next());
                    assertTrue(results.next());
                    assertArticle(results, "Epler", 2);
                }
            }
        }
    }

    private void assertArticle(ResultSet results, String name, int categoryid) throws Exception {
        assertTrue(results.next(), "Expected a row in table articles but found none");
        assertEquals(name, results.getString(2)); // column 1 is the id
        assertEquals(categoryid, results.getInt(3));
    }

}
