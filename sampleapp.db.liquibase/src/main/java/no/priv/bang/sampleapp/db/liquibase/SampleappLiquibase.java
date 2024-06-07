/*
 * Copyright 2021-2024 Steinar Bang
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
package no.priv.bang.sampleapp.db.liquibase;

import java.sql.Connection;
import java.util.Map;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.Scope;
import liquibase.Scope.ScopedRunner;
import liquibase.changelog.ChangeLogParameters;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DatabaseChangelogCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

public class SampleappLiquibase {

    public void createInitialSchema(Connection connection) throws LiquibaseException {
        applyLiquibaseChangelist(connection, "sampleapp-db-changelog/db-changelog-1.0.0.xml");
    }

    public void updateSchema(Connection connection) throws LiquibaseException {
        applyLiquibaseChangelist(connection, "sampleapp-db-changelog/db-changelog-1.0.1.xml");
    }

    public void applyLiquibaseChangelist(Connection connection, String changelistClasspathResource, ClassLoader classLoader) throws LiquibaseException {
        try (var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection))) {
            try(var liquibase = new Liquibase(changelistClasspathResource, new ClassLoaderResourceAccessor(classLoader), database)) {
                liquibase.update(new Contexts(), new LabelExpression());
            }
        } catch (LiquibaseException e) {
            throw e;
        } catch (Exception e) {
            // AutoClosable.close() may throw Exception
            throw new LiquibaseException(e);
        }
    }

    private void applyLiquibaseChangelist(Connection connection, String changelistClasspathResource) throws LiquibaseException {
        applyLiquibaseChangelist(connection, changelistClasspathResource, getClass().getClassLoader());
    }

}
