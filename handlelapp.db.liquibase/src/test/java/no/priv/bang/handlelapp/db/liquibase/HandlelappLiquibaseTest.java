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
package no.priv.bang.handlelapp.db.liquibase;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

class HandlelappLiquibaseTest {
    DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();

    @Test
    void testCreateSchema() throws Exception {
        Connection connection = createConnection();
        HandlelappLiquibase handlelappLiquibase = new HandlelappLiquibase();
        handlelappLiquibase.createInitialSchema(connection);
        int[] accountIds = addAccounts(connection);
        assertAccounts(connection);
        int[] categoryIds = addCategories(connection);
        assertCategories(connection, categoryIds);
        int[] articleIds = addArticles(connection, categoryIds);
        assertArticles(connection, articleIds, categoryIds);
        int storeid = 123;
        addStoreCategoryOrder(connection, storeid, categoryIds);
        assertStoreCategoryOrder(connection, storeid, categoryIds);
        int shoppingcartid = addShoppingCart(connection, accountIds[0], storeid);
        assertShoppingCart(connection, shoppingcartid, accountIds[0], storeid);
        addToShoppingList(connection, articleIds);
        assertShoppingList(connection, articleIds);
        handlelappLiquibase.updateSchema(connection);
    }

    @Test
    void testForceReleaseLocks() throws Exception {
        Connection connection = createConnection();
        HandlelappLiquibase handlelappLiquibase = new HandlelappLiquibase();
        assertDoesNotThrow(() -> handlelappLiquibase.forceReleaseLocks(connection));
    }

    private int[] addAccounts(Connection connection) throws Exception {
        int adminid = addAccount(connection, "admin");
        return new int[] { adminid };
    }

