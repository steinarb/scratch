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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;
import no.priv.bang.oldalbum.db.liquibase.test.OldAlbumDerbyTestDatabase;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;
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
        assertThat(paths.size()).isGreaterThanOrEqualTo(20);
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
        assertThat(children.size()).isGreaterThanOrEqualTo(3);
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
        AlbumEntry modifiedAlbum = new AlbumEntry(2, 1, "/moto/", true, "Album has been updated", "This is an updated description", null, null, 1, 2);
        List<AlbumEntry> allroutes = provider.updateEntry(modifiedAlbum);
        AlbumEntry updatedAlbum = allroutes.stream().filter(r -> r.getId() == 2).findFirst().get();
        assertEquals(modifiedAlbum.getTitle(), updatedAlbum.getTitle());
        assertEquals(modifiedAlbum.getDescription(), updatedAlbum.getDescription());
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
        List<AlbumEntry> allroutes = provider.fetchAllRoutes();
        AlbumEntry destinationAlbum = allroutes.stream().filter(r -> "/moto/places/".equals(r.getPath())).findFirst().get();
        AlbumEntry imageToMove = allroutes.stream().filter(r -> "/moto/vfr96/acirc3".equals(r.getPath())).findFirst().get();
        int sortValueOfNextImageInOriginalAlbum = allroutes.stream().filter(r -> "/moto/vfr96/dirtroad".equals(r.getPath())).findFirst().get().getSort();
        int sortValueOfLastItemInDestinationAlbum = allroutes.stream().filter(r -> "/moto/places/hove".equals(r.getPath())).findFirst().get().getSort();

        AlbumEntry movedImage = new AlbumEntry(imageToMove.getId(), destinationAlbum.getId(), "/moto/places/acirc3", false, imageToMove.getTitle(), imageToMove.getDescription(), imageToMove.getImageUrl(), imageToMove.getThumbnailUrl(), imageToMove.getSort(), 0);
        allroutes = provider.updateEntry(movedImage);

        // Verify that sort values in the original album have been adjusted
        int sortValueOfNextImageInOriginalAlbumAfterMove = allroutes.stream().filter(r -> "/moto/vfr96/dirtroad".equals(r.getPath())).findFirst().get().getSort();
        assertThat(sortValueOfNextImageInOriginalAlbum).isGreaterThan(sortValueOfNextImageInOriginalAlbumAfterMove);

        // Verify that image is sorted last in the destination album
        AlbumEntry imageInDestination = allroutes.stream().filter(r -> r.getId() == imageToMove.getId()).findFirst().get();
        assertThat(imageInDestination.getSort()).isGreaterThan(sortValueOfLastItemInDestinationAlbum);
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
        AlbumEntry modifiedAlbum = new AlbumEntry(357, 1, "/moto/", true, "Album has been updated", "This is an updated description", null, null, 1, 2);
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
        AlbumEntry albumToAdd = new AlbumEntry(0, 1, "/newalbum/", true, "A new album", "A new album for new pictures", null, null, 2, 0);
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
        AlbumEntry albumToAdd = new AlbumEntry(0, 1, "/newalbum/", true, "A new album", "A new album for new pictures", null, null, 2, 0);
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
        int numberOfEntriesBeforeDelete = provider.fetchAllRoutes().size();
        AlbumEntry pictureToDelete = new AlbumEntry(7, 3, "/oldalbum/moto/places/grava3", false, "", "Tyrigrava, view from the north. Lotsa bikes here too", "https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg", "https://www.bang.priv.no/sb/pics/moto/places/icons/grava3.gif", 1, 0);
        List<AlbumEntry> allroutes = provider.deleteEntry(pictureToDelete);
        assertThat(allroutes.size()).isLessThan(numberOfEntriesBeforeDelete);
        Optional<AlbumEntry> deletedPicture = allroutes.stream().filter(r -> r.getId() == 7).findFirst();
        assertFalse(deletedPicture.isPresent());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testDeleteEntryWithDataSourceFailure() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        DataSource datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate();
        int numberOfEntriesBeforeDelete = provider.fetchAllRoutes().size();
        AlbumEntry pictureToDelete = new AlbumEntry(7, 3, "/oldalbum/moto/places/grava3", false, "", "Tyrigrava, view from the north. Lotsa bikes here too", "https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg", "https://www.bang.priv.no/sb/pics/moto/places/icons/grava3.gif", 1, 0);
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

        List<AlbumEntry> allroutes = provider.fetchAllRoutes();
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

    @SuppressWarnings("unchecked")
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
        List<AlbumEntry> allroutes = provider.moveEntryUp(new AlbumEntry(0, 1, null, false, null, null, null, null, 10, 10));
        assertEquals(0, allroutes.size());
        assertThat(logservice.getLogmessages().size()).isPositive();
        assertThat(logservice.getLogmessages().get(0)).contains("Failed to move album entry with id");
    }

    @Test
    void testMoveAlbumEntriesDown() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate();

        List<AlbumEntry> allroutes = provider.fetchAllRoutes();
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

    @SuppressWarnings("unchecked")
    @Test
    void testMoveAlbumEntriesDownWithDatabaseFailure() throws Exception {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);
        DataSource datasourceThrowsException = mock(DataSource.class);
        when(datasourceThrowsException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsException);
        provider.activate();

        List<AlbumEntry> allroutes = provider.moveEntryDown(new AlbumEntry(0, 1, null, false, null, null, null, null, 10, 10));
        assertEquals(0, allroutes.size());
        assertThat(logservice.getLogmessages().size()).isPositive();
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

        AlbumEntry updatedEntry = new AlbumEntry(7, 3, "/oldalbum/moto/places/grava3", false, "", "Tyrigrava, view from the north. Lotsa bikes here too", "https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg", "https://www.bang.priv.no/sb/pics/moto/places/icons/grava3.gif", 1, 0);
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

        int entryCount = provider.findPreviousEntryInTheSameAlbum(connection, new AlbumEntry(), 2);
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

        int entryCount = provider.findNextEntryInTheSameAlbum(connection, new AlbumEntry(), 2);
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
        assertThat(metadata.getLastModified()).isPositive();
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
        assertEquals(0L, metadata.getLastModified());
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
        assertThat(logservice.getLogmessages().size()).isPositive();
        assertThat(logservice.getLogmessages().get(0)).contains("Error when reading metadata");
    }

    @Test
    void testDumpImageMetadata() {
        OldAlbumServiceProvider provider = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        provider.setLogService(logservice);

        List<String> imageUrls = Arrays.asList(
            "https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg",
            "https://www.bang.priv.no/sb/pics/moto/places/grava2.jpg",
            "https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg",
            "https://www.bang.priv.no/sb/pics/moto/places/hove.jpg",
            "https://www.bang.priv.no/sb/pics/moto/vfr96/acirc1.jpg",
            "https://www.bang.priv.no/sb/pics/moto/vfr96/acirc2.jpg",
            "https://www.bang.priv.no/sb/pics/moto/vfr96/acirc3.jpg",
            "https://www.bang.priv.no/sb/pics/moto/vfr96/dirtroad.jpg",
            "https://www.bang.priv.no/sb/pics/moto/vfr96/enga1.jpg",
            "https://www.bang.priv.no/sb/pics/moto/vfr96/enga2.jpg",
            "https://www.bang.priv.no/sb/pics/moto/vfr96/fjell1.jpg",
            "https://www.bang.priv.no/sb/pics/moto/vfr96/fjell2.jpg",
            "https://www.bang.priv.no/sb/pics/moto/vfr96/hovevfr.jpg",
            "https://www.bang.priv.no/sb/pics/moto/vfr96/rv306.jpg",
            "https://www.bang.priv.no/sb/pics/moto/vfr96/vfr1.jpg",
            "https://www.bang.priv.no/sb/pics/moto/vfr96/vfr2.jpg",
            "https://www.bang.priv.no/sb/pics/moto/vfr96/wintervfr-ef.jpg"
                                               );
        for (String imageUrl : imageUrls) {
            ImageMetadata metadata = provider.readMetadata(imageUrl);
            System.out.println(imageUrl);
            System.out.println(metadata.getLastModified());
            System.out.println(metadata.getContentLength());
        }
    }

}
