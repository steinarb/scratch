/*
 * Copyright 2020-2021 Steinar Bang
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.sdk.resource.MockResourceAccessor;
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
        String contentLiquibaseChangelog = fetchDatabaseContentFromUrl();
        if (contentLiquibaseChangelog != null) {
            setDatabaseContent(contentLiquibaseChangelog);
        }
    }

    private String fetchDatabaseContentFromUrl() {
        String databaseContentUrl = getenv("DATABASE_CONTENT_URL");
        if (!nullOrEmpty(databaseContentUrl)) {
            try {
                HttpURLConnection connection = getConnectionFactory().connect(databaseContentUrl);
                int statusCode = connection.getResponseCode();
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    InputStream content = connection.getInputStream();
                    StringBuilder builder = new StringBuilder();
                    try(Reader reader = new BufferedReader(new InputStreamReader(content, StandardCharsets.UTF_8))) {
                        int c = 0;
                        while ((c = reader.read()) != -1) {
                            builder.append((char) c);
                        }
                    }
                    return builder.toString();
                } else {
                    logger.error(String.format("Failed to load oldalbum database content because HTTP statuscode of %s was %d", databaseContentUrl, statusCode));
                    return null;
                }
            } catch (IOException e) {
                logger.error(String.format("Failed to load oldalbum database content because loading from %s failed", databaseContentUrl), e);
                return null;
            }
        } else {
            logger.error("Failed to load oldalbum database content because DATABASE_CONTENT_URL wasn't set");
            return null;
        }
    }

    private void setDatabaseContent(String contentLiquibaseChangelog) {
        Map<String, String> contentByFileName = new HashMap<>();
        contentByFileName.put("dumproutes.sql", contentLiquibaseChangelog);
        MockResourceAccessor accessor = new MockResourceAccessor(contentByFileName);
        try(Connection connection = datasource.getConnection()) {
            DatabaseConnection database = new JdbcConnection(connection);
            Liquibase liquibase = new Liquibase("dumproutes.sql", accessor, database);
            liquibase.update("");
        } catch (Exception e) {
            logger.error("Failed to insert oldalbum database content into the database");
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
