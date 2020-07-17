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
package no.priv.bang.oldalbum.backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.priv.bang.oldalbum.db.liquibase.test.OldAlbumDerbyTestDatabase;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class OldAlbumServiceProviderTest {

	static ObjectMapper mapper = new ObjectMapper();
    private static DataSource datasource;

    @BeforeAll
    static void setupDataSource() throws Exception {
        DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:oldalbum;create=true");
        datasource = derbyDataSourceFactory.createDataSource(properties);
        MockLogService logservice = new MockLogService();
        OldAlbumDerbyTestDatabase preHook = new OldAlbumDerbyTestDatabase();
        preHook.setLogService(logservice);
        preHook.activate();
        preHook.prepare(datasource);
    }

    @Test
    void testFetchRoutes() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();
        List<AlbumEntry> allroutes = provider.fetchRoutes();
        System.err.println(mapper.writeValueAsString(allroutes));
        assertEquals(21, allroutes.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFetchRoutesWithDatabaseFailure() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        DataSource datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate();
        List<AlbumEntry> allroutes = provider.fetchRoutes();
        assertEquals(1, logservice.getLogmessages().size());
        assertEquals(0, allroutes.size());
    }

}
