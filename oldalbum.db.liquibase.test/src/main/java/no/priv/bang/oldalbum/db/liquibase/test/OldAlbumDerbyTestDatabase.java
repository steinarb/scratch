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
package no.priv.bang.oldalbum.db.liquibase.test;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.priv.bang.oldalbum.db.liquibase.OldAlbumLiquibase;
import no.priv.bang.osgi.service.adapters.logservice.LogServiceAdapter;

@Component(immediate=true, property = "name=oldalbum")
public class OldAlbumDerbyTestDatabase implements PreHook {
    LogServiceAdapter logservice = new LogServiceAdapter();

    @Reference
    public void setLogService(LogService logservice) {
        this.logservice.setLogService(logservice);
    }

    @Activate
    public void activate() {
        // Called when the component is activated
    }

    @Override
    public void prepare(DataSource datasource) throws SQLException {
        createInitialSchema(datasource);
        insertMockData(datasource);
    }

    void createInitialSchema(DataSource datasource) throws SQLException {
        try (Connection connect = datasource.getConnection()) {
            OldAlbumLiquibase handleregLiquibase = new OldAlbumLiquibase();
            handleregLiquibase.createInitialSchema(connect);
        } catch (LiquibaseException e) {
            logservice.log(LogService.LOG_ERROR, "Error creating handlreg test database", e);
        }
    }

    void insertMockData(DataSource datasource) throws SQLException {
        DatabaseConnection databaseConnection = new JdbcConnection(datasource.getConnection());
        ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
        try (Liquibase liquibase = new Liquibase("oldalbum/sql/data/db-changelog.xml", classLoaderResourceAccessor, databaseConnection)) {
            liquibase.update("");
        } catch (LiquibaseException e) {
            logservice.log(LogService.LOG_ERROR, "Error populating dummy data database", e);
        } catch (Exception e) {
            logservice.log(LogService.LOG_ERROR, "Error closing connection when populating dummy data database", e);
        }
    }

}
