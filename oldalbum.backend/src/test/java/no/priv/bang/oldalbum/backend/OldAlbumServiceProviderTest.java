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
package no.priv.bang.oldalbum.backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.sdk.resource.MockResourceAccessor;
import no.priv.bang.oldalbum.db.liquibase.OldAlbumLiquibase;
import no.priv.bang.oldalbum.db.liquibase.test.OldAlbumDerbyTestDatabase;
import no.priv.bang.oldalbum.services.OldAlbumException;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.oldalbum.services.bean.BatchAddPicturesRequest;
import no.priv.bang.oldalbum.services.bean.ImageMetadata;
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

        // First check all routes not requiring login
        List<AlbumEntry> allroutesNotRequiringLogin = provider.fetchAllRoutes(null, false);
        assertThat(allroutesNotRequiringLogin).hasSizeGreaterThan(20);

        // Then check that all routes including those that require login has at least 3 more entries
        List<AlbumEntry> allroutesIncludingThoseRequiringLogin = provider.fetchAllRoutes(null, true);
        assertThat(allroutesIncludingThoseRequiringLogin)
            .hasSizeGreaterThanOrEqualTo(allroutesNotRequiringLogin.size() + 3);
    }

    @Test
    void testFetchAllRoutesWithDatabaseFailure() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        DataSource datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate();
        List<AlbumEntry> allroutes = provider.fetchAllRoutes(null, false);
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

        // Test paths when logged in
        List<String> pathsWhenNotLoggedIn = provider.getPaths(false);
        assertThat(pathsWhenNotLoggedIn).hasSizeGreaterThanOrEqualTo(19);

        // Test paths when not logged in
        List<String> pathsWhenLoggedIn = provider.getPaths(true);
        assertThat(pathsWhenLoggedIn).hasSize(pathsWhenNotLoggedIn.size() + 4);
    }

    @Test
    void testGetPathsWithDatabaseFailure() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        DataSource datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate();
        List<String> paths = provider.getPaths(false);
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
        assertThat(children).hasSizeGreaterThanOrEqualTo(3);
    }

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
        AlbumEntry modifiedAlbum = AlbumEntry.with()
            .id(2)
            .parent(1)
            .path("/moto/")
            .album(true)
            .title("Album has been updated")
            .description("This is an updated description")
            .sort(1)
            .contentLength(0)
            .childcount(2)
            .requireLogin(true)
            .build();
        List<AlbumEntry> allroutes = provider.updateEntry(modifiedAlbum);
        AlbumEntry updatedAlbum = allroutes.stream().filter(r -> r.getId() == 2).findFirst().get();
        assertEquals(modifiedAlbum.getTitle(), updatedAlbum.getTitle());
        assertEquals(modifiedAlbum.getDescription(), updatedAlbum.getDescription());
        assertEquals(modifiedAlbum.isRequireLogin(), updatedAlbum.isRequireLogin());
    }

    @Test
    void testUpdatePicture() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        var originalPicture = provider.fetchAllRoutes(null, false).stream().filter(r -> r.getId() == 5).findFirst().get();
        String modifiedTitle = "New picture title";
        String modifiedDescription = "This is an updated description";
        var modifiedDate = Date.from(LocalDateTime.now().minusDays(5).toInstant(ZoneOffset.UTC));
        boolean requireLogin = true;
        AlbumEntry modifiedPicture = AlbumEntry.with(originalPicture)
            .title(modifiedTitle)
            .description(modifiedDescription)
            .lastModified(modifiedDate)
            .requireLogin(requireLogin)
            .build();
        List<AlbumEntry> allroutes = provider.updateEntry(modifiedPicture);
        AlbumEntry updatedPicture = allroutes.stream().filter(r -> r.getId() == 5).findFirst().get();
        assertEquals(modifiedTitle, updatedPicture.getTitle());
        assertEquals(modifiedDescription, updatedPicture.getDescription());
        assertEquals(modifiedDate, updatedPicture.getLastModified());
        assertEquals(requireLogin, updatedPicture.isRequireLogin());
    }

    @Test()
    void testSwitchEntryParent() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        // Verify that sort values are updated correctly when an album
        // entry is moved to a different album
        List<AlbumEntry> allroutes = provider.fetchAllRoutes(null, false);
        AlbumEntry destinationAlbum = allroutes.stream().filter(r -> "/moto/places/".equals(r.getPath())).findFirst().get();
        AlbumEntry imageToMove = allroutes.stream().filter(r -> "/moto/vfr96/acirc3".equals(r.getPath())).findFirst().get();
        int sortValueOfNextImageInOriginalAlbum = allroutes.stream().filter(r -> "/moto/vfr96/dirtroad".equals(r.getPath())).findFirst().get().getSort();
        int sortValueOfLastItemInDestinationAlbum = allroutes.stream().filter(r -> "/moto/places/hove".equals(r.getPath())).findFirst().get().getSort();

        AlbumEntry movedImage = AlbumEntry.with(imageToMove)
            .parent(destinationAlbum.getId())
            .path("/moto/places/acirc3")
            .build();
        allroutes = provider.updateEntry(movedImage);

        // Verify that sort values in the original album have been adjusted
        int sortValueOfNextImageInOriginalAlbumAfterMove = allroutes.stream().filter(r -> "/moto/vfr96/dirtroad".equals(r.getPath())).findFirst().get().getSort();        assertThat(sortValueOfNextImageInOriginalAlbum).isGreaterThan(sortValueOfNextImageInOriginalAlbumAfterMove);

        // Verify that image is sorted last in the destination album
        AlbumEntry imageInDestination = allroutes.stream().filter(r -> r.getId() == imageToMove.getId()).findFirst().get();
        assertThat(imageInDestination.getSort()).isGreaterThan(sortValueOfLastItemInDestinationAlbum);
    }

    @Test
    void testUpdateEntryWithDataSourceFailure() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        DataSource datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate();
        AlbumEntry modifiedAlbum = AlbumEntry.with()
            .id(357)
            .parent(1)
            .path("/moto/")
            .album(true)
            .title("Album has been updated")
            .description("This is an updated description")
            .sort(1)
            .lastModified(new Date())
            .contentType("image/jpeg")
            .contentLength(71072)
            .childcount(2)
            .build();
        List<AlbumEntry> allroutes = provider.updateEntry(modifiedAlbum);
        assertEquals(0, allroutes.size());
        assertEquals(2, logservice.getLogmessages().size());
        assertThat(logservice.getLogmessages().get(0)).contains("Failed to update album entry for id");
    }

    @Test
    void testAddEntryWithAlbum() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();
        int numberOfEntriesBeforeAdd = provider.fetchAllRoutes(null, false).size();
        AlbumEntry albumToAdd = AlbumEntry.with()
            .parent(1)
            .path("/newalbum/")
            .album(true)
            .title("A new album")
            .description("A new album for new pictures")
            .sort(2)
            .requireLogin(true)
            .build();
        List<AlbumEntry> allroutes = provider.addEntry(albumToAdd);
        assertThat(allroutes).hasSizeGreaterThan(numberOfEntriesBeforeAdd);
        AlbumEntry addedAlbum = allroutes.stream().filter(r -> "/newalbum/".equals(r.getPath())).findFirst().get();
        assertNotEquals(albumToAdd.getId(), addedAlbum.getId()); // Placeholder ID is replaced with an actual database id
        assertThat(addedAlbum.getId()).isPositive();
        assertEquals(1, addedAlbum.getParent());
        assertEquals(albumToAdd.getTitle(), addedAlbum.getTitle());
        assertEquals(albumToAdd.getDescription(), addedAlbum.getDescription());
        assertEquals(albumToAdd.isRequireLogin(), addedAlbum.isRequireLogin());
    }

    @Test
    void testAddEntryWithPicture() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();
        int numberOfEntriesBeforeAdd = provider.fetchAllRoutes(null, false).size();
        String imageUrl = "https://www.bang.priv.no/sb/pics/misc/sylane4.jpg";
        ImageMetadata metadata = provider.readMetadata(imageUrl);
        AlbumEntry pictureToAdd = AlbumEntry.with()
            .parent(1)
            .path("/sylane4")
            .album(false)
            .title("Sylane påsken 1995")
            .description("Fra Storerikvollen til Nedalshytta i kraftig vind")
            .imageUrl(imageUrl)
            .thumbnailUrl("https://www.bang.priv.no/sb/pics/misc/.icons/sylane4.gif")
            .sort(3)
            .lastModified(metadata.getLastModified())
            .contentType(metadata.getContentType())
            .contentLength(metadata.getContentLength())
            .requireLogin(true)
            .build();
        List<AlbumEntry> allroutes = provider.addEntry(pictureToAdd);
        assertThat(allroutes).hasSizeGreaterThan(numberOfEntriesBeforeAdd);
        AlbumEntry addedPicture = allroutes.stream().filter(r -> "/sylane4".equals(r.getPath())).findFirst().get();
        assertNotEquals(pictureToAdd.getId(), addedPicture.getId()); // Placeholder ID is replaced with an actual database id
        assertEquals(1, addedPicture.getParent());
        assertEquals(pictureToAdd.getTitle(), addedPicture.getTitle());
        assertEquals(pictureToAdd.getDescription(), addedPicture.getDescription());
        assertEquals(pictureToAdd.getLastModified(), addedPicture.getLastModified());
        assertEquals(pictureToAdd.getContentType(), addedPicture.getContentType());
        assertEquals(pictureToAdd.getContentLength(), addedPicture.getContentLength());
        assertEquals(pictureToAdd.isRequireLogin(), pictureToAdd.isRequireLogin());
    }

    @Test
    void testAddEntryWithPictureWhenPictureIs404() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();
        int numberOfEntriesBeforeAdd = provider.fetchAllRoutes(null, false).size();
        AlbumEntry pictureToAdd = AlbumEntry.with()
            .parent(1)
            .path("/sylane5")
            .album(false)
            .title("Sylane påsken 1996")
            .description("Ut på ski, alltid blid")
            .imageUrl("https://www.bang.priv.no/sb/pics/misc/sylane5.jpg")
            .thumbnailUrl("https://www.bang.priv.no/sb/pics/misc/.icons/sylane5.gif")
            .sort(4)
            .build();
        List<AlbumEntry> allroutes = provider.addEntry(pictureToAdd);
        assertThat(allroutes).hasSizeGreaterThan(numberOfEntriesBeforeAdd);
        AlbumEntry addedPicture = allroutes.stream().filter(r -> "/sylane5".equals(r.getPath())).findFirst().get();
        assertNotEquals(pictureToAdd.getId(), addedPicture.getId()); // Placeholder ID is replaced with an actual database id
        assertEquals(1, addedPicture.getParent());
        assertEquals(pictureToAdd.getTitle(), addedPicture.getTitle());
        assertEquals(pictureToAdd.getDescription(), addedPicture.getDescription());
        assertNull(addedPicture.getLastModified());
    }

    @Test
    void testAddEntryWithPictureWhenPictureIs500() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        // Mocked HTTP request
        HttpConnectionFactory connectionFactory = mock(HttpConnectionFactory.class);
        HttpURLConnection connection = mock(HttpURLConnection.class);
        when(connection.getResponseCode()).thenReturn(500);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        provider.setConnectionFactory(connectionFactory);

        int numberOfEntriesBeforeAdd = provider.fetchAllRoutes(null, false).size();
        AlbumEntry pictureToAdd = AlbumEntry.with()
            .parent(1)
            .path("/sylane5")
            .album(false)
            .title("Sylane påsken 1996")
            .description("Ut på ski, alltid blid")
            .imageUrl("https://www.bang.priv.no/sb/pics/misc/sylane5.jpg")
            .thumbnailUrl("https://www.bang.priv.no/sb/pics/misc/.icons/sylane5.gif")
            .sort(4)
            .build();
        List<AlbumEntry> allroutes = provider.addEntry(pictureToAdd);
        assertThat(allroutes).hasSizeGreaterThan(numberOfEntriesBeforeAdd);
        AlbumEntry addedPicture = allroutes.stream().filter(r -> "/sylane5".equals(r.getPath())).findFirst().get();
        assertNotEquals(pictureToAdd.getId(), addedPicture.getId()); // Placeholder ID is replaced with an actual database id
        assertEquals(1, addedPicture.getParent());
        assertEquals(pictureToAdd.getTitle(), addedPicture.getTitle());
        assertEquals(pictureToAdd.getDescription(), addedPicture.getDescription());
        assertNull(addedPicture.getLastModified());
    }

    @Test
    void testAddEntryWithPictureWhenPictureIsFoundButNotAnyMetadata() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();
        int numberOfEntriesBeforeAdd = provider.fetchAllRoutes(null, false).size();
        AlbumEntry pictureToAdd = AlbumEntry.with()
            .parent(1)
            .path("/sylane5")
            .album(false)
            .title("Sylane påsken 1996")
            .description("Ut på ski, alltid blid")
            .imageUrl("https://www.bang.priv.no/sb/pics/misc/sylane5.jpg")
            .thumbnailUrl("https://www.bang.priv.no/sb/pics/misc/.icons/sylane5.gif")
            .sort(4)
            .build();
        List<AlbumEntry> allroutes = provider.addEntry(pictureToAdd);
        assertThat(allroutes).hasSizeGreaterThan(numberOfEntriesBeforeAdd);
        AlbumEntry addedPicture = allroutes.stream().filter(r -> "/sylane5".equals(r.getPath())).findFirst().get();
        assertNotEquals(pictureToAdd.getId(), addedPicture.getId()); // Placeholder ID is replaced with an actual database id
        assertEquals(1, addedPicture.getParent());
        assertEquals(pictureToAdd.getTitle(), addedPicture.getTitle());
        assertEquals(pictureToAdd.getDescription(), addedPicture.getDescription());
        assertNull(addedPicture.getLastModified());
    }

    @Test
    void testAddEntryWithDataSourceFailure() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        DataSource datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate();
        AlbumEntry albumToAdd = AlbumEntry.with()
            .parent(1)
            .path("/newalbum/")
            .album(true)
            .title("A new album")
            .description("A new album for new pictures")
            .sort(2)
            .build();
        List<AlbumEntry> allroutes = provider.addEntry(albumToAdd);
        assertEquals(0, allroutes.size());
        assertEquals(2, logservice.getLogmessages().size());
        assertThat(logservice.getLogmessages().get(0)).contains("Failed to add album entry with path");
    }

    @Test
    void testDeleteEntry() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();
        int numberOfEntriesBeforeDelete = provider.fetchAllRoutes(null, true).size();
        AlbumEntry pictureToDelete = AlbumEntry.with()
            .id(7)
            .parent(3)
            .path("/oldalbum/moto/places/grava3")
            .album(false)
            .title("")
            .description("Tyrigrava, view from the north. Lotsa bikes here too")
            .imageUrl("https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg")
            .thumbnailUrl("https://www.bang.priv.no/sb/pics/moto/places/icons/grava3.gif")
            .sort(1)
            .lastModified(new Date())
            .contentType("image/jpeg")
            .contentLength(71072)
            .childcount(0)
            .build();
        List<AlbumEntry> allroutes = provider.deleteEntry(pictureToDelete);
        assertThat(allroutes).hasSizeLessThan(numberOfEntriesBeforeDelete);
        Optional<AlbumEntry> deletedPicture = allroutes.stream().filter(r -> r.getId() == 7).findFirst();
        assertFalse(deletedPicture.isPresent());
    }

    @Test
    void testDeleteEntryWithDataSourceFailure() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        DataSource datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate();
        int numberOfEntriesBeforeDelete = provider.fetchAllRoutes(null, false).size();
        AlbumEntry pictureToDelete = AlbumEntry.with()
            .id(7)
            .parent(3)
            .path("/oldalbum/moto/places/grava3")
            .album(false)
            .title("")
            .description("Tyrigrava, view from the north. Lotsa bikes here too")
            .imageUrl("https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg")
            .thumbnailUrl("https://www.bang.priv.no/sb/pics/moto/places/icons/grava3.gif")
            .sort(1)
            .lastModified(new Date())
            .contentType("image/jpeg")
            .contentLength(71072)
            .childcount(0)
            .build();
        List<AlbumEntry> allroutes = provider.deleteEntry(pictureToDelete);
        assertEquals(numberOfEntriesBeforeDelete, allroutes.size());
        assertEquals(3, logservice.getLogmessages().size());
        assertThat(logservice.getLogmessages().get(1)).contains("Failed to delete album entry with id");
    }

    @Test
    void testMoveAlbumEntriesUp() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        List<AlbumEntry> allroutes = provider.fetchAllRoutes(null, false);
        // Find the first and second entries of the "vfr" album
        AlbumEntry originalFirstEntry = allroutes.stream().filter(r -> "/moto/vfr96/acirc1".equals(r.getPath())).findFirst().get();
        assertEquals(1, originalFirstEntry.getSort());
        AlbumEntry secondEntry = allroutes.stream().filter(r -> "/moto/vfr96/acirc2".equals(r.getPath())).findFirst().get();
        assertEquals(2, secondEntry.getSort());

        // Move from second to first
        allroutes = provider.moveEntryUp(secondEntry);
        secondEntry = allroutes.stream().filter(r -> "/moto/vfr96/acirc2".equals(r.getPath())).findFirst().get();
        assertEquals(1, secondEntry.getSort());
        originalFirstEntry = allroutes.stream().filter(r -> "/moto/vfr96/acirc1".equals(r.getPath())).findFirst().get();
        assertEquals(2, originalFirstEntry.getSort());

        // Corner case test: Trying to move up from the first entry of the album
        // This should have no effect (and should not crash)
        allroutes = provider.moveEntryUp(secondEntry);
        secondEntry = allroutes.stream().filter(r -> "/moto/vfr96/acirc2".equals(r.getPath())).findFirst().get();
        assertEquals(1, secondEntry.getSort());
        originalFirstEntry = allroutes.stream().filter(r -> "/moto/vfr96/acirc1".equals(r.getPath())).findFirst().get();
        assertEquals(2, originalFirstEntry.getSort());
    }

    @Test
    void testMoveAlbumEntriesUpWithDatabaseFailure() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        DataSource datasourceThrowsException = mock(DataSource.class);
        when(datasourceThrowsException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsException);
        provider.activate();

        // Try moving an album and failing
        List<AlbumEntry> allroutes = provider.moveEntryUp(AlbumEntry.with().id(0).parent(1).sort(10).childcount(10).build());
        assertEquals(0, allroutes.size());
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).contains("Failed to move album entry with id");
    }

    @Test
    void testMoveAlbumEntriesDown() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        List<AlbumEntry> allroutes = provider.fetchAllRoutes(null, false);
        // Find the last and second to last entries of the "vfr" album
        int numberOfAlbumentriesInAlbum = allroutes.stream().filter(r -> "/moto/vfr96/".equals(r.getPath())).findFirst().get().getChildcount();
        AlbumEntry originalLastEntry = allroutes.stream().filter(r -> "/moto/vfr96/wintervfr-ef".equals(r.getPath())).findFirst().get();
        assertEquals(numberOfAlbumentriesInAlbum, originalLastEntry.getSort());
        AlbumEntry secondToLastEntry = allroutes.stream().filter(r -> "/moto/vfr96/vfr2".equals(r.getPath())).findFirst().get();
        assertEquals(numberOfAlbumentriesInAlbum - 1, secondToLastEntry.getSort());

        // Move from second to last position to last position
        allroutes = provider.moveEntryDown(secondToLastEntry);
        secondToLastEntry = allroutes.stream().filter(r -> "/moto/vfr96/vfr2".equals(r.getPath())).findFirst().get();
        assertEquals(numberOfAlbumentriesInAlbum, secondToLastEntry.getSort());
        originalLastEntry = allroutes.stream().filter(r -> "/moto/vfr96/wintervfr-ef".equals(r.getPath())).findFirst().get();
        assertEquals(numberOfAlbumentriesInAlbum - 1, originalLastEntry.getSort());

        // Corner case test: Trying to move down from the last entry of the album
        // This should have no effect (and should not crash)
        allroutes = provider.moveEntryDown(secondToLastEntry);
        secondToLastEntry = allroutes.stream().filter(r -> "/moto/vfr96/vfr2".equals(r.getPath())).findFirst().get();
        assertEquals(numberOfAlbumentriesInAlbum, secondToLastEntry.getSort());
        originalLastEntry = allroutes.stream().filter(r -> "/moto/vfr96/wintervfr-ef".equals(r.getPath())).findFirst().get();
        assertEquals(numberOfAlbumentriesInAlbum - 1, originalLastEntry.getSort());
    }

    @Test
    void testMoveAlbumEntriesDownWithDatabaseFailure() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        DataSource datasourceThrowsException = mock(DataSource.class);
        when(datasourceThrowsException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsException);
        provider.activate();

        List<AlbumEntry> allroutes = provider.moveEntryDown(AlbumEntry.with().id(0).parent(1).sort(10).childcount(10).build());
        assertEquals(0, allroutes.size());
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).contains("Failed to move album entry with id");
    }

    @Test
    void testAdjustSortValuesWhenMovingToDifferentAlbumNoExistingParent() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        AlbumEntry updatedEntry = AlbumEntry.with()
            .id(7)
            .parent(3)
            .path("/oldalbum/moto/places/grava3")
            .album(false)
            .title("")
            .description("Tyrigrava, view from the north. Lotsa bikes here too")
            .imageUrl("https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg")
            .thumbnailUrl("https://www.bang.priv.no/sb/pics/moto/places/icons/grava3.gif")
            .sort(1)
            .lastModified(new Date())
            .contentType("image/jpeg")
            .contentLength(71072)
            .childcount(0)
            .build();
        int sort = provider.adjustSortValuesWhenMovingToDifferentAlbum(connection, updatedEntry);
        assertEquals(updatedEntry.getSort(), sort);
    }

    @Test
    void testFindNumberOfEntriesInAlbumEmptyResultSet() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        int entryCount = provider.findNumberOfEntriesInAlbum(connection, 0);
        assertEquals(0, entryCount);
    }

    @Test
    void testFindPreviousEntryInTheSameAlbumEmptyResultSet() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        int entryCount = provider.findPreviousEntryInTheSameAlbum(connection, AlbumEntry.with().build(), 2);
        assertEquals(0, entryCount);
    }

    @Test
    void testFindNextEntryInTheSameAlbumResultSet() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        int entryCount = provider.findNextEntryInTheSameAlbum(connection, AlbumEntry.with().build(), 2);
        assertEquals(0, entryCount);
    }

    @Test
    void testGetEntryWhenEntryNotFound() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        AlbumEntry entry = provider.getEntry(connection, 0);
        assertNull(entry);
    }

    @Test
    void testReadImageMetadata() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);

        String imageUrl = "https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg";
        ImageMetadata metadata = provider.readMetadata(imageUrl);
        assertEquals(200, metadata.getStatus());
        assertThat(metadata.getLastModified()).isAfter(Date.from(Instant.EPOCH));
        assertEquals("image/jpeg", metadata.getContentType());
        assertThat(metadata.getContentLength()).isPositive();
    }

    @Test
    void testReadImageMetadataImageNotFound() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);

        String imageUrl = "https://www.bang.priv.no/sb/pics/moto/places/gravva1.jpg";
        ImageMetadata metadata = provider.readMetadata(imageUrl);
        assertEquals(404, metadata.getStatus());
        assertEquals(Date.from(Instant.EPOCH), metadata.getLastModified());
        assertEquals("text/html", metadata.getContentType());
        assertThat(metadata.getContentLength()).isPositive();
    }

    @Test
    void testReadImageMetadataServerNotFound() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);

        String imageUrl = "https://www.bang.priv.com/sb/pics/moto/places/gravva1.jpg";
        ImageMetadata metadata = provider.readMetadata(imageUrl);
        assertNull(metadata);
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).contains("Error when reading metadata");
    }

    @Test
    void testReadImageMetadataWithNullImageUrl() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);

        ImageMetadata metadata = provider.readMetadata(null);
        assertNull(metadata);
    }

    @Test
    void testReadImageMetadataWithEmptyImageUrl() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);

        ImageMetadata metadata = provider.readMetadata("");
        assertNull(metadata);
    }

    @Test
    void testDumpDatabaseSqlNotLoggedIn() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        int allroutesCount = findAlbumentriesRows(datasource, false);
        String sql = provider.dumpDatabaseSql(null, false);
        assertThat(sql)
            .contains("insert into")
            .hasLineCount(allroutesCount + 3);

        // Create an empty database initialized with the oldalbum schema
        // Then use liquibase to fill the database with the dumped content
        DataSource emptybase = createEmptyBase("emptyoldalbum1");
        int rowsBeforeInsert = findAlbumentriesRows(emptybase, false);
        assertEquals(0, rowsBeforeInsert);
        Map<String, String> contentByFileName = new HashMap<>();
        contentByFileName.put("dumproutes.sql", sql);
        MockResourceAccessor accessor = new MockResourceAccessor(contentByFileName);
        try(Connection connection = emptybase.getConnection()) {
            DatabaseConnection database = new JdbcConnection(connection);
            try(var liquibase = new Liquibase("dumproutes.sql", accessor, database)) {
                liquibase.update("");
            }
        }

        // Check that the empty database now has the same number of rows as the original
        int rowsInOriginal = findAlbumentriesRows(datasource, false);
        int rowsAfterInsert = findAlbumentriesRows(emptybase, false);
        assertEquals(rowsInOriginal, rowsAfterInsert);

        // Try inserting a row to verify that the id autoincrement doesn't
        // create duplicated
        try(Connection connection = emptybase.getConnection()) {
            addAlbumEntry(connection, 0, "/album/", true, "Album", "This is an album", null, null, 1, null, null, 0);
        }
        int rowsAfterInsertingExtraRow = findAlbumentriesRows(emptybase, false);
        assertThat(rowsAfterInsertingExtraRow).isGreaterThan(rowsInOriginal);
    }

    @Test
    void testDumpDatabaseSqlLoggedIn() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        int allroutesCount = findAlbumentriesRows(datasource, true);
        String sql = provider.dumpDatabaseSql(null, true);
        assertThat(sql)
            .contains("insert into")
            .hasLineCount(allroutesCount + 3);

        // Create an empty database initialized with the oldalbum schema
        // Then use liquibase to fill the database with the dumped content
        DataSource emptybase = createEmptyBase("emptyoldalbum2");
        int rowsBeforeInsert = findAlbumentriesRows(emptybase, false);
        assertEquals(0, rowsBeforeInsert);
        Map<String, String> contentByFileName = new HashMap<>();
        contentByFileName.put("dumproutes.sql", sql);
        MockResourceAccessor accessor = new MockResourceAccessor(contentByFileName);
        try(Connection connection = emptybase.getConnection()) {
            DatabaseConnection database = new JdbcConnection(connection);
            try(var liquibase = new Liquibase("dumproutes.sql", accessor, database)) {
                liquibase.update("");
            }
        }

        // Check that the empty database now has the same number of rows as the original
        int rowsInOriginal = findAlbumentriesRows(datasource, true);
        int rowsAfterInsert = findAlbumentriesRows(emptybase, true);
        assertEquals(rowsInOriginal, rowsAfterInsert);

        // Try inserting a row to verify that the id autoincrement doesn't
        // create duplicated
        try(Connection connection = emptybase.getConnection()) {
            addAlbumEntry(connection, 0, "/album/", true, "Album", "This is an album", null, null, 1, null, null, 0);
        }
        int rowsAfterInsertingExtraRow = findAlbumentriesRows(emptybase, true);
        assertThat(rowsAfterInsertingExtraRow).isGreaterThan(rowsInOriginal);
    }

    @Test
    void testBatchAddPictures() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        var database = createEmptyBase("emptyoldalbum3");
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(database);
        provider.activate();

        // Mocked HTTP request
        HttpConnectionFactory connectionFactory = mock(HttpConnectionFactory.class);
        HttpURLConnection connection = mock(HttpURLConnection.class);
        when(connection.getResponseCode()).thenReturn(200);
        when(connection.getInputStream()).thenReturn(getClass().getClassLoader().getResourceAsStream("html/pictures_directory_list_nginx_index.html"));
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        provider.setConnectionFactory(connectionFactory);

        // Prepare empty database with an album to put pictures in
        AlbumEntry parentForBatchAddedPictures = AlbumEntry.with()
            .parent(1)
            .path("/pictures/")
            .album(true)
            .title("A lot of pictures")
            .description("Pictures added using the batch functionality")
            .sort(2)
            .requireLogin(true)
            .build();
        var entriesBeforeBatchAdd = provider.addEntry(parentForBatchAddedPictures);
        var parentId = entriesBeforeBatchAdd.get(0).getId();

        // Do the batch import
        var request = BatchAddPicturesRequest.with()
            .parent(parentId)
            .batchAddUrl("http://lorenzo.hjemme.lan/bilder/202349_001396/Export%20JPG%2016Base/")
            .build();
        var entriesAfterBatchAdd = provider.batchAddPictures(request);

        // Check that pictures have been added
        assertThat(entriesAfterBatchAdd).hasSizeGreaterThan(entriesBeforeBatchAdd.size());
    }

    @Test
    void testBatchAddPicturesWith404OnTheBatchUrl() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        var database = createEmptyBase("emptyoldalbum3");
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(database);
        provider.activate();

        // Mocked HTTP request
        HttpConnectionFactory connectionFactory = mock(HttpConnectionFactory.class);
        HttpURLConnection connection = mock(HttpURLConnection.class);
        when(connection.getResponseCode()).thenReturn(404);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        provider.setConnectionFactory(connectionFactory);

        // Do the batch import
        var request = BatchAddPicturesRequest.with()
            .parent(1)
            .batchAddUrl("http://lorenzo.hjemme.lan/bilder/202349_001396/Export%20JPG%2016Base/")
            .build();
        var e = assertThrows(OldAlbumException.class, () -> provider.batchAddPictures(request));
        assertThat(e.getMessage()).startsWith("Got HTTP error when requesting the batch add pictures URL, statuscode: 404");
    }

    @Test
    void testBatchAddPicturesWithIOExceptionOnReceivedFileParse() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        var database = createEmptyBase("emptyoldalbum3");
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(database);
        provider.activate();

        // Mocked HTTP request
        HttpConnectionFactory connectionFactory = mock(HttpConnectionFactory.class);
        HttpURLConnection connection = mock(HttpURLConnection.class);
        when(connection.getResponseCode()).thenReturn(200);
        when(connection.getInputStream()).thenThrow(IOException.class);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        provider.setConnectionFactory(connectionFactory);

        // Do the batch import
        var request = BatchAddPicturesRequest.with()
            .parent(1)
            .batchAddUrl("http://lorenzo.hjemme.lan/bilder/202349_001396/Export%20JPG%2016Base/")
            .build();
        var e = assertThrows(OldAlbumException.class, () -> provider.batchAddPictures(request));
        assertThat(e.getMessage()).startsWith("Got error parsing the content of URL:");
    }

    private int findAlbumentriesRows(DataSource ds, boolean isLoggedIn) throws SQLException {
        String sql = "select count(albumentry_id) from albumentries where (not require_login or (require_login and require_login=?))";
        try (Connection connection = ds.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, isLoggedIn);
                try (ResultSet results = statement.executeQuery()) {
                    if (results.next()) {
                        return results.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    private DataSource createEmptyBase(String dbname) throws Exception {
        DataSourceFactory derbyDataSourceFactory = new DerbyDataSourceFactory();
        Properties properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + ";create=true");
        DataSource emptyDatasource = derbyDataSourceFactory.createDataSource(properties);
        try (Connection connection = emptyDatasource.getConnection()) {
            OldAlbumLiquibase oldAlbumLiquibase = new OldAlbumLiquibase();
            oldAlbumLiquibase.createInitialSchema(connection);
        }
        try (Connection connection = emptyDatasource.getConnection()) {
            OldAlbumLiquibase oldAlbumLiquibase = new OldAlbumLiquibase();
            oldAlbumLiquibase.updateSchema(connection);
        }
        return emptyDatasource;
    }

    private void addAlbumEntry(Connection connection, int parent, String path, boolean album, String title, String description, String imageUrl, String thumbnailUrl, int sort, Date lastmodified, String contenttype, int size) throws Exception {
        String sql = "insert into albumentries (parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, parent);
            statement.setString(2, path);
            statement.setBoolean(3, album);
            statement.setString(4, title);
            statement.setString(5, description);
            statement.setString(6, imageUrl);
            statement.setString(7, thumbnailUrl);
            statement.setInt(8, sort);
            statement.setTimestamp(9, lastmodified != null ? Timestamp.from(Instant.ofEpochMilli(lastmodified.getTime())) : null);
            statement.setString(10, contenttype);
            statement.setInt(11, size);
            statement.executeUpdate();
        }
    }

}
