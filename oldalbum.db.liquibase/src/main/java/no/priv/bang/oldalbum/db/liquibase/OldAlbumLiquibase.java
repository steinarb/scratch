/*
 * Copyright 2020-2022 Steinar Bang
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
package no.priv.bang.oldalbum.db.liquibase;

import java.sql.Connection;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class OldAlbumLiquibase {

    public void createInitialSchema(Connection connection) throws LiquibaseException {
        applyLiquibaseChangeLog(connection, "oldalbum/changelog01.xml");
    }

    public void updateSchema(Connection connection) throws LiquibaseException {
        applyLiquibaseChangeLog(connection, "oldalbum/changelog02.xml");
    }

    private void applyLiquibaseChangeLog(Connection connection, String changelogClasspathResource) throws LiquibaseException {
        DatabaseConnection databaseConnection = new JdbcConnection(connection);
        try(var classLoaderResourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader())) {
            try(var liquibase = new Liquibase(changelogClasspathResource, classLoaderResourceAccessor, databaseConnection)) {
                liquibase.update("");
            }
        } catch (LiquibaseException e) {
            throw e;
        } catch (Exception e) {
            throw new LiquibaseException("Error closing resource", e);
        }
    }

}
