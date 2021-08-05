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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;
import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.priv.bang.handlelapp.db.liquibase.HandlelappLiquibase;

@Component(immediate=true, property = "name=handlelappdb")
public class HandlelappTestDbLiquibaseRunner implements PreHook {

    private static final String DEFAULT_DUMMY_DATA_CHANGELOG = "sql/data/db-changelog.xml";
    private Logger logger;
    private String databaselanguage;

    @Reference
    public void setLogService(LogService logservice) {
        this.logger = logservice.getLogger(HandlelappTestDbLiquibaseRunner.class);
    }

    @Activate
    public void activate(Map<String, Object> config) {
        databaselanguage = (String) config.get("databaselanguage");
    }

    @Override
    public void prepare(DataSource datasource) throws SQLException {
        try (Connection connect = datasource.getConnection()) {
            HandlelappLiquibase handlelappLiquibase = new HandlelappLiquibase();
            handlelappLiquibase.createInitialSchema(connect);
            insertMockData(connect);
            handlelappLiquibase.updateSchema(connect);
        } catch (LiquibaseException e) {
            logger.error("Error creating handlelapp test database schema", e);
        }
    }

    public void insertMockData(Connection connect) throws LiquibaseException {
        DatabaseConnection databaseConnection = new JdbcConnection(connect);
        ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());
        Liquibase liquibase = new Liquibase(dummyDataResourceName(), classLoaderResourceAccessor, databaseConnection);
        liquibase.update("");
    }

    String dummyDataResourceName() {
        if (databaselanguage == null) {
            return DEFAULT_DUMMY_DATA_CHANGELOG;
        }

        String resourceName = DEFAULT_DUMMY_DATA_CHANGELOG.replace(".xml", "_" + databaselanguage + ".xml");
        if (getClass().getClassLoader().getResource(resourceName) == null) {
            logger.warn(String.format("Failed to find data for %s defaulting to Norwegian", databaselanguage));
            return DEFAULT_DUMMY_DATA_CHANGELOG;
        }

        return resourceName;
    }

}
