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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;
import no.priv.bang.oldalbum.db.liquibase.test.OldAlbumDerbyTestDatabase;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class OldAlbumServiceProviderTest {

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
    void testFetchAllRoutes() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();
        List<AlbumEntry> allroutes = provider.fetchAllRoutes();
        assertThat(allroutes.size()).isGreaterThan(20);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFetchAllRoutesWithDatabaseFailure() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        DataSource datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate();
        List<AlbumEntry> allroutes = provider.fetchAllRoutes();
        assertEquals(1, logservice.getLogmessages().size());
        assertEquals(0, allroutes.size());
    }

    @Test
    void testGetPaths() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();
        List<String> paths = provider.getPaths();
        assertThat(paths.size()).isGreaterThan(20);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetPathsWithDatabaseFailure() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        DataSource datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate();
        List<String> paths = provider.getPaths();
        assertEquals(1, logservice.getLogmessages().size());
        assertEquals(0, paths.size());
    }

    @Test
    void testGetAlbumEntryFromPath() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();
        AlbumEntry entry = provider.getAlbumEntryFromPath("/moto/places/");
        assertEquals(3, entry.getId());
    }

    @Test
    void testGetAlbumEntryFromPathWithPathNotMatching() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();
        AlbumEntry entry = provider.getAlbumEntryFromPath("/path/not/matching/");
        assertNull(entry);
        assertEquals(1, logservice.getLogmessages().size());
        assertThat(logservice.getLogmessages().get(0)).contains("Found no albumentry matching path");
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetAlbumEntryFromPathWithDatabaseFailure() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        DataSource datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate();
        AlbumEntry entry = provider.getAlbumEntryFromPath("/moto/places/");
        assertNull(entry);
        assertEquals(1, logservice.getLogmessages().size());
        assertThat(logservice.getLogmessages().get(0)).contains("Failed to find albumentry with path");
    }

    @Test
    void testFetchChildren() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();
        List<AlbumEntry> children = provider.getChildren(3);
        assertEquals(4, children.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFetchChildrenWithDatabaseFailure() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        DataSource datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate();
        List<AlbumEntry> children = provider.getChildren(3);
        assertEquals(0, children.size());
        assertEquals(1, logservice.getLogmessages().size());
    }

    @Test
    void testUpdateEntry() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();
        AlbumEntry modifiedAlbum = new AlbumEntry(2, 1, "/moto/", true, "Album has been updated", "This is an updated description", null, null);
        List<AlbumEntry> allroutes = provider.updateEntry(modifiedAlbum);
        AlbumEntry updatedAlbum = allroutes.stream().filter(r -> r.getId() == 2).findFirst().get();
        assertEquals(modifiedAlbum.getTitle(), updatedAlbum.getTitle());
        assertEquals(modifiedAlbum.getDescription(), updatedAlbum.getDescription());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testUpdateEntryWithDataSourceFailure() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        DataSource datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate();
        AlbumEntry modifiedAlbum = new AlbumEntry(357, 1, "/moto/", true, "Album has been updated", "This is an updated description", null, null);
        List<AlbumEntry> allroutes = provider.updateEntry(modifiedAlbum);
        assertEquals(0, allroutes.size());
        assertEquals(2, logservice.getLogmessages().size());
        assertThat(logservice.getLogmessages().get(0)).contains("Failed to update album entry for id");
    }

    @Test
    void testAddEntry() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();
        int numberOfEntriesBeforeAdd = provider.fetchAllRoutes().size();
        AlbumEntry albumToAdd = new AlbumEntry(0, 1, "/newalbum/", true, "A new album", "A new album for new pictures", null, null);
        List<AlbumEntry> allroutes = provider.addEntry(albumToAdd);
        assertThat(allroutes.size()).isGreaterThan(numberOfEntriesBeforeAdd);
        AlbumEntry addedAlbum = allroutes.stream().filter(r -> "/newalbum/".equals(r.getPath())).findFirst().get();
        assertNotEquals(0, addedAlbum.getId());
        assertEquals(1, addedAlbum.getParent());
        assertEquals(albumToAdd.getTitle(), addedAlbum.getTitle());
        assertEquals(albumToAdd.getDescription(), addedAlbum.getDescription());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testAddEntryWithDataSourceFailure() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        DataSource datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate();
        AlbumEntry albumToAdd = new AlbumEntry(0, 1, "/newalbum/", true, "A new album", "A new album for new pictures", null, null);
        List<AlbumEntry> allroutes = provider.addEntry(albumToAdd);
        assertEquals(0, allroutes.size());
        assertEquals(2, logservice.getLogmessages().size());
        assertThat(logservice.getLogmessages().get(0)).contains("Failed to add album entry with path");
    }

}
