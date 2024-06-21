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
package no.priv.bang.oldalbum.db.liquibase;

import java.sql.Connection;
import liquibase.exception.LiquibaseException;
import no.priv.bang.karaf.liquibase.runner.LiquibaseClassPathChangeLogRunner;

public class OldAlbumLiquibase extends LiquibaseClassPathChangeLogRunner {

    public void createInitialSchema(Connection connection) throws LiquibaseException {
        applyLiquibaseChangeLog(connection, "oldalbum/changelog01.xml");
    }

    public void updateSchema(Connection connection) throws LiquibaseException {
        applyLiquibaseChangeLog(connection, "oldalbum/changelog02.xml");
    }

    private void applyLiquibaseChangeLog(Connection connection, String changelogClasspathResource) throws LiquibaseException {
        applyLiquibaseChangelist(connection, changelogClasspathResource, getClass().getClassLoader());
    }

}
