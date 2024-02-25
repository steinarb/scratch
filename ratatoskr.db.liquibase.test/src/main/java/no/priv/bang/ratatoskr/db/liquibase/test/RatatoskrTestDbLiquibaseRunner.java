/*
 * Copyright 2023-2024 Steinar Bang
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
package no.priv.bang.ratatoskr.db.liquibase.test;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.priv.bang.ratatoskr.db.liquibase.RatatoskrLiquibase;

@Component(immediate=true, property = "name=ratatoskrdb")
public class RatatoskrTestDbLiquibaseRunner implements PreHook {

    @Activate
    public void activate() {
        // Called after all injections have been satisfied and before the PreHook service is exposed
    }

    @Override
    public void prepare(DataSource datasource) throws SQLException {
        var ratatoskrLiquibase = new RatatoskrLiquibase();
        try (var connect = datasource.getConnection()) {
            ratatoskrLiquibase.createInitialSchema(connect);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Error creating ratatoskr test database schema", e);
        }

        try (var connect = datasource.getConnection()) {
            insertMockData(connect);
        }

        try (var connect = datasource.getConnection()) {
            ratatoskrLiquibase.updateSchema(connect);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Error updating ratatoskr test database schema", e);
        }
    }

    public void insertMockData(Connection connect) throws SQLException {
        var databaseConnection = new JdbcConnection(connect);
        try(var classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader())) {
            try(var liquibase = new Liquibase("sql/data/db-changelog.xml", classLoaderResourceAccessor, databaseConnection)) {
                liquibase.update("");
            }
        } catch (Exception e) {
            throw new SQLException("Error inserting ratatoskr test database mock data", e);
        }
    }

}