    private void assertAccounts(Connection connection) throws Exception {
        String sql = "select count(*) from handlelapp_accounts";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            try(ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    int count = results.getInt(1);
                    assertEquals(1, count);
                }
            }
        }
    }

    private int addAccount(Connection connection, String username) throws Exception {
        String sql = "insert into handlelapp_accounts (username) values (?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.executeUpdate();
        }

        return findAccountId(connection, username);
    }

    private int findAccountId(Connection connection, String username) throws Exception {
        String sql = "select account_id from handlelapp_accounts where username=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try(ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return results.getInt(1);
                }
            }
        }

        return -1;
    }

    private int[] addCategories(Connection connection) throws Exception {
        int fruitid = addCategory(connection, "Fruit");
        int vegetableid = addCategory(connection, "Vegetables");
        int dairyid = addCategory(connection, "Dairy");
        return new int[]{ fruitid, vegetableid, dairyid };
    }

    private void assertCategories(Connection connection, int[] categoryIds) throws Exception {
        String sql = "select * from categories";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            try(ResultSet results = statement.executeQuery()) {
                assertCategory(results, categoryIds[0], "Fruit");
                assertCategory(results, categoryIds[1], "Vegetables");
                assertCategory(results, categoryIds[2], "Dairy");
            }
        }
    }

    private int addArticle(Connection connection, String name, int categoryId) throws Exception {
        String sql = "insert into articles (name, category_id) values (?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setInt(2, categoryId);
            statement.executeUpdate();
        }

        return findArticleId(connection, name);
    }

    private void assertCategory(ResultSet results, int categoryId, String name) throws Exception {
        if (results.next()) {
            assertEquals(categoryId, results.getInt(1));
            assertEquals(name, results.getString(2));
        }
    }

    private int addCategory(Connection connection, String name) throws Exception {
        String sql = "insert into categories (name) values (?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.executeUpdate();
        }

        return findCategoryId(connection, name);
    }

    private int findCategoryId(Connection connection, String name) throws Exception {
        String sql = "select category_id from categories where name=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try(ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return results.getInt(1);
                }
            }
        }

        return -1;
    }

    private int[] addArticles(Connection connection, int[] categoryIds) throws Exception {
        int appleId = addArticle(connection, "Apple", categoryIds[0]);
        int carrotId = addArticle(connection, "Carrot", categoryIds[1]);
        int milkId = addArticle(connection, "Milk", categoryIds[2]);
        return new int[]{ appleId, carrotId, milkId };
    }

    private void assertArticles(Connection connection, int[] articleIds, int[] categoryIds) throws Exception {
        String sql = "select * from articles";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            try(ResultSet results = statement.executeQuery()) {
                assertArticle(results, articleIds[0], "Apple", categoryIds[0]);
                assertArticle(results, articleIds[1], "Carrot", categoryIds[1]);
                assertArticle(results, articleIds[2], "Milk", categoryIds[2]);
            }
        }
    }

    private void assertArticle(ResultSet results, int articleId, String name, int categoryId) throws Exception {
        if (results.next()) {
            assertEquals(articleId, results.getInt(1));
            assertEquals(name, results.getString(2));
            assertEquals(categoryId, results.getInt(3));
        }
    }

    private int findArticleId(Connection connection, String name) throws Exception {
        String sql = "select article_id from articles where name=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try(ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return results.getInt(1);
                }
            }
        }

        return -1;
    }

    private void addStoreCategoryOrder(Connection connection, int storeid, int[] categoryIds) throws Exception {
        String sql = "insert into store_category_order (store_id, category_id, sort) values (?, ?, ?)";
        for (int i=0; i<categoryIds.length; ++i) {
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, storeid);
                statement.setInt(2, categoryIds[i]);
                statement.setInt(3, i + 1);
                statement.executeUpdate();
            }
        }
    }

    private void assertStoreCategoryOrder(Connection connection, int storeid, int[] categoryIds) throws Exception {
        String sql = "select * from store_category_order where store_id=? order by sort";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, storeid);
            try(ResultSet results = statement.executeQuery()) {
                for (int i=0; i < categoryIds.length; ++i) {
                    assertTrue(results.next());
                    assertEquals(storeid, results.getInt(1));
                    assertEquals(categoryIds[i], results.getInt(2));
                    int sort = i + 1;
                    assertEquals(sort, results.getInt(3));
                }
            }
        }
    }

    private int addShoppingCart(Connection connection, int accountid, int storeid) throws Exception {
        String sql = "insert into shoppingcarts (account_id, store_id, shopping_start_time) values (?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountid);
            statement.setInt(2, storeid);
            statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            statement.executeUpdate();
        }

        return findShoppingCartId(connection, accountid, storeid);
    }

    private void assertShoppingCart(Connection connection, int shoppingcartid, int accountid, int storeid) throws Exception {
        String sql = "select * from shoppingcarts where shoppingcart_id=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, shoppingcartid);
            try(ResultSet results = statement.executeQuery()) {
                assertTrue(results.next());
                assertEquals(accountid, results.getInt(2));
                assertEquals(storeid, results.getInt(3));
                assertNotNull(results.getTimestamp(4));
                assertNull(results.getTimestamp(5));
            }
        }
    }

    private int findShoppingCartId(Connection connection, int accountid, int storeid) throws Exception {
        String sql = "select shoppingcart_id from shoppingcarts where account_id=? and store_id=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountid);
            statement.setInt(2, storeid);
            try(ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return results.getInt(1);
                }
            }
        }

        return -1;
    }

    private void addToShoppingList(Connection connection, int[] articleIds) throws Exception {
        addShoppinglistItem(connection, articleIds[2], 4);
        addShoppinglistItem(connection, articleIds[0], null);
        addShoppinglistItem(connection, articleIds[1], 1);
    }

    private void assertShoppingList(Connection connection, int[] articleIds) throws Exception {
        String sql = "select * from shoppinglist";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            try(ResultSet results = statement.executeQuery()) {
                assertShoppingListItem(results, articleIds[2], 4, null);
                assertShoppingListItem(results, articleIds[0], null, null);
                assertShoppingListItem(results, articleIds[1], 1, null);
            }
        }
    }

    private void addShoppinglistItem(Connection connection, int articleid, Integer amount) throws Exception {
        if (amount != null) {
            String sql = "insert into shoppinglist (article_id, amount) values (?, ?)";
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, articleid);
                statement.setInt(2, amount);
                statement.executeUpdate();
            }
        } else {
            String sql = "insert into shoppinglist (article_id) values (?)";
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, articleid);
                statement.executeUpdate();
            }
        }
    }

    private void assertShoppingListItem(ResultSet results, int articleid, Integer amount, Integer shoppingcartid) throws Exception {
        assertTrue(results.next());
        assertEquals(articleid, results.getInt(2));
        int amountInDb = results.getInt(3);
        if (amount == null) {
            assertTrue(results.wasNull());
        } else {
            assertEquals(amount, amountInDb);
        }
        int shoppingcartidInDb = results.getInt(4);
        if (shoppingcartid == null) {
            assertTrue(results.wasNull());
        } else {
            assertEquals(shoppingcartid, shoppingcartidInDb);
        }
    }

    private Connection createConnection() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:handlelapp;create=true");
        DataSource dataSource = derbyDataSourceFactory.createDataSource(properties);
        return dataSource.getConnection();
    }

}
