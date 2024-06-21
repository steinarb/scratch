/*
 * Copyright 2018-2024 Steinar Bang
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
package no.priv.bang.handlereg.db.liquibase;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.io.PrintWriter;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

class HandleregLiquibaseTest {
    DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();

    @Test
    void testCreateSchema() throws Exception {
        var handleregLiquibase = new HandleregLiquibase();
        try(var connection = createConnection("handlereg")) {
            handleregLiquibase.createInitialSchema(connection);
        }

        try(var connection = createConnection("handlereg")) {
            addAccounts(connection);
            assertAccounts(connection);
            addStores(connection);
            assertStores(connection);
            addTransactions(connection);
            assertTransactions(connection);
            addFavourites(connection);
            assertFavourites(connection);
        }

        try(var connection = createConnection("handlereg")) {
            handleregLiquibase.updateSchema(connection);
        }
    }

    @Disabled("Pseudo-test that imports legacy data and turns them into SQL files that can be imported into an SQL database")
    @Test
    void createSqlFromOriginalData() throws Exception {
        var connection = createConnection("handlereg");
        var handleregLiquibase = new HandleregLiquibase();
        handleregLiquibase.createInitialSchema(connection);
        var oldData = new OldData();
        assertEquals(137, oldData.butikker.size());
        assertEquals(4501, oldData.handlinger.size());
        var jdAccountid = addAccount(connection, "sb");
        var jadAccountid = addAccount(connection, "tlf");
        var nærbutikkRekkefølge = 0;
        var annenbutikkRekkefølge = 0;
        var gruppe = 1;
        var rekkefølge = 0;
        for (var store : oldData.butikker) {
            var deaktivert = oldData.deaktivert.contains(store);
            if (oldData.nærbutikker.contains(store)) {
                gruppe = 1;
                rekkefølge = (nærbutikkRekkefølge += 10);
            } else {
                gruppe = 2;
                rekkefølge = (annenbutikkRekkefølge += 10);
            }
            addStore(connection, store, gruppe, rekkefølge, deaktivert);
        }

        var accountids = new HashMap<>();
        accountids.put("jd", jdAccountid);
        accountids.put("jad", jadAccountid);
        try(var storeWriter = new PrintWriter("accounts.sql")) {
            storeWriter.println("--liquibase formatted sql");
            storeWriter.println("--changeset sb:example_accounts");
            try(var statement = connection.prepareStatement("select username from accounts order by account_id")) {
                var results = statement.executeQuery();
                while(results.next()) {
                    var username = results.getString(1);
                    storeWriter.println(String.format("insert into accounts (username) values ('%s');", username));
                }
            }
        }

        var storeids = findStoreIds(connection);
        assertEquals(137, storeids.size());

        var format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try(var transactionWriter = new PrintWriter("transactions.sql")) {
            transactionWriter.println("--liquibase formatted sql");
            transactionWriter.println("--changeset sb:example_transactions");
            for (Handling handling : oldData.handlinger) {
                var accountid = accountids.get(handling.username);
                System.out.println("handling: " + handling);
                var storeid = storeids.get(handling.butikk);
                double belop = handling.belop;
                var timestamp = format.format(handling.timestamp);
                transactionWriter.println(String.format("insert into transactions (account_id, store_id, transaction_time, transaction_amount) values (%d, %d, '%s', %f);", accountid, storeid, timestamp, belop));
            }
        }
    }

    private void addAccounts(Connection connection) throws Exception {
        addAccount(connection, "admin");
    }

    private void assertAccounts(Connection connection) throws Exception {
        var sql = "select count(*) from accounts";
        try(var statement = connection.prepareStatement(sql)) {
            try(var results = statement.executeQuery()) {
                if (results.next()) {
                    var count = results.getInt(1);
                    assertEquals(1, count);
                }
            }
        }
    }

    private int addAccount(Connection connection, String username) throws Exception {
        var sql = "insert into accounts (username) values (?)";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.executeUpdate();
        }

        return findAccountId(connection, username);
    }

    private int findAccountId(Connection connection, String username) throws Exception {
        var sql = "select account_id from accounts where username=?";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try(var results = statement.executeQuery()) {
                if (results.next()) {
                    return results.getInt(1);
                }
            }
        }

        return -1;
    }

    private Map<String, Integer> findStoreIds(Connection connection) throws Exception {
        var storeids = new HashMap<String, Integer>();
        try(var storeWriter = new PrintWriter("stores.sql")) {
            storeWriter.println("--liquibase formatted sql");
            storeWriter.println("--changeset sb:example_stores");
            try(var statement = connection.prepareStatement("select * from stores")) {
                var results = statement.executeQuery();
                while(results.next()) {
                    var storename = results.getString(2);
                    var storeid = results.getInt(1);
                    var gruppe = results.getInt(3);
                    var rekkefølge = results.getInt(4);
                    var deaktivert = results.getBoolean(5);
                    storeids.put(storename, storeid);
                    storeWriter.println(String.format("insert into stores (store_name, gruppe, rekkefolge, deaktivert) values ('%s', %d, %d, %b);", storename, gruppe, rekkefølge, deaktivert));
                }
            }
        }

        return storeids;
    }

    private void addStores(Connection connection) throws Exception {
        addStore(connection, "Joker Folldal", 2, 10, false);
    }

    private void assertStores(Connection connection) throws Exception {
        try(var statement = connection.prepareStatement("select * from stores")) {
            var resultset = statement.executeQuery();
            assertStore(resultset, "Joker Folldal");
        }
    }

    private void assertTransactions(Connection connection) throws Exception {
        try(var statement = connection.prepareStatement("select * from transactions join stores on transactions.store_id=stores.store_id join accounts on transactions.account_id=accounts.account_id")) {
            var results = statement.executeQuery();
            assertTransaction(results, 210.0, "Joker Folldal", "admin");
        }
    }

    private void addTransactions(Connection connection) throws Exception {
        var accountid = 1;
        var storeid = 1;
        addTransaction(connection, accountid, storeid, 210.0);
    }

    private void addStore(Connection connection, String storename, int gruppe, int rekkefølge, boolean deaktivert) throws Exception {
        try(var statement = connection.prepareStatement("insert into stores (store_name, gruppe, rekkefolge, deaktivert) values (?, ?, ?, ?)")) {
            statement.setString(1, storename);
            statement.setInt(2, gruppe);
            statement.setInt(3, rekkefølge);
            statement.setBoolean(4, deaktivert);
            statement.executeUpdate();
        }
    }

    private void assertStore(ResultSet resultset, String storename) throws Exception {
        assertTrue(resultset.next());
        assertEquals(storename, resultset.getString(2));
    }

    private void addTransaction(Connection connection, int accountid, int storeid, double amount) throws Exception {
        try(var statement = connection.prepareStatement("insert into transactions (account_id, store_id, transaction_amount) values (?, ?, ?)")) {
            statement.setInt(1, accountid);
            statement.setInt(2, storeid);
            statement.setDouble(3, amount);
            statement.executeUpdate();
        }
    }

    private void assertTransaction(ResultSet results, double amount, String storename, String username) throws Exception {
        assertTrue(results.next());
        assertEquals(amount, results.getDouble(5), 0.1);
        assertEquals(storename, results.getString(7));
        assertEquals(username, results.getString(12));
    }

    private void addFavourites(Connection connection) throws Exception {
        var accountid = findAccountId(connection, "admin");
        var storeid = findStoreIds(connection).entrySet().stream().findFirst().get().getValue();
        addFavourite(connection, accountid, storeid, 10);
    }

    private void addFavourite(Connection connection, int accountid, int storeid, int rekkefolge) throws Exception {
        try(var statement = connection.prepareStatement("insert into favourites (account_id, store_id, rekkefolge) values (?, ?, ?)")) {
            statement.setInt(1, accountid);
            statement.setInt(2, storeid);
            statement.setInt(3, rekkefolge);
            statement.executeUpdate();
        }
    }

    private void assertFavourites(Connection connection) throws Exception {
        var accountid = findAccountId(connection, "admin");
        var storeid = findStoreIds(connection).entrySet().stream().findFirst().get().getValue();
        var rekkefolge = 10;
        try(var statement = connection.prepareStatement("select * from favourites")) {
            var results = statement.executeQuery();
            assertFavourite(results, accountid, storeid, rekkefolge);
        }

    }

    private void assertFavourite(ResultSet results, int accountid, int storeid, int rekkefolge) throws Exception {
        assertTrue(results.next());
        assertEquals(accountid, results.getInt(2));
        assertEquals(storeid, results.getInt(3));
        assertEquals(rekkefolge, results.getInt(4));
    }

    private Connection createConnection(String dbname) throws Exception {
        var properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + ";create=true");
        var dataSource = derbyDataSourceFactory.createDataSource(properties);
        return dataSource.getConnection();
    }

}
