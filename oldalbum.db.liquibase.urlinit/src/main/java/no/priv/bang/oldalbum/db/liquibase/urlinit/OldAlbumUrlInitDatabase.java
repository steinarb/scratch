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
package no.priv.bang.oldalbum.db.liquibase.urlinit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import liquibase.Scope;
import liquibase.Scope.ScopedRunner;
import liquibase.changelog.ChangeLogParameters;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DatabaseChangelogCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionArgumentsCommandStep;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.sdk.resource.MockResourceAccessor;
import no.priv.bang.oldalbum.services.OldAlbumException;
import no.priv.bang.osgi.service.adapters.logservice.LoggerAdapter;

@Component(immediate=true)
public class OldAlbumUrlInitDatabase {
    LoggerAdapter logger = new LoggerAdapter(getClass());
    private DataSource datasource;
    private Environment environment;
    private HttpConnectionFactory connectionFactory;

    @Reference
    public void setLogService(LogService logservice) {
        this.logger.setLogService(logservice);
    }

    @Reference(target = "(osgi.jndi.service.name=jdbc/oldalbum)")
    public void setDatasource(DataSource datasource) {
        this.datasource = datasource;
    }

    @Activate
    public void activate() {
        var contentLiquibaseChangelog = fetchDatabaseContentFromUrl();
        if (contentLiquibaseChangelog != null) {
            setDatabaseContent(contentLiquibaseChangelog);
        }
    }

    private String fetchDatabaseContentFromUrl() {
        var databaseContentUrl = getenv("DATABASE_CONTENT_URL");
        if (!nullOrEmpty(databaseContentUrl)) {
            try {
                var connection = getConnectionFactory().connect(databaseContentUrl);
                var statusCode = connection.getResponseCode();
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    var content = connection.getInputStream();
                    var builder = new StringBuilder();
                    try(var reader = new BufferedReader(new InputStreamReader(content, StandardCharsets.UTF_8))) {
                        int c = 0;
                        while ((c = reader.read()) != -1) {
                            builder.append((char) c);
                        }
                    }

                    return builder.toString();
                } else {
                    var message = String.format("Failed to load oldalbum database content because HTTP statuscode of %s was %d", databaseContentUrl, statusCode);
                    throw new OldAlbumException(message);
                }
            } catch (IOException e) {
                var message = String.format("Failed to load oldalbum database content because loading from %s failed", databaseContentUrl);
                throw new OldAlbumException(message, e);
            }
        } else {
            throw new OldAlbumException("Failed to load oldalbum database content because DATABASE_CONTENT_URL wasn't set");
        }
    }

    private void setDatabaseContent(String contentLiquibaseChangelog) {
        var contentByFileName = new HashMap<String, String>();
        contentByFileName.put("dumproutes.sql", contentLiquibaseChangelog);
        try(var connection = datasource.getConnection()) {
            try(var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection))) {
                Map<String, Object> scopeObjects = Map.of(
                    Scope.Attr.database.name(), database,
                    Scope.Attr.resourceAccessor.name(), new MockResourceAccessor(contentByFileName));

                Scope.child(scopeObjects, (ScopedRunner<?>) () -> new CommandScope("update")
                            .addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, database)
                            .addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, "dumproutes.sql")
                            .addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, new ChangeLogParameters(database))
                            .execute());
            }
        } catch (Exception e) {
            throw new OldAlbumException("Failed to load database from", e);
        }
    }

    static boolean nullOrEmpty(String s) {
        return s == null || s.isBlank();
    }

    private String getenv(String variableName) {
        return getEnvironment().getEnv(variableName);
    }

    Environment getEnvironment() {
        if (environment == null) {
            setEnvironment(System::getenv);
        }

        return environment;
    }

    void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    private HttpConnectionFactory getConnectionFactory() {
        if (connectionFactory == null) {
            connectionFactory = new HttpConnectionFactory() {

                    @Override
                    public HttpURLConnection connect(String url) throws IOException {
                        return (HttpURLConnection) new URL(url).openConnection();
                    }
                };
        }
        return connectionFactory;
    }

    void setConnectionFactory(HttpConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

}
