/*
 * Copyright 2020-2024 Steinar Bang
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

import java.sql.SQLException;
import javax.sql.DataSource;

import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import liquibase.Scope;
import liquibase.ThreadLocalScopeManager;
import liquibase.exception.LiquibaseException;
import no.priv.bang.oldalbum.db.liquibase.OldAlbumLiquibase;
import no.priv.bang.osgi.service.adapters.logservice.LoggerAdapter;

@Component(immediate=true, property = "name=oldalbum")
public class OldAlbumDerbyTestDatabase implements PreHook {
    LoggerAdapter logger = new LoggerAdapter(getClass());

    @Reference
    public void setLogService(LogService logservice) {
        this.logger.setLogService(logservice);
    }

    @Activate
    public void activate() {
        // Called when the component is activated
        Scope.setScopeManager(new ThreadLocalScopeManager());
    }

    @Override
    public void prepare(DataSource datasource) throws SQLException {
        var oldalbumLiquibase = new OldAlbumLiquibase();
        createInitialSchema(datasource, oldalbumLiquibase);
        insertMockData(datasource, oldalbumLiquibase);
        updateSchema(datasource, oldalbumLiquibase);
        insertAdditionalMockDataAfterSchemaChange(datasource, oldalbumLiquibase);
    }

    void createInitialSchema(DataSource datasource, OldAlbumLiquibase oldalbumLiquibase) throws SQLException {
        try (var connect = datasource.getConnection()) {
            oldalbumLiquibase.createInitialSchema(connect);
        } catch (LiquibaseException e) {
            logger.error("Error creating schema in oldalbum derby test database", e);
        }
    }

    void insertMockData(DataSource datasource, OldAlbumLiquibase liquibase) throws SQLException {
        try (var connect = datasource.getConnection()) {
            liquibase.applyLiquibaseChangeLog(connect, "oldalbum/sql/data/db-changelog.xml", getClass().getClassLoader());
        } catch (Exception e) {
            logger.error("Error populating oldalbum derby test database with dummy data", e);
        }
    }

    private void updateSchema(DataSource datasource, OldAlbumLiquibase oldalbumLiquibase) throws SQLException {
        try (var connect = datasource.getConnection()) {
            oldalbumLiquibase.updateSchema(connect);
        } catch (LiquibaseException e) {
            logger.error("Error updating schema of oldalbum derby test database", e);
        }
    }

    private void insertAdditionalMockDataAfterSchemaChange(DataSource datasource, OldAlbumLiquibase liquibase) throws SQLException {
        try (var connect = datasource.getConnection()) {
            liquibase.applyLiquibaseChangeLog(connect, "oldalbum/sql/data/db-changelog-02.xml", getClass().getClassLoader());
        } catch (Exception e) {
            logger.error("Error populating oldalbum derby test database with additional dummy data after schema update", e);
        }
    }

}
