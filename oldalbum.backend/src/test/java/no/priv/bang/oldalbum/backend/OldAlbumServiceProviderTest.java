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
package no.priv.bang.oldalbum.backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.metadata.IIOMetadataNode;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ops4j.pax.jdbc.derby.impl.DerbyDataSourceFactory;
import org.osgi.service.jdbc.DataSourceFactory;

import com.mockrunner.mock.jdbc.MockConnection;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.jpeg.JPEGSegment;
import liquibase.Scope;
import liquibase.Scope.ScopedRunner;
import liquibase.changelog.ChangeLogParameters;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DatabaseChangelogCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.sdk.resource.MockResourceAccessor;
import no.priv.bang.oldalbum.backend.imageio.ImageioSpiRegistration;
import no.priv.bang.oldalbum.db.liquibase.OldAlbumLiquibase;
import no.priv.bang.oldalbum.db.liquibase.test.OldAlbumDerbyTestDatabase;
import no.priv.bang.oldalbum.services.OldAlbumException;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.oldalbum.services.bean.BatchAddPicturesRequest;
import no.priv.bang.oldalbum.services.bean.ImageMetadata;
import no.priv.bang.oldalbum.services.bean.LocaleBean;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class OldAlbumServiceProviderTest {
    private final static Locale NB_NO = Locale.forLanguageTag("nb-no");

    private static DataSource datasource;

    @BeforeAll
    static void setupDataSource() throws Exception {
        datasource = createNewTestDatabase("oldalbum");
    }

    @Test
    void testFetchAllRoutes() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        var database = createNewTestDatabase("oldalbum2");
        provider.setLogService(logservice);
        provider.setDataSource(database);
        provider.activate(Collections.emptyMap());

        // First check all routes not requiring login
        var allroutesNotRequiringLogin = provider.fetchAllRoutes(null, false);
        assertThat(allroutesNotRequiringLogin).hasSize(22);

        // Then check that all routes including those that require login has at least 3 more entries
        var allroutesIncludingThoseRequiringLogin = provider.fetchAllRoutes(null, true);
        assertThat(allroutesIncludingThoseRequiringLogin).hasSize(allroutesNotRequiringLogin.size() + 2);
    }

    @Test
    void testFetchAllRoutesWithDatabaseFailure() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate(Collections.emptyMap());
        var allroutes = provider.fetchAllRoutes(null, false);
        assertEquals(1, logservice.getLogmessages().size());
        assertEquals(0, allroutes.size());
    }

    @Test
    void testFindShiroProtectedUrls() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        var database = createNewTestDatabase("oldalbum3");
        provider.setLogService(logservice);
        provider.setDataSource(database);
        provider.activate(Collections.emptyMap());

        // Call method under test
        var urls = provider.findShiroProtectedUrls();

        // Verify that the results comes with unprotected album entries before protected albums
        // In the Ini files what comes first "wins", so the unprotected pictures can be accessed
        // even though their parent albums are blocked
        var expectedUrls = new LinkedHashMap<String, String>();
        expectedUrls.put("/slides/berglia", "anon");
        expectedUrls.put("/slides/", "authc");
        var iterator = expectedUrls.entrySet().iterator();
        var bergliaPicture = iterator.next();
        var slidesAlbum = iterator.next();
        assertThat(urls.entrySet()).containsExactly(bergliaPicture, slidesAlbum);
    }

    @Test
    void testFindShiroProtectedUrlsWithDatabaseFailure() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate(Collections.emptyMap());

        // Precondition: log initially empty
        assertThat(logservice.getLogmessages()).isEmpty();

        // Call method under test
        var urls = provider.findShiroProtectedUrls();

        // Expect empty result and logged error
        // In case of an empty result the initial, static, shiro.ini will determine what's protected
        assertThat(urls.entrySet()).isEmpty();
        assertThat(logservice.getLogmessages()).hasSize(1);
        assertThat(logservice.getLogmessages().get(0)).contains("Failed to find the list of shiro protected urls");
    }

    @Test
    void testGetPaths() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());

        // Test paths when logged in
        var pathsWhenNotLoggedIn = provider.getPaths(false);
        assertThat(pathsWhenNotLoggedIn).hasSizeGreaterThanOrEqualTo(19);

        // Test paths when not logged in
        var pathsWhenLoggedIn = provider.getPaths(true);
        assertThat(pathsWhenLoggedIn).hasSize(pathsWhenNotLoggedIn.size() + 3);
    }

    @Test
    void testGetPathsWithDatabaseFailure() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate(Collections.emptyMap());
        var paths = provider.getPaths(false);
        assertEquals(1, logservice.getLogmessages().size());
        assertEquals(0, paths.size());
    }

    @Test
    void testGetAlbumEntryFromPath() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());
        var entry = provider.getAlbumEntryFromPath("/moto/places/");
        assertEquals(3, entry.getId());
    }

    @Test
    void testGetAlbumEntryFromPathWithPathNotMatching() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());
        var entry = provider.getAlbumEntryFromPath("/path/not/matching/");
        assertNull(entry);
        assertEquals(1, logservice.getLogmessages().size());
        assertThat(logservice.getLogmessages().get(0)).contains("Found no albumentry matching path");
    }

    @Test
    void testGetAlbumEntryFromPathWithDatabaseFailure() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate(Collections.emptyMap());
        var entry = provider.getAlbumEntryFromPath("/moto/places/");
        assertNull(entry);
        assertEquals(1, logservice.getLogmessages().size());
        assertThat(logservice.getLogmessages().get(0)).contains("Failed to find albumentry with path");
    }

    @Test
    void testFetchChildren() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());
        var children = provider.getChildren(3);
        assertThat(children).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void testFetchChildrenWithDatabaseFailure() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate(Collections.emptyMap());
        var children = provider.getChildren(3);
        assertEquals(0, children.size());
        assertEquals(1, logservice.getLogmessages().size());
    }

    @Test
    void testFindSelectedentriesWhenDatabaseConnectionFails( ) throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate(Collections.emptyMap());

        assertThat(logservice.getLogmessages()).isEmpty(); // Verify empty before calling method under test
        provider.findSelectedentries(Collections.emptyList());
        assertThat(logservice.getLogmessages()).isNotEmpty(); // Verify that an error has been logged
    }

    @Test
    void testUpdateEntry() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());
        var modifiedAlbum = AlbumEntry.with()
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
        var allroutes = provider.updateEntry(modifiedAlbum);
        var updatedAlbum = allroutes.stream().filter(r -> r.getId() == 2).findFirst().get();
        assertEquals(modifiedAlbum.getTitle(), updatedAlbum.getTitle());
        assertEquals(modifiedAlbum.getDescription(), updatedAlbum.getDescription());
        assertEquals(modifiedAlbum.isRequireLogin(), updatedAlbum.isRequireLogin());
    }

    @Test
    void testUpdatePicture() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());

        var originalPicture = provider.fetchAllRoutes(null, false).stream().filter(r -> r.getId() == 5).findFirst().get();
        var modifiedTitle = "New picture title";
        var modifiedDescription = "This is an updated description";
        var modifiedDate = Date.from(LocalDateTime.now().minusDays(5).toInstant(ZoneOffset.UTC));
        var requireLogin = true;
        var modifiedPicture = AlbumEntry.with(originalPicture)
            .title(modifiedTitle)
            .description(modifiedDescription)
            .lastModified(modifiedDate)
            .requireLogin(requireLogin)
            .build();
        var allroutes = provider.updateEntry(modifiedPicture);
        var updatedPicture = allroutes.stream().filter(r -> r.getId() == 5).findFirst().get();
        assertEquals(modifiedTitle, updatedPicture.getTitle());
        assertEquals(modifiedDescription, updatedPicture.getDescription());
        assertEquals(modifiedDate, updatedPicture.getLastModified());
        assertEquals(requireLogin, updatedPicture.isRequireLogin());
    }

    @Test
    void testToggleEntryPasswordProtection() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        var database = createNewTestDatabase("oldalbum4");
        provider.setLogService(logservice);
        provider.setDataSource(database);
        provider.activate(Collections.emptyMap());

        var allroutes = provider.fetchAllRoutes(null, true);
        var protectedAlbums = allroutes.stream().filter(r -> r.isAlbum()).filter(r -> r.isRequireLogin()).collect(Collectors.toList());
        var protectedPictures = allroutes.stream().filter(r -> !r.isAlbum()).filter(r -> r.isRequireLogin()).collect(Collectors.toList());

        var protectedAlbum = protectedAlbums.get(0);
        assertTrue(protectedAlbum.isRequireLogin());
        var updatedAllroutesAfterUnprotectingAlbum = provider.toggleEntryPasswordProtection(protectedAlbum.getId());
        var unprotectedAlbum = updatedAllroutesAfterUnprotectingAlbum.stream().filter(r -> r.getId() == protectedAlbum.getId()).findFirst().get();
        assertThat(unprotectedAlbum.isRequireLogin()).isNotEqualTo(protectedAlbum.isRequireLogin());

        var protectedPicture = protectedPictures.get(0);
        assertTrue(protectedPicture.isRequireLogin());
        var updatedAllroutesAfterUnprotectingPicture = provider.toggleEntryPasswordProtection(protectedPicture.getId());
        var unprotectedPicture = updatedAllroutesAfterUnprotectingPicture.stream().filter(r -> r.getId() == protectedAlbum.getId()).findFirst().get();
        assertThat(unprotectedPicture.isRequireLogin()).isNotEqualTo(protectedPicture.isRequireLogin());
    }

    @Test
    void testToggleEntryPasswordProtectionWithDatabaseFailure() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        var database = mock(DataSource.class);
        when(database.getConnection()).thenThrow(SQLException.class);
        provider.setLogService(logservice);
        provider.setDataSource(database);
        provider.activate(Collections.emptyMap());

        assertThat(logservice.getLogmessages()).isEmpty(); // Verify log initially empty
        var allroutes = provider.toggleEntryPasswordProtection(1);
        assertThat(allroutes).isEmpty(); // No real database so empty is expected
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    @Test()
    void testSwitchEntryParent() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());

        // Verify that sort values are updated correctly when an album
        // entry is moved to a different album
        var allroutes = provider.fetchAllRoutes(null, false);
        var destinationAlbum = allroutes.stream().filter(r -> "/moto/places/".equals(r.getPath())).findFirst().get();
        var imageToMove = allroutes.stream().filter(r -> "/moto/vfr96/acirc3".equals(r.getPath())).findFirst().get();
        var sortValueOfNextImageInOriginalAlbum = allroutes.stream().filter(r -> "/moto/vfr96/dirtroad".equals(r.getPath())).findFirst().get().getSort();
        var sortValueOfLastItemInDestinationAlbum = allroutes.stream().filter(r -> "/moto/places/hove".equals(r.getPath())).findFirst().get().getSort();

        var movedImage = AlbumEntry.with(imageToMove)
            .parent(destinationAlbum.getId())
            .path("/moto/places/acirc3")
            .build();
        allroutes = provider.updateEntry(movedImage);

        // Verify that sort values in the original album have been adjusted
        var sortValueOfNextImageInOriginalAlbumAfterMove = allroutes.stream().filter(r -> "/moto/vfr96/dirtroad".equals(r.getPath())).findFirst().get().getSort();        assertThat(sortValueOfNextImageInOriginalAlbum).isGreaterThan(sortValueOfNextImageInOriginalAlbumAfterMove);

        // Verify that image is sorted last in the destination album
        var imageInDestination = allroutes.stream().filter(r -> r.getId() == imageToMove.getId()).findFirst().get();
        assertThat(imageInDestination.getSort()).isGreaterThan(sortValueOfLastItemInDestinationAlbum);
    }

    @Test
    void testUpdateEntryWithDataSourceFailure() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate(Collections.emptyMap());
        var modifiedAlbum = AlbumEntry.with()
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
        var allroutes = provider.updateEntry(modifiedAlbum);
        assertEquals(0, allroutes.size());
        assertEquals(2, logservice.getLogmessages().size());
        assertThat(logservice.getLogmessages().get(0)).contains("Failed to update album entry for id");
    }

    @Test
    void testAddEntryWithAlbum() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());
        var numberOfEntriesBeforeAdd = provider.fetchAllRoutes(null, false).size();
        var albumToAdd = AlbumEntry.with()
            .parent(1)
            .path("/newalbum/")
            .album(true)
            .title("A new album")
            .description("A new album for new pictures")
            .sort(2)
            .requireLogin(true)
            .groupByYear(true)
            .build();
        var allroutes = provider.addEntry(albumToAdd);
        assertThat(allroutes).hasSizeGreaterThan(numberOfEntriesBeforeAdd);
        var addedAlbum = allroutes.stream().filter(r -> "/newalbum/".equals(r.getPath())).findFirst().get();
        assertNotEquals(albumToAdd.getId(), addedAlbum.getId()); // Placeholder ID is replaced with an actual database id
        assertThat(addedAlbum.getId()).isPositive();
        assertEquals(1, addedAlbum.getParent());
        assertEquals(albumToAdd.getTitle(), addedAlbum.getTitle());
        assertEquals(albumToAdd.getDescription(), addedAlbum.getDescription());
        assertEquals(albumToAdd.isRequireLogin(), addedAlbum.isRequireLogin());
        assertEquals(albumToAdd.getGroupByYear(), addedAlbum.getGroupByYear());
    }

    @Test
    void testAddEntryWithPicture() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());
        var numberOfEntriesBeforeAdd = provider.fetchAllRoutes(null, false).size();
        var imageUrl = "https://www.bang.priv.no/sb/pics/misc/sylane4.jpg";
        var metadata = provider.readMetadata(imageUrl);
        var pictureToAdd = AlbumEntry.with()
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
        var allroutes = provider.addEntry(pictureToAdd);
        assertThat(allroutes).hasSizeGreaterThan(numberOfEntriesBeforeAdd);
        var addedPicture = allroutes.stream().filter(r -> "/sylane4".equals(r.getPath())).findFirst().get();
        assertNotEquals(pictureToAdd.getId(), addedPicture.getId()); // Placeholder ID is replaced with an actual database id
        assertEquals(1, addedPicture.getParent());
        assertEquals(pictureToAdd.getTitle(), addedPicture.getTitle());
        assertEquals(pictureToAdd.getDescription(), addedPicture.getDescription());
        assertEquals(pictureToAdd.getLastModified(), addedPicture.getLastModified());
        assertEquals(pictureToAdd.getContentType(), addedPicture.getContentType());
        assertEquals(pictureToAdd.getContentLength(), addedPicture.getContentLength());
        assertEquals(pictureToAdd.isRequireLogin(), pictureToAdd.isRequireLogin());
        assertNull(addedPicture.getGroupByYear());
    }

    @Test
    void testAddEntryWithPictureWhenPictureIs404() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());
        var numberOfEntriesBeforeAdd = provider.fetchAllRoutes(null, false).size();
        var pictureToAdd = AlbumEntry.with()
            .parent(1)
            .path("/sylane5")
            .album(false)
            .title("Sylane påsken 1996")
            .description("Ut på ski, alltid blid")
            .imageUrl("https://www.bang.priv.no/sb/pics/misc/sylane5.jpg")
            .thumbnailUrl("https://www.bang.priv.no/sb/pics/misc/.icons/sylane5.gif")
            .sort(4)
            .build();
        var allroutes = provider.addEntry(pictureToAdd);
        assertThat(allroutes).hasSizeGreaterThan(numberOfEntriesBeforeAdd);
        var addedPicture = allroutes.stream().filter(r -> "/sylane5".equals(r.getPath())).findFirst().get();
        assertNotEquals(pictureToAdd.getId(), addedPicture.getId()); // Placeholder ID is replaced with an actual database id
        assertEquals(1, addedPicture.getParent());
        assertEquals(pictureToAdd.getTitle(), addedPicture.getTitle());
        assertEquals(pictureToAdd.getDescription(), addedPicture.getDescription());
        assertNull(addedPicture.getLastModified());
    }

    @Test
    void testAddEntryWithPictureWhenPictureIs500() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());

        // Mocked HTTP request
        var connectionFactory = mock(HttpConnectionFactory.class);
        var connection = mock(HttpURLConnection.class);
        when(connection.getResponseCode()).thenReturn(500);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        provider.setConnectionFactory(connectionFactory);

        var numberOfEntriesBeforeAdd = provider.fetchAllRoutes(null, false).size();
        var pictureToAdd = AlbumEntry.with()
            .parent(1)
            .path("/sylane5")
            .album(false)
            .title("Sylane påsken 1996")
            .description("Ut på ski, alltid blid")
            .imageUrl("https://www.bang.priv.no/sb/pics/misc/sylane5.jpg")
            .thumbnailUrl("https://www.bang.priv.no/sb/pics/misc/.icons/sylane5.gif")
            .sort(4)
            .build();
        var allroutes = provider.addEntry(pictureToAdd);
        assertThat(allroutes).hasSizeGreaterThan(numberOfEntriesBeforeAdd);
        var addedPicture = allroutes.stream().filter(r -> "/sylane5".equals(r.getPath())).findFirst().get();
        assertNotEquals(pictureToAdd.getId(), addedPicture.getId()); // Placeholder ID is replaced with an actual database id
        assertEquals(1, addedPicture.getParent());
        assertEquals(pictureToAdd.getTitle(), addedPicture.getTitle());
        assertEquals(pictureToAdd.getDescription(), addedPicture.getDescription());
        assertNull(addedPicture.getLastModified());
    }

    @Test
    void testAddEntryWithPictureWhenPictureIsFoundButNotAnyMetadata() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());
        var numberOfEntriesBeforeAdd = provider.fetchAllRoutes(null, false).size();
        var pictureToAdd = AlbumEntry.with()
            .parent(1)
            .path("/sylane5")
            .album(false)
            .title("Sylane påsken 1996")
            .description("Ut på ski, alltid blid")
            .imageUrl("https://www.bang.priv.no/sb/pics/misc/sylane5.jpg")
            .thumbnailUrl("https://www.bang.priv.no/sb/pics/misc/.icons/sylane5.gif")
            .sort(4)
            .build();
        var allroutes = provider.addEntry(pictureToAdd);
        assertThat(allroutes).hasSizeGreaterThan(numberOfEntriesBeforeAdd);
        var addedPicture = allroutes.stream().filter(r -> "/sylane5".equals(r.getPath())).findFirst().get();
        assertNotEquals(pictureToAdd.getId(), addedPicture.getId()); // Placeholder ID is replaced with an actual database id
        assertEquals(1, addedPicture.getParent());
        assertEquals(pictureToAdd.getTitle(), addedPicture.getTitle());
        assertEquals(pictureToAdd.getDescription(), addedPicture.getDescription());
        assertNull(addedPicture.getLastModified());
    }

    @Test
    void testAddEntryWithDataSourceFailure() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate(Collections.emptyMap());
        var albumToAdd = AlbumEntry.with()
            .parent(1)
            .path("/newalbum/")
            .album(true)
            .title("A new album")
            .description("A new album for new pictures")
            .sort(2)
            .build();
        var allroutes = provider.addEntry(albumToAdd);
        assertEquals(0, allroutes.size());
        assertEquals(2, logservice.getLogmessages().size());
        assertThat(logservice.getLogmessages().get(0)).contains("Failed to add album entry with path");
    }

    @Test
    void testDeleteEntry() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());
        var numberOfEntriesBeforeDelete = provider.fetchAllRoutes(null, true).size();
        var pictureToDelete = AlbumEntry.with()
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
        var allroutes = provider.deleteEntry(pictureToDelete);
        assertThat(allroutes).hasSizeLessThan(numberOfEntriesBeforeDelete);
        var deletedPicture = allroutes.stream().filter(r -> r.getId() == 7).findFirst();
        assertFalse(deletedPicture.isPresent());
    }

    @Test
    void testDeleteEntryWithDataSourceFailure() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var datasourceThrowsSqlException = mock(DataSource.class);
        when(datasourceThrowsSqlException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsSqlException);
        provider.activate(Collections.emptyMap());
        var numberOfEntriesBeforeDelete = provider.fetchAllRoutes(null, false).size();
        var pictureToDelete = AlbumEntry.with()
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
        var allroutes = provider.deleteEntry(pictureToDelete);
        assertEquals(numberOfEntriesBeforeDelete, allroutes.size());
        assertEquals(3, logservice.getLogmessages().size());
        assertThat(logservice.getLogmessages().get(1)).contains("Failed to delete album entry with id");
    }

    @Test
    void testDeleteSelectedEntries() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var database = createNewTestDatabase("oldalbum1");
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(database);
        provider.activate(Collections.emptyMap());
        var numberOfEntriesBeforeDelete = provider.fetchAllRoutes(null, true).size();
        var selection = Arrays.asList(7);
        var allroutes = provider.deleteSelectedEntries(selection);
        assertThat(allroutes).hasSizeLessThan(numberOfEntriesBeforeDelete);
        var deletedPicture = allroutes.stream().filter(r -> r.getId() == 7).findFirst();
        assertFalse(deletedPicture.isPresent());
    }

    @Test
    void testMoveAlbumEntriesUp() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());

        var allroutes = provider.fetchAllRoutes(null, false);
        // Find the first and second entries of the "vfr" album
        var originalFirstEntry = allroutes.stream().filter(r -> "/moto/vfr96/acirc1".equals(r.getPath())).findFirst().get();
        assertEquals(1, originalFirstEntry.getSort());
        var originalFirstEntryLastModifiedDate = originalFirstEntry.getLastModified();
        var secondEntry = allroutes.stream().filter(r -> "/moto/vfr96/acirc2".equals(r.getPath())).findFirst().get();
        assertEquals(2, secondEntry.getSort());
        var originalSecondEntryLastModifiedDate = secondEntry.getLastModified();

        // Move from second to first
        allroutes = provider.moveEntryUp(secondEntry);
        secondEntry = allroutes.stream().filter(r -> "/moto/vfr96/acirc2".equals(r.getPath())).findFirst().get();
        assertEquals(1, secondEntry.getSort());
        assertEquals(originalFirstEntryLastModifiedDate, secondEntry.getLastModified(), "Expected lastModifiedTime to be swapped by move");
        originalFirstEntry = allroutes.stream().filter(r -> "/moto/vfr96/acirc1".equals(r.getPath())).findFirst().get();
        assertEquals(2, originalFirstEntry.getSort());
        assertEquals(originalSecondEntryLastModifiedDate, originalFirstEntry.getLastModified(), "Expected lastModifiedTime to be swapped by move");

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
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var datasourceThrowsException = mock(DataSource.class);
        when(datasourceThrowsException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsException);
        provider.activate(Collections.emptyMap());

        // Try moving an album and failing
        var allroutes = provider.moveEntryUp(AlbumEntry.with().id(0).parent(1).sort(10).childcount(10).build());
        assertEquals(0, allroutes.size());
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).contains("Failed to move album entry with id");
    }

    @Test
    void testMoveAlbumEntriesDown() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());

        var allroutes = provider.fetchAllRoutes(null, false);
        // Find the last and second to last entries of the "vfr" album
        var numberOfAlbumentriesInAlbum = allroutes.stream().filter(r -> "/moto/vfr96/".equals(r.getPath())).findFirst().get().getChildcount();
        var originalLastEntry = allroutes.stream().filter(r -> "/moto/vfr96/wintervfr-ef".equals(r.getPath())).findFirst().get();
        assertEquals(numberOfAlbumentriesInAlbum, originalLastEntry.getSort());
        var originalLastEntryLastModifiedDate = originalLastEntry.getLastModified();
        var secondToLastEntry = allroutes.stream().filter(r -> "/moto/vfr96/vfr2".equals(r.getPath())).findFirst().get();
        assertEquals(numberOfAlbumentriesInAlbum - 1, secondToLastEntry.getSort());
        var secondToLastEntryLastModifiedDate = secondToLastEntry.getLastModified();

        // Move from second to last position to last position
        allroutes = provider.moveEntryDown(secondToLastEntry);
        secondToLastEntry = allroutes.stream().filter(r -> "/moto/vfr96/vfr2".equals(r.getPath())).findFirst().get();
        assertEquals(numberOfAlbumentriesInAlbum, secondToLastEntry.getSort());
        assertEquals(originalLastEntryLastModifiedDate, secondToLastEntry.getLastModified(), "Expected lastModifiedTime to be swapped by move");
        originalLastEntry = allroutes.stream().filter(r -> "/moto/vfr96/wintervfr-ef".equals(r.getPath())).findFirst().get();
        assertEquals(numberOfAlbumentriesInAlbum - 1, originalLastEntry.getSort());
        assertEquals(secondToLastEntryLastModifiedDate, originalLastEntry.getLastModified(), "Expected lastModifiedTime to be swapped by move");

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
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var datasourceThrowsException = mock(DataSource.class);
        when(datasourceThrowsException.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(datasourceThrowsException);
        provider.activate(Collections.emptyMap());

        var allroutes = provider.moveEntryDown(AlbumEntry.with().id(0).parent(1).sort(10).childcount(10).build());
        assertEquals(0, allroutes.size());
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).contains("Failed to move album entry with id");
    }

    @Test
    void testThatDatesAreSwappedWhenMovinAlbumEntriesUpAndDownButNotWhenSwappingWithAlbums() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());
        var allroutes = provider.addEntry(AlbumEntry.with().parent(1).path("/albumtomoveentriesin/").album(true).build());
        var albumToMoveEntriesIn = allroutes.stream().filter(r -> r.getPath().equals("/albumtomoveentriesin/")).findFirst().get();
        var albumid = albumToMoveEntriesIn.getId();
        provider.addEntry(AlbumEntry.with().parent(albumid).path("/albumtomoveentriesin/b").album(false).sort(1).lastModified(parseDate("1971-02-25T13:13:22Z")).build());
        provider.addEntry(AlbumEntry.with().parent(albumid).path("/albumtomoveentriesin/a").album(false).sort(2).lastModified(parseDate("1967-04-10T11:27:31Z")).build());
        provider.addEntry(AlbumEntry.with().parent(albumid).path("/albumtomoveentriesin/e").album(true).sort(3).build());
        provider.addEntry(AlbumEntry.with().parent(albumid).path("/albumtomoveentriesin/d").album(false).sort(4).lastModified(parseDate("2022-12-24T17:10:11Z")).build());
        allroutes = provider.addEntry(AlbumEntry.with().parent(albumid).path("/albumtomoveentriesin/c").album(false).sort(5).lastModified(parseDate("2014-10-12T10:39:40Z")).build());

        // Verify that moving up over an image swaps the lastModifiedTime timestamp
        var c = allroutes.stream().filter(r -> r.getPath().equals("/albumtomoveentriesin/c")).findFirst().get();
        var originalCLastModifiedDate = c.getLastModified();
        var d = allroutes.stream().filter(r -> r.getPath().equals("/albumtomoveentriesin/d")).findFirst().get();
        var originalDLastModfiedDate = d.getLastModified();
        allroutes = provider.moveEntryUp(c);
        var cAfterMoveUp = allroutes.stream().filter(r -> r.getPath().equals("/albumtomoveentriesin/c")).findFirst().get();
        assertEquals(originalDLastModfiedDate, cAfterMoveUp.getLastModified(), "Expected lastModifiedDate to be swapped when moving up over an image");
        var dAfterMoveUp = allroutes.stream().filter(r -> r.getPath().equals("/albumtomoveentriesin/d")).findFirst().get();
        assertEquals(originalCLastModifiedDate, dAfterMoveUp.getLastModified(), "Expected lastModifiedDate to be swapped when moving up over an image");

        // Verify that moving up over an album keeps the lastModifiedTime timestamp
        c = allroutes.stream().filter(r -> r.getPath().equals("/albumtomoveentriesin/c")).findFirst().get();
        originalCLastModifiedDate = c.getLastModified();
        allroutes = provider.moveEntryUp(c);
        cAfterMoveUp = albumToMoveEntriesIn = allroutes.stream().filter(r -> r.getPath().equals("/albumtomoveentriesin/c")).findFirst().get();
        assertEquals(originalCLastModifiedDate, cAfterMoveUp.getLastModified(), "Expected lastModifiedDate not to be swapped when moving up over an album");

        // Verify that moving down over an image swaps the lastModifiedTime timestamp
        var a = allroutes.stream().filter(r -> r.getPath().equals("/albumtomoveentriesin/a")).findFirst().get();
        var originalALastModfiedDate = a.getLastModified();
        c = allroutes.stream().filter(r -> r.getPath().equals("/albumtomoveentriesin/c")).findFirst().get();
        originalCLastModifiedDate = c.getLastModified();
        allroutes = provider.moveEntryDown(a);
        var aAfterMoveDown = allroutes.stream().filter(r -> r.getPath().equals("/albumtomoveentriesin/a")).findFirst().get();
        assertEquals(originalCLastModifiedDate, aAfterMoveDown.getLastModified(), "Expected lastModifiedDate to be swapped when moving down over an image");
        var cAfterMoveDown = allroutes.stream().filter(r -> r.getPath().equals("/albumtomoveentriesin/c")).findFirst().get();
        assertEquals(originalALastModfiedDate, cAfterMoveDown.getLastModified(), "Expected lastModifiedDate to be swapped when moving down over an image");

        // Verify that moving down over an album keeps the lastModifiedTime timestamp
        a = allroutes.stream().filter(r -> r.getPath().equals("/albumtomoveentriesin/a")).findFirst().get();
        originalALastModfiedDate = a.getLastModified();
        allroutes = provider.moveEntryDown(a);
        aAfterMoveDown = albumToMoveEntriesIn = allroutes.stream().filter(r -> r.getPath().equals("/albumtomoveentriesin/a")).findFirst().get();
        assertEquals(originalALastModfiedDate, aAfterMoveDown.getLastModified(), "Expected lastModifiedDate not to be swapped when moving down over an album");
    }

    @Test
    void testAdjustSortValuesWhenMovingToDifferentAlbumNoExistingParent() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        var results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        var updatedEntry = AlbumEntry.with()
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
        var sort = provider.adjustSortValuesWhenMovingToDifferentAlbum(connection, updatedEntry);
        assertEquals(updatedEntry.getSort(), sort);
    }

    @Test
    void testAdjustSortValuesAfterEntryIsRemoved() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);

        var e = assertThrows(OldAlbumException.class, () -> provider.adjustSortValuesAfterEntryIsRemoved(connection, 0, 0));
        assertThat(e.getMessage()).startsWith("Failed to adjust sort values after removing album item in album with id=");
    }

    @Test
    void testSwapSortValuesFailOnFirst() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        var results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);

        var e = assertThrows(OldAlbumException.class, () -> provider.swapSortValues(connection, 0, 0, 0, 0));
        assertThat(e.getMessage()).startsWith("Failed to update sort value of moved entry");
    }

    @Test
    void testSwapSortValuesFailOnSecond() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        var results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1).thenThrow(SQLException.class);

        var e = assertThrows(OldAlbumException.class, () -> provider.swapSortValues(connection, 0, 0, 0, 0));
        assertThat(e.getMessage()).startsWith("Failed to update sort value of neighbouring entry");
    }

    @Test
    void testSwapSortAndLastModifiedValuesFailOnFirst() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        var results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenThrow(SQLException.class);
        var date = new Date();

        var e = assertThrows(OldAlbumException.class, () -> provider.swapSortAndLastModifiedValues(connection, 0, 0, date, 0, 0, null));
        assertThat(e.getMessage()).startsWith("Failed to update sort value of moved entry");
    }

    @Test
    void testSwapSortAndLastModifiedValuesFailOnSecond() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        var results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1).thenThrow(SQLException.class);
        var date1 = new Date();
        var date2 = new Date();

        var e = assertThrows(OldAlbumException.class, () -> provider.swapSortAndLastModifiedValues(connection, 0, 0, date1, 0, 0, date2));
        assertThat(e.getMessage()).startsWith("Failed to update sort value of neighbouring entry");
    }

    @Test
    void testAtLeastOneEntryIsAlbum() {
        var provider = new OldAlbumServiceProvider();
        var picture1 = AlbumEntry.with().album(false).build();
        var picture2 = AlbumEntry.with().album(false).build();
        assertFalse(provider.atLeastOneEntryIsAlbum(picture1, picture2));
        var album1 = AlbumEntry.with().album(true).build();
        assertTrue(provider.atLeastOneEntryIsAlbum(picture1, album1));
        assertTrue(provider.atLeastOneEntryIsAlbum(album1, picture1));
        var album2 = AlbumEntry.with().album(true).build();
        assertTrue(provider.atLeastOneEntryIsAlbum(album1, album2));
    }

    @Test
    void testFindNumberOfEntriesInAlbumWithSqlExceptionThrown() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);

        var e = assertThrows(OldAlbumException.class, () -> provider.findNumberOfEntriesInAlbum(connection, 0));
        assertThat(e.getMessage()).startsWith("Failed to find number of entries in album with id=");
    }

    @Test
    void testFindNumberOfEntriesInAlbumEmptyResultSet() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var connection = new MockConnection();

        int entryCount = provider.findNumberOfEntriesInAlbum(connection, 0);
        assertEquals(0, entryCount);
    }

    @Test
    void testFindPreviousEntryInTheSameAlbumEmptyResultSet() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        var results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        var previousEntry = provider.findPreviousEntryInTheSameAlbum(connection, AlbumEntry.with().build(), 2);
        assertThat(previousEntry).isEmpty();
    }

    @Test
    void testFindNextEntryInTheSameAlbumResultSet() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var connection = mock(Connection.class);
        var statement = mock(PreparedStatement.class);
        var results = mock(ResultSet.class);
        when(statement.executeQuery()).thenReturn(results);
        when(connection.prepareStatement(anyString())).thenReturn(statement);

        var nextEntry = provider.findNextEntryInTheSameAlbum(connection, AlbumEntry.with().build(), 2);
        assertThat(nextEntry).isEmpty();
    }

    @Test
    void testGetEntryWhenEntryNotFound() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var connection = new MockConnection();

        var entry = provider.getEntry(connection, 0);
        assertThat(entry).isEmpty();
    }

    @Test
    void testGetEntryWithSqlExceptionThrown() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenThrow(SQLException.class);

        var e = assertThrows(OldAlbumException.class, () -> provider.getEntry(connection, 0));
        assertThat(e.getMessage()).startsWith("Unable to load album entry matching id=");
    }

    @Test
    void testDownloadAlbumEntryWithEntryNotFoundInDatabase() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());

        var e = assertThrows(OldAlbumException.class, () -> provider.downloadAlbumEntry(999));
        assertThat(e.getMessage()).startsWith("Unable to find album entry matching id=");;
    }

    @Test
    void testDownloadAlbumEntryOnExistingImage() throws Exception {
        var replacementTitle = "Replacement title";
        var replacementDescription = "Replacement description";
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        var imageIOService = new ImageioSpiRegistration();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.setImageIOService(imageIOService);
        provider.activate(Collections.emptyMap());
        var dummyAlbum = provider.addEntry(AlbumEntry.with().parent(1).album(true).path("dummy").title("Dummy album").description("Dummy description").build()).stream().filter(e -> "dummy".equals(e.getPath())).findFirst().get();
        var modifiedEntry = AlbumEntry.with(provider.getAlbumEntry(9).get()).parent(dummyAlbum.getId()).title(replacementTitle).description(replacementDescription).build();
        var entry = provider.addEntry(modifiedEntry).stream().filter(e -> replacementDescription.equals(e.getDescription())).findFirst().get();

        var streamingOutput = provider.downloadAlbumEntry(entry.getId());
        assertNotNull(streamingOutput);
        var downloadFile = Files.createTempFile("image", "jpg").toFile();
        try(var outputStream = new FileOutputStream(downloadFile)) {
            streamingOutput.write(outputStream);
        }

        var dummyConnection = mock(HttpURLConnection.class);
        var metadata = provider.readMetadataOfLocalFile(downloadFile, dummyConnection);
        assertThat(metadata.getTitle()).startsWith(replacementTitle);
        assertThat(metadata.getDescription()).startsWith(replacementDescription);
        assertEquals(entry.getLastModified(), metadata.getLastModified());
    }

    @Test
    void testDownloadAlbumEntryOnExistingAlbum() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        var imageIOService = new ImageioSpiRegistration();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.setImageIOService(imageIOService);
        provider.activate(Collections.emptyMap());
        var albumentry = provider.getAlbumEntry(4).get();
        var albumpictures = provider.getChildren(albumentry.getId());

        var streamingOutput = provider.downloadAlbumEntry(albumentry.getId());
        assertNotNull(streamingOutput);

        // Stream the album into a zip file
        var downloadAlbum = Files.createTempFile("album", "zip");
        try(var outputStream = new FileOutputStream(downloadAlbum.toFile())) {
            streamingOutput.write(outputStream);
        }

        // Check that zip members last modified time have been set to albumEntry values
        var picturefilename = provider.findFileNamePartOfUrl(albumpictures.get(0).getImageUrl());
        var zipentry = findZipEntryFor(downloadAlbum.toFile(), picturefilename);
        assertEquals(albumpictures.get(0).getLastModified(), new Date(zipentry.getLastModifiedTime().toInstant().toEpochMilli()));
    }

    @Test
    void testDownloadAlbumEntrySelection() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        var imageIOService = new ImageioSpiRegistration();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.setImageIOService(imageIOService);
        provider.activate(Collections.emptyMap());
        var albumentry = provider.getAlbumEntry(4).get();
        var albumpictures = provider.getChildren(albumentry.getId());
        var selectedentryIds = albumpictures.stream().map(e -> e.getId()).toList();

        var streamingOutput = provider.downloadAlbumEntrySelection(selectedentryIds);
        assertNotNull(streamingOutput);

        // Stream the album into a zip file
        var downloadAlbum = Files.createTempFile("album", "zip");
        try(var outputStream = new FileOutputStream(downloadAlbum.toFile())) {
            streamingOutput.write(outputStream);
        }

        // Check that zip members last modified time have been set to albumEntry values
        var picturefilename = provider.findFileNamePartOfUrl(albumpictures.get(0).getImageUrl());
        var zipentry = findZipEntryFor(downloadAlbum.toFile(), picturefilename);
        assertEquals(albumpictures.get(0).getLastModified(), new Date(zipentry.getLastModifiedTime().toInstant().toEpochMilli()));
    }

    @Test
    void testDownloadAlbumEntryOnImageThatIsATextFile() throws Exception {
        // TODO endre testen til det tittelen sier at den skal være
        var replacementTitle = "Replacement title";
        var replacementDescription = "Replacement description";
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        var imageIOService = new ImageioSpiRegistration();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.setImageIOService(imageIOService);
        provider.activate(Collections.emptyMap());
        var dummyAlbum = provider.addEntry(AlbumEntry.with().parent(1).album(true).path("dummy").title("Dummy album").description("Dummy description").build()).stream().filter(e -> "dummy".equals(e.getPath())).findFirst().get();
        var modifiedEntry = AlbumEntry.with(provider.getAlbumEntry(9).get()).parent(dummyAlbum.getId()).title(replacementTitle).description(replacementDescription).build();
        var entry = provider.addEntry(modifiedEntry).stream().filter(e -> replacementDescription.equals(e.getDescription())).findFirst().get();

        var streamingOutput = provider.downloadAlbumEntry(entry.getId());
        assertNotNull(streamingOutput);
        var downloadFile = Files.createTempFile("image", "jpg").toFile();
        try(var outputStream = new FileOutputStream(downloadFile)) {
            streamingOutput.write(outputStream);
        }
    }

    @Test
    void testDownloadImageUrlToTempFileOnNonImageFile() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var imageFileName = "logback.xml";
        var lastModifiedTime = findLastModifiedTimeOfClasspathResource(imageFileName);
        var connectionFactory = mockHttpConnectionReturningClasspathResource(imageFileName, lastModifiedTime);
        provider.setConnectionFactory(connectionFactory);
        var albumEntry = AlbumEntry.with().imageUrl("http://localhost/logback.xml").title("Title").description("description").build();
        var e = assertThrows(OldAlbumException.class, () -> provider.downloadImageUrlAndStreamImageWithModifiedMetadata(albumEntry));
        assertThat(e.getMessage()).startsWith("Album entry matching id").endsWith(" not recognizable as an image. Download failed");
    }

    @Test
    void testDownloadImageUrlToTempFileWithNullImageUrl() {
        var provider = new OldAlbumServiceProvider();
        var albumEntry = AlbumEntry.with().build();
        var e = assertThrows(OldAlbumException.class, () -> provider.downloadImageUrlAndStreamImageWithModifiedMetadata(albumEntry));
        assertThat(e.getMessage()).startsWith("Unable to download album entry matching id").endsWith("imageUrl is missing");
    }

    @Test
    void testDownloadImageUrlToTempFileWithEmptyImageUrl() {
        var provider = new OldAlbumServiceProvider();
        var albumEntry = AlbumEntry.with().imageUrl("").build();
        var e = assertThrows(OldAlbumException.class, () -> provider.downloadImageUrlAndStreamImageWithModifiedMetadata(albumEntry));
        assertThat(e.getMessage()).startsWith("Unable to download album entry matching id").endsWith("imageUrl is missing");
    }

    @Test
    void testDownloadImageUrlToTempFileWithWrongImageUrl() {
        var provider = new OldAlbumServiceProvider();
        var albumEntry = AlbumEntry.with().imageUrl("https://www.bang.priv.no/sb/pics/moto/places/notfound.jpg").build();
        var e = assertThrows(OldAlbumException.class, () -> provider.downloadImageUrlAndStreamImageWithModifiedMetadata(albumEntry));
        assertThat(e.getMessage()).startsWith("Unable to download album entry matching id").contains("from url");
    }

    @Test
    void testFindMarkerSequenceAndCreateIfNotFoundWithEmptyNodeList() {
        var provider = new OldAlbumServiceProvider();
        var root = new IIOMetadataNode("root");
        assertEquals(0, root.getChildNodes().getLength());
        var markerNode = provider.findMarkerSequenceAndCreateIfNotFound(root);
        assertEquals(1, root.getChildNodes().getLength());
        assertThat(markerNode.getNodeName()).startsWith("markerSequence");
    }

    @Test
    void testSetJfifCommentFromAlbumEntryDescriptionAndCreateCommentIfNotFound() {
        var provider = new OldAlbumServiceProvider();
        var entry = AlbumEntry.with().description("some description").build();
        var markerSequence = new IIOMetadataNode("markerSequence");
        provider.setJfifCommentFromAlbumEntryDescription(markerSequence, entry);
        var comList = markerSequence.getElementsByTagName("com");
        assertEquals(1, comList.getLength());
    }

    @Test
    void testWriteDateTitleAndDescriptionToExifDataStructure() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var markerSequence = new IIOMetadataNode("markerSequence");
        var lastModified = new Date();
        var title = "A title";
        var description = "A descrption";
        var entry = AlbumEntry.with().lastModified(lastModified).title(title).description(description).build();
        provider.writeDateTitleAndDescriptionToExifDataStructure(markerSequence, entry);
        var unknown = markerSequence.getElementsByTagName("unknown");
        assertEquals(1, unknown.getLength());
        var exifNode = (IIOMetadataNode) unknown.item(0);
        var userObject = (byte[]) exifNode.getUserObject();
        assertThat(userObject).hasSize(136);
    }

    @Test
    void testWriteDateTitleAndDescriptionToExifDataStructureWithNulLastModifiedDate() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var markerSequence = new IIOMetadataNode("markerSequence");
        var title = "A title";
        var description = "A descrption";
        var entry = AlbumEntry.with().title(title).description(description).build();
        provider.writeDateTitleAndDescriptionToExifDataStructure(markerSequence, entry);
        var unknown = markerSequence.getElementsByTagName("unknown");
        assertEquals(1, unknown.getLength());
        var exifNode = (IIOMetadataNode) unknown.item(0);
        var userObject = (byte[]) exifNode.getUserObject();
        assertThat(userObject).hasSize(72);
    }

    @Test
    void testWriteDateTitleAndDescriptionToExifDataStructureWithNullTitle() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var markerSequence = new IIOMetadataNode("markerSequence");
        var lastModified = new Date();
        var description = "A descrption";
        var entry = AlbumEntry.with().lastModified(lastModified).description(description).build();
        provider.writeDateTitleAndDescriptionToExifDataStructure(markerSequence, entry);
        var unknown = markerSequence.getElementsByTagName("unknown");
        assertEquals(1, unknown.getLength());
        var exifNode = (IIOMetadataNode) unknown.item(0);
        var userObject = (byte[]) exifNode.getUserObject();
        assertThat(userObject).hasSize(116);
    }

    @Test
    void testWriteDateTitleAndDescriptionToExifDataStructureWithNullDescription() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var markerSequence = new IIOMetadataNode("markerSequence");
        var lastModified = new Date();
        var title = "A title";
        var entry = AlbumEntry.with().lastModified(lastModified).title(title).build();
        provider.writeDateTitleAndDescriptionToExifDataStructure(markerSequence, entry);
        var unknown = markerSequence.getElementsByTagName("unknown");
        assertEquals(1, unknown.getLength());
        var exifNode = (IIOMetadataNode) unknown.item(0);
        var userObject = (byte[]) exifNode.getUserObject();
        assertThat(userObject).hasSize(104);
    }

    @Test
    void testWriteDateTitleAndDescriptionToExifDataStructureWithAllMetadataNull() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var markerSequence = new IIOMetadataNode("markerSequence");
        var entry = AlbumEntry.with().build();
        provider.writeDateTitleAndDescriptionToExifDataStructure(markerSequence, entry);
        var unknown = markerSequence.getElementsByTagName("unknown");
        assertEquals(0, unknown.getLength());
    }

    @Test
    void testFormatExifUserComment() {
        var provider = new OldAlbumServiceProvider();
        var originalUserComment = "This is a user comment";

        var exifUserComment = provider.formatExifUserComment(originalUserComment);

        var splitUserComment = provider.splitUserCommentInEncodingAndComment(exifUserComment);
        assertThat(splitUserComment).hasSize(2);
        assertThat(splitUserComment.get(0)).isEqualTo(OldAlbumServiceProvider.EXIF_ASCII_ENCODING);
        var decodedComment = new String(splitUserComment.get(1), StandardCharsets.UTF_8);
        assertEquals(originalUserComment, decodedComment);
    }

    @Test
    void testReadImageMetadata() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);

        var imageUrl = "https://www.bang.priv.no/sb/pics/moto/vfr96/acirc1.jpg";
        var metadata = provider.readMetadata(imageUrl);
        assertEquals(200, metadata.getStatus());
        assertThat(metadata.getLastModified()).isAfter(Date.from(Instant.EPOCH));
        assertEquals("image/jpeg", metadata.getContentType());
        assertThat(metadata.getContentLength()).isPositive();
        assertThat(metadata.getTitle()).isNullOrEmpty();
        assertThat(metadata.getDescription()).startsWith("My VFR 750F, in front of Polarsirkelsenteret.");
    }

    @Test
    void testReadJpegWithExifMetadata() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var imageFileName = "jpeg/acirc1_with_exif_datetime.jpg";
        var lastModifiedTime = findLastModifiedTimeOfClasspathResource(imageFileName);
        var connectionFactory = mockHttpConnectionReturningClasspathResource(imageFileName, lastModifiedTime);
        provider.setConnectionFactory(connectionFactory);

        var imageMetadata = provider.readMetadata("http://localhost/acirc1_with_exif_datetime.jpg");
        assertNotNull(imageMetadata);
        assertNotEquals(new Date(lastModifiedTime), imageMetadata.getLastModified());
        assertThat(imageMetadata.getTitle()).isNullOrEmpty();
        assertThat(imageMetadata.getDescription()).startsWith("My VFR 750F");
    }

    @Test
    void testReadJpegWithDescriptionInExifMetadata() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var imageFileName = "jpeg/acirc1_with_exif_datetime_and_image_description.jpg";
        var lastModifiedTime = findLastModifiedTimeOfClasspathResource(imageFileName);
        var connectionFactory = mockHttpConnectionReturningClasspathResource(imageFileName, lastModifiedTime);
        provider.setConnectionFactory(connectionFactory);

        var imageMetadata = provider.readMetadata("http://localhost/acirc1_with_exif_datetime_and_image_description.jpg");
        assertNotNull(imageMetadata);
        assertNotEquals(new Date(lastModifiedTime), imageMetadata.getLastModified());
        assertThat(imageMetadata.getTitle()).startsWith("VFR at Arctic Circle");
        assertThat(imageMetadata.getDescription()).startsWith("My VFR 750F, in front of Polarsirkelsenteret.");
    }

    @Test
    void testReadJpegWithDescriptionAndUserCommentInExifMetadata() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var imageFileName = "jpeg/acirc1_with_exif_datetime_and_image_description_and_user_comment.jpg";
        var lastModifiedTime = findLastModifiedTimeOfClasspathResource(imageFileName);
        var connectionFactory = mockHttpConnectionReturningClasspathResource(imageFileName, lastModifiedTime);
        provider.setConnectionFactory(connectionFactory);

        var imageMetadata = provider.readMetadata("http://localhost/acirc1_with_exif_datetime_and_image_description_and_user_comment.jpg");
        assertNotNull(imageMetadata);
        assertNotEquals(new Date(lastModifiedTime), imageMetadata.getLastModified());
        assertThat(imageMetadata.getTitle()).startsWith("VFR at Arctic Circle");
        assertThat(imageMetadata.getDescription()).startsWith("Honda VFR750F in Rana");
    }

    @Test
    void testReadMetadataOnNonImageFile() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var imageFileName = "logback.xml";
        var lastModifiedTime = findLastModifiedTimeOfClasspathResource(imageFileName);
        var connectionFactory = mockHttpConnectionReturningClasspathResource(imageFileName, lastModifiedTime);
        provider.setConnectionFactory(connectionFactory);

        var imageMetadata = provider.readMetadata("http://localhost/logback.xml");
        assertNotNull(imageMetadata);
        assertNull(imageMetadata.getTitle());
        assertNull(imageMetadata.getDescription());
    }

    @Test
    void testReadImageMetadataImageNotFound() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);

        var imageUrl = "https://www.bang.priv.no/sb/pics/moto/places/gravva1.jpg";
        var e = assertThrows(OldAlbumException.class, () -> provider.readMetadata(imageUrl));
        assertThat(e.getMessage()).startsWith("HTTP Connection error when reading metadata for");
    }

    @Test
    void testReadImageMetadataServerNotFound() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);

        var imageUrl = "https://www.bang.priv.com/sb/pics/moto/places/gravva1.jpg";
        var e = assertThrows(OldAlbumException.class, () -> provider.readMetadata(imageUrl));
        assertThat(e.getMessage()).startsWith("HTTP Connection error when reading metadata for");
    }

    @Test
    void testReadImageMetadataWithNullImageUrl() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);

        var metadata = provider.readMetadata(null);
        assertNull(metadata);
    }

    @Test
    void testReadImageMetadataWithEmptyImageUrl() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);

        var metadata = provider.readMetadata("");
        assertNull(metadata);
    }

    @Test
    void testReadExifImageMetadataWithIOException() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var imageUrl = "http://localhost/image.jpg";
        var builder = ImageMetadata.with();
        var jpegSegment = mock(JPEGSegment.class);
        var exifData = mock(InputStream.class);
        when(exifData.read()).thenThrow(IOException.class);
        when(jpegSegment.data()).thenReturn(exifData);
        var exifSegment = Collections.singletonList(jpegSegment);

        var e = assertThrows(RuntimeException.class, () -> provider.readExifImageMetadata(imageUrl, builder, exifSegment));
        assertThat(e.getMessage()).startsWith("Error reading EXIF data of");
    }

    @Test
    void testExtractExifDatetimeWithParseException() {
        var provider = new OldAlbumServiceProvider();
        var builder = ImageMetadata.with();
        var entry = mock(Entry.class);
        when(entry.getValueAsString()).thenReturn("not a parsable date");
        var imageUrl = "http://localhost/image.jpg";

        var e = assertThrows(RuntimeException.class, () -> provider.extractExifDatetime(builder, entry, imageUrl));
        assertThat(e.getMessage()).startsWith("Error parsing EXIF 306/DateTime entry of");
    }

    @Test
    void testDumpDatabaseSqlNotLoggedIn() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());

        var allroutesCount = findAlbumentriesRows(datasource, false);
        var sql = provider.dumpDatabaseSql(null, false);
        assertThat(sql)
            .contains("insert into")
            .hasLineCount(allroutesCount + 3);

        // Create an empty database initialized with the oldalbum schema
        // Then use liquibase to fill the database with the dumped content
        var emptybase = createEmptyBase("emptyoldalbum1");
        var rowsBeforeInsert = findAlbumentriesRows(emptybase, false);
        assertEquals(0, rowsBeforeInsert);
        setDatabaseContentAsLiquibaseChangelog(emptybase, sql);

        // Check that the empty database now has the same number of rows as the original
        var rowsInOriginal = findAlbumentriesRows(datasource, false);
        var rowsAfterInsert = findAlbumentriesRows(emptybase, false);
        assertEquals(rowsInOriginal, rowsAfterInsert);

        // Try inserting a row to verify that the id autoincrement doesn't
        // create duplicated
        try(Connection connection = emptybase.getConnection()) {
            addAlbumEntry(connection, 0, "/album/", true, "Album", "This is an album", null, null, 1, null, null, 0);
        }

        var rowsAfterInsertingExtraRow = findAlbumentriesRows(emptybase, false);
        assertThat(rowsAfterInsertingExtraRow).isGreaterThan(rowsInOriginal);
    }

    @Test
    void testDumpDatabaseSqlLoggedIn() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());

        var allroutesCount = findAlbumentriesRows(datasource, true);
        var sql = provider.dumpDatabaseSql(null, true);
        assertThat(sql)
            .contains("insert into")
            .hasLineCount(allroutesCount + 3);

        // Create an empty database initialized with the oldalbum schema
        // Then use liquibase to fill the database with the dumped content
        var emptybase = createEmptyBase("emptyoldalbum2");
        var rowsBeforeInsert = findAlbumentriesRows(emptybase, false);
        assertEquals(0, rowsBeforeInsert);
        setDatabaseContentAsLiquibaseChangelog(emptybase, sql);

        // Check that the empty database now has the same number of rows as the original
        var rowsInOriginal = findAlbumentriesRows(datasource, true);
        var rowsAfterInsert = findAlbumentriesRows(emptybase, true);
        assertEquals(rowsInOriginal, rowsAfterInsert);

        // Try inserting a row to verify that the id autoincrement doesn't
        // create duplicated
        try(Connection connection = emptybase.getConnection()) {
            addAlbumEntry(connection, 0, "/album/", true, "Album", "This is an album", null, null, 1, null, null, 0);
        }

        var rowsAfterInsertingExtraRow = findAlbumentriesRows(emptybase, true);
        assertThat(rowsAfterInsertingExtraRow).isGreaterThan(rowsInOriginal);
    }

    @Test
    void testDumpDatabaseSqlWithSqlError() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var statement = mock(PreparedStatement.class);
        when(statement.executeQuery()).thenThrow(SQLException.class);
        var connection = mock(Connection.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        var datasource = mock(DataSource.class);
        when(datasource.getConnection()).thenReturn(connection);
        provider.setDataSource(datasource);

        provider.dumpDatabaseSql(null, false);
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    @Test
    void testDumpDatabaseSqlToOutputStreamWithIOError() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        var outputstream = mock(OutputStream.class);
        doThrow(IOException.class).when(outputstream).close();

        provider.dumpDatabaseSqlToOutputStream(false, outputstream);
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    @Test
    void testBatchAddPictures() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var database = createEmptyBase("emptyoldalbum3");
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(database);
        provider.activate(Collections.emptyMap());

        // Mocked HTTP request
        var connectionFactory = mock(HttpConnectionFactory.class);
        var connection = mock(HttpURLConnection.class);
        when(connection.getResponseCode()).thenReturn(200);
        var connectionStubbing = when(connection.getInputStream());
        connectionStubbing = connectionStubbing.thenReturn(getClass().getClassLoader().getResourceAsStream("html/pictures_directory_list_nginx_mkpicidx.html"));
        for (int i=0; i<110; ++i) { // Need 110 JPEG streams
            connectionStubbing = connectionStubbing.thenReturn(getClass().getClassLoader().getResourceAsStream("jpeg/acirc1.jpg"));
        }

        connectionStubbing = connectionStubbing.thenReturn(getClass().getClassLoader().getResourceAsStream("html/pictures_directory_list_nginx_mkpicidx.html"));
        for (int i=0; i<110; ++i) { // Need 110 JPEG streams
            connectionStubbing = connectionStubbing.thenReturn(getClass().getClassLoader().getResourceAsStream("jpeg/acirc1.jpg"));
        }

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

        // Check that sort is incremented during batch import
        var firstSortValue = entriesAfterBatchAdd.stream().filter(e -> e.getParent() == parentId).mapToInt(AlbumEntry::getSort).min().getAsInt();
        var lastSortValue = entriesAfterBatchAdd.stream().filter(e -> e.getParent() == parentId).mapToInt(AlbumEntry::getSort).max().getAsInt();
        assertThat(lastSortValue).isGreaterThan(firstSortValue);

        // Check that a second import will continue to increase the sort value
        var entriesAfterSecondBatchAdd = provider.batchAddPictures(request);
        var lastSortValueInSecondBatchAdd = entriesAfterSecondBatchAdd.stream().filter(e -> e.getParent() == parentId).mapToInt(AlbumEntry::getSort).max().getAsInt();
        assertThat(lastSortValueInSecondBatchAdd).isGreaterThan(lastSortValue);
    }

    @Test
    void testBatchAddPicturesFromInstaloaderDump() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var database = createEmptyBase("emptyoldalbum4");
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(database);
        provider.activate(Collections.emptyMap());

        // Mocked HTTP request
        var connectionFactory = mock(HttpConnectionFactory.class);
        var connection = mock(HttpURLConnection.class);
        when(connection.getResponseCode()).thenReturn(200);
        var connectionStubbing = when(connection.getInputStream());
        connectionStubbing = connectionStubbing.thenReturn(getClass().getClassLoader().getResourceAsStream("html/instaloader_dump_directory_list_nginx_index.html"));
        connectionStubbing = connectionStubbing.thenReturn(getClass().getClassLoader().getResourceAsStream("jpeg/2013-07-22_07-48-41_UTC.jpg"));
        connectionStubbing = connectionStubbing.thenReturn(getClass().getClassLoader().getResourceAsStream("txt/2013-07-22_07-48-41_UTC.txt"));
        connectionStubbing = connectionStubbing.thenReturn(getClass().getClassLoader().getResourceAsStream("jpeg/2013-07-23_18-50-21_UTC.jpg"));
        connectionStubbing = connectionStubbing.thenReturn(getClass().getClassLoader().getResourceAsStream("txt/2013-07-23_18-50-21_UTC.txt"));
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        provider.setConnectionFactory(connectionFactory);

        // Prepare empty database with an album to put pictures in
        // First add root album
        var rootAlbum = AlbumEntry.with()
            .path("/")
            .album(true)
            .title("Old pictures")
            .requireLogin(false)
            .build();
        provider.addEntry(rootAlbum);
        var parentForBatchAddedPictures = AlbumEntry.with()
            .parent(1)
            .path("/daisy/")
            .album(true)
            .title("Kaninen Daisy")
            .description("En liten svart og hvit løvehodekanin")
            .sort(2)
            .requireLogin(false)
            .build();
        var entriesBeforeBatchAdd = provider.addEntry(parentForBatchAddedPictures);
        var parentId = entriesBeforeBatchAdd.get(1).getId();

        // Do the batch import
        var defaultTitle = "Daisy";
        var request = BatchAddPicturesRequest.with()
            .parent(parentId)
            .batchAddUrl("https://www.bang.priv.no/tmpdownload/kaninen_daisy/")
            .defaultTitle(defaultTitle)
            .build();
        var entriesAfterBatchAdd = provider.batchAddPictures(request);

        // Check that pictures have been added
        assertThat(entriesAfterBatchAdd).hasSizeGreaterThan(entriesBeforeBatchAdd.size());

        // Check that sort is incremented during batch import
        var firstSortValue = entriesAfterBatchAdd.stream().filter(e -> e.getParent() == parentId).mapToInt(AlbumEntry::getSort).min().getAsInt();
        var lastSortValue = entriesAfterBatchAdd.stream().filter(e -> e.getParent() == parentId).mapToInt(AlbumEntry::getSort).max().getAsInt();
        assertThat(lastSortValue).isGreaterThan(firstSortValue);

        // Check that description of first imported image is content of description txt file
        // and check that the title starts with the default title of the batch import
        var firstImage = entriesAfterBatchAdd.stream().filter(ent -> ent.getParent() == parentId).findFirst().get();
        var expectedDescription = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("txt/2013-07-22_07-48-41_UTC.txt"), StandardCharsets.UTF_8);
        assertEquals(expectedDescription, firstImage.getDescription());
        assertThat(firstImage.getTitle()).startsWith(defaultTitle);
    }

    @Test
    void testBatchAddPicturesFromInstaloaderDumpNoDefaultTitle() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var database = createEmptyBase("emptyoldalbum5");
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(database);
        provider.activate(Collections.emptyMap());

        // Mocked HTTP request
        var connectionFactory = mock(HttpConnectionFactory.class);
        var connection = mock(HttpURLConnection.class);
        when(connection.getResponseCode()).thenReturn(200);
        var connectionStubbing = when(connection.getInputStream());
        connectionStubbing = connectionStubbing.thenReturn(getClass().getClassLoader().getResourceAsStream("html/instaloader_dump_directory_list_nginx_index.html"));
        connectionStubbing = connectionStubbing.thenReturn(getClass().getClassLoader().getResourceAsStream("jpeg/2013-07-22_07-48-41_UTC.jpg"));
        connectionStubbing = connectionStubbing.thenReturn(getClass().getClassLoader().getResourceAsStream("txt/2013-07-22_07-48-41_UTC.txt"));
        connectionStubbing = connectionStubbing.thenReturn(getClass().getClassLoader().getResourceAsStream("jpeg/2013-07-23_18-50-21_UTC.jpg"));
        connectionStubbing = connectionStubbing.thenReturn(getClass().getClassLoader().getResourceAsStream("txt/2013-07-23_18-50-21_UTC.txt"));
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        provider.setConnectionFactory(connectionFactory);

        // Prepare empty database with an album to put pictures in
        // First add root album
        var rootAlbum = AlbumEntry.with()
            .path("/")
            .album(true)
            .title("Old pictures")
            .requireLogin(false)
            .build();
        provider.addEntry(rootAlbum);
        var parentForBatchAddedPictures = AlbumEntry.with()
            .parent(1)
            .path("/daisy/")
            .album(true)
            .title("Kaninen Daisy")
            .description("En liten svart og hvit løvehodekanin")
            .sort(2)
            .requireLogin(false)
            .build();
        var entriesBeforeBatchAdd = provider.addEntry(parentForBatchAddedPictures);
        var parentId = entriesBeforeBatchAdd.get(1).getId();

        // Do the batch import
        var request = BatchAddPicturesRequest.with()
            .parent(parentId)
            .batchAddUrl("https://www.bang.priv.no/tmpdownload/kaninen_daisy/")
            .build();
        var entriesAfterBatchAdd = provider.batchAddPictures(request);

        // Check that pictures have been added
        assertThat(entriesAfterBatchAdd).hasSizeGreaterThan(entriesBeforeBatchAdd.size());

        // Check that sort is incremented during batch import
        var firstSortValue = entriesAfterBatchAdd.stream().filter(e -> e.getParent() == parentId).mapToInt(AlbumEntry::getSort).min().getAsInt();
        var lastSortValue = entriesAfterBatchAdd.stream().filter(e -> e.getParent() == parentId).mapToInt(AlbumEntry::getSort).max().getAsInt();
        assertThat(lastSortValue).isGreaterThan(firstSortValue);

        // Check that description of first imported image is content of description txt file
        // and check that the title starts with the default title of the batch import
        var firstImage = entriesAfterBatchAdd.stream().filter(ent -> ent.getParent() == parentId).findFirst().get();
        var expectedDescription = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("txt/2013-07-22_07-48-41_UTC.txt"), StandardCharsets.UTF_8);
        assertEquals(expectedDescription, firstImage.getDescription());
        assertThat(firstImage.getTitle()).isNull();
    }

    @Test
    void testBatchAddPicturesWithThumbnails() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var database = createEmptyBase("emptyoldalbum3");
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(database);
        provider.activate(Collections.emptyMap());

        // Mocked HTTP request
        var connectionFactory = mock(HttpConnectionFactory.class);
        var connection = mock(HttpURLConnection.class);
        when(connection.getResponseCode()).thenReturn(200);
        var connectionStubbing = when(connection.getInputStream());
        connectionStubbing = connectionStubbing.thenReturn(getClass().getClassLoader().getResourceAsStream("html/pictures_directory_list_nginx_mkpicidx.html"));
        for (int i=0; i<110; ++i) { // Need 110 JPEG streams
            connectionStubbing = connectionStubbing.thenReturn(getClass().getClassLoader().getResourceAsStream("jpeg/acirc1.jpg"));
        }

        connectionStubbing = connectionStubbing.thenReturn(getClass().getClassLoader().getResourceAsStream("html/pictures_directory_list_nginx_mkpicidx.html"));
        for (int i=0; i<110; ++i) { // Need 110 JPEG streams
            connectionStubbing = connectionStubbing.thenReturn(getClass().getClassLoader().getResourceAsStream("jpeg/acirc1.jpg"));
        }

        when(connectionFactory.connect(anyString())).thenReturn(connection);
        provider.setConnectionFactory(connectionFactory);

        // Prepare empty database with an album to put pictures in
        var parentForBatchAddedPictures = AlbumEntry.with()
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

        // Check that sort is incremented during batch import
        var firstSortValue = entriesAfterBatchAdd.stream().filter(e -> e.getParent() == parentId).mapToInt(AlbumEntry::getSort).min().getAsInt();
        var lastSortValue = entriesAfterBatchAdd.stream().filter(e -> e.getParent() == parentId).mapToInt(AlbumEntry::getSort).max().getAsInt();
        assertThat(lastSortValue).isGreaterThan(firstSortValue);

        // Check that a second import will continue to increase the sort value
        var entriesAfterSecondBatchAdd = provider.batchAddPictures(request);
        var lastSortValueInSecondBatchAdd = entriesAfterSecondBatchAdd.stream().filter(e -> e.getParent() == parentId).mapToInt(AlbumEntry::getSort).max().getAsInt();
        assertThat(lastSortValueInSecondBatchAdd).isGreaterThan(lastSortValue);
    }

    @Test
    void testBatchAddPicturesWith404OnTheBatchUrl() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());

        // Mocked HTTP request
        var connectionFactory = mock(HttpConnectionFactory.class);
        var connection = mock(HttpURLConnection.class);
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
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());

        // Mocked HTTP request
        var connectionFactory = mock(HttpConnectionFactory.class);
        var connection = mock(HttpURLConnection.class);
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

    @Test
    void testGetEntryWithSQLException() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var database = mock(DataSource.class);
        when(database.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(database);
        assertThat(logservice.getLogmessages()).isEmpty();
        assertThat(provider.getAlbumEntry(1)).isEmpty();
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    @Test
    void testFindHighestSortValueInParentAlbumWithSqlException( ) throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var database = mock(DataSource.class);
        when(database.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(database);
        assertThat(logservice.getLogmessages()).isEmpty();
        assertEquals(0, provider.findHighestSortValueInParentAlbum(1));
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    @Test
    void testFindHighestSortValueInParentAlbumWithEmptyQueryResult( ) throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var results = mock(ResultSet.class);
        var statement = mock(PreparedStatement.class);
        when(statement.executeQuery()).thenReturn(results);
        var connection = mock(Connection.class);
        when (connection.prepareStatement(anyString())).thenReturn(statement);
        var database = mock(DataSource.class);
        when(database.getConnection()).thenReturn(connection);
        provider.setDataSource(database);
        assertThat(logservice.getLogmessages()).isEmpty();
        assertEquals(0, provider.findHighestSortValueInParentAlbum(1));
        assertThat(logservice.getLogmessages()).isEmpty();
    }

    @Test
    void testFindThumbnailUrlWhenNothingCanBeFound() {
        var provider = new OldAlbumServiceProvider();
        var img = mock(Element.class);
        when(img.absUrl("src")).thenReturn("");
        var imgs = mock(Elements.class);
        when(imgs.get(0)).thenReturn(img);
        var link = mock(Element.class);
        when(link.select("img")).thenReturn(imgs);

        var thumbnailUrl = provider.findThumbnailUrl(link);
        assertNull(thumbnailUrl);
    }

    @Test
    void testFindLastModifiedDate() {
        var provider = new OldAlbumServiceProvider();
        var now = new Date();
        var metadata = ImageMetadata.with().lastModified(now).build();

        var lastModifiedDate = provider.findLastModifiedDate(metadata, null);

        assertEquals(now, lastModifiedDate);
    }

    @Test
    void testFindLastModifiedDateWhenMetadataIsNull() {
        var provider = new OldAlbumServiceProvider();

        var lastModifiedDate = provider.findLastModifiedDate(null, null);

        assertNull(lastModifiedDate);
    }

    @Test
    void testFindLastModifiedDateWhenMetadataDateIsNull() {
        var provider = new OldAlbumServiceProvider();
        var metadata = ImageMetadata.with().build();

        var lastModifiedDate = provider.findLastModifiedDate(metadata, null);

        assertNull(lastModifiedDate);
    }

    @Test
    void testFindLastModifiedDateWithImportYearSet() {
        var provider = new OldAlbumServiceProvider();
        var now = new Date();
        var importYear = 1967;
        var metadata = ImageMetadata.with().lastModified(now).build();

        var lastModifiedDate = provider.findLastModifiedDate(metadata, importYear);

        assertThat(lastModifiedDate).hasYear(importYear);
    }

    @Test
    void testFindLastModifiedDateWithImportYearSetAndNullMetadata() {
        var provider = new OldAlbumServiceProvider();
        var importYear = 1967;

        var lastModifiedDate = provider.findLastModifiedDate(null, importYear);

        assertThat(lastModifiedDate).hasYear(importYear);
    }

    @Test
    void testFindLastModifiedDateWithImportYearSetAndNullMetadataLastModifiedDate() {
        var provider = new OldAlbumServiceProvider();
        var importYear = 1967;
        var metadata = ImageMetadata.with().build();

        var lastModifiedDate = provider.findLastModifiedDate(metadata, importYear);

        assertThat(lastModifiedDate).hasYear(importYear);
    }

    @Test
    void testSortAlbumEntriesByDate() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        provider.setDataSource(datasource);
        provider.activate(Collections.emptyMap());
        var allroutes = provider.addEntry(AlbumEntry.with().parent(1).path("/albumtosort/").album(true).build());
        var albumToSort = allroutes.stream().filter(r -> r.getPath().equals("/albumtosort/")).findFirst().get();
        var albumid = albumToSort.getId();
        provider.addEntry(AlbumEntry.with().parent(albumid).path("/b").album(false).sort(1).lastModified(parseDate("1971-02-25T13:13:22Z")).build());
        provider.addEntry(AlbumEntry.with().parent(albumid).path("/a").album(false).sort(2).lastModified(parseDate("1967-04-10T11:27:31Z")).build());
        provider.addEntry(AlbumEntry.with().parent(albumid).path("/d").album(false).sort(3).lastModified(parseDate("2022-12-24T17:10:11Z")).build());
        provider.addEntry(AlbumEntry.with().parent(albumid).path("/c").album(false).sort(4).lastModified(parseDate("2014-10-12T10:39:40Z")).build());
        allroutes = provider.sortByDate(albumid);
        var albumentries = allroutes.stream().filter(r -> r.getParent() == albumid).sorted(Comparator.comparingInt(AlbumEntry::getSort)).collect(Collectors.toList());
        var albumentrypaths = albumentries.stream().map(e -> e.getPath()).collect(Collectors.toList());
        assertThat(albumentrypaths).containsExactly("/a", "/b", "/c", "/d");
    }

    @Test
    void testSortAlbumEntriesByDateWhenSqlFails() throws Exception {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var database = mock(DataSource.class);
        when(database.getConnection()).thenThrow(SQLException.class);
        provider.setDataSource(database);
        provider.activate(Collections.emptyMap());

        var e = assertThrows(OldAlbumException.class, () -> provider.sortByDate(1));
        assertThat(e.getMessage()).contains("Failed to fetch album entries to sort");
    }

    @Test
    void testDefaultLocale() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var database = mock(DataSource.class);
        provider.setDataSource(database);
        provider.activate(Collections.singletonMap("defaultlocale", "nb_NO"));
        assertEquals(NB_NO, provider.defaultLocale());
    }

    @Test
    void testDefaultLocaleWhenLocaleNotInConfig() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var database = mock(DataSource.class);
        provider.setDataSource(database);
        provider.activate(Collections.singletonMap("DataSource.target", "value \"(osgi.jndi.service.name=jdbc/oldalbum)\""));
        assertNull(provider.defaultLocale());
    }

    @Test
    void testAvailableLocales() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var database = mock(DataSource.class);
        provider.setDataSource(database);
        provider.activate(Collections.singletonMap("defaultlocale", "nb_NO"));
        var locales = provider.availableLocales();
        assertThat(locales).isNotEmpty().contains(LocaleBean.with().locale(provider.defaultLocale()).build());
    }

    @Test
    void testDisplayTextsForDefaultLocale() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var database = mock(DataSource.class);
        provider.setDataSource(database);
        provider.activate(Collections.singletonMap("defaultlocale", "nb_NO"));
        var displayTexts = provider.displayTexts(provider.defaultLocale());
        assertThat(displayTexts).isNotEmpty();
    }

    @Test
    void testDisplayText() {
        var provider = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        provider.setLogService(logservice);
        var database = mock(DataSource.class);
        provider.setDataSource(database);
        provider.activate(Collections.singletonMap("defaultlocale", "nb_NO"));
        var text1 = provider.displayText("hi", "nb_NO");
        assertEquals("Hei", text1);
        var text2 = provider.displayText("hi", "en_GB");
        assertEquals("Hi", text2);
        var text3 = provider.displayText("hi", "");
        assertEquals("Hei", text3);
        var text4 = provider.displayText("hi", null);
        assertEquals("Hei", text4);
    }

    @Test
    void testHrefIsJpeg() {
        var diasJpeg = "https://www.bang.priv.no/bilder/202349_001396/Export%20JPG%2016Base/R1-08031-0106.JPG";
        var diasParent = "..";
        var instaloaderJpeg = "https://www.bang.priv.no/tmpdownload/kaninen_daisy/2013-07-22_07-48-41_UTC.jpg";
        var instaloaderParent = "https://www.bang.priv.no/tmpdownload/";
        var instaloaderTxt = "https://www.bang.priv.no/tmpdownload/kaninen_daisy/2013-07-22_07-48-41_UTC.txt";
        assertTrue(OldAlbumServiceProvider.hrefIsJpeg(diasJpeg));
        assertFalse(OldAlbumServiceProvider.hrefIsJpeg(diasParent));
        assertTrue(OldAlbumServiceProvider.hrefIsJpeg(instaloaderJpeg));
        assertFalse(OldAlbumServiceProvider.hrefIsJpeg(instaloaderParent));
        assertFalse(OldAlbumServiceProvider.hrefIsJpeg(instaloaderTxt));
        var extensionAllCapsJpeg = "https://www.bang.priv.no/bilder/202349_001396/Export%20JPG%2016Base/R1-08031-0106.JPEG";
        assertTrue(OldAlbumServiceProvider.hrefIsJpeg(extensionAllCapsJpeg));
        var extensionLowercaseJpeg = "https://www.bang.priv.no/bilder/202349_001396/Export%20JPG%2016Base/R1-08031-0106.jpeg";
        assertTrue(OldAlbumServiceProvider.hrefIsJpeg(extensionLowercaseJpeg));
    }

    @Test
    void testConvertJpegUrlToTxtUrl() {
        var instaloaderJpeg = "https://www.bang.priv.no/tmpdownload/kaninen_daisy/2013-07-22_07-48-41_UTC.jpg";
        var instaloaderTxt = "https://www.bang.priv.no/tmpdownload/kaninen_daisy/2013-07-22_07-48-41_UTC.txt";
        assertEquals(instaloaderTxt, OldAlbumServiceProvider.convertJpegUrlToTxtUrl(instaloaderJpeg));
        var diasJpeg = "https://www.bang.priv.no/bilder/202349_001396/Export%20JPG%2016Base/R1-08031-0106.JPG";
        var diasTxt = "https://www.bang.priv.no/bilder/202349_001396/Export%20JPG%2016Base/R1-08031-0106.txt";
        assertEquals(diasTxt, OldAlbumServiceProvider.convertJpegUrlToTxtUrl(diasJpeg));
    }

    private int findAlbumentriesRows(DataSource ds, boolean isLoggedIn) throws SQLException {
        var sql = "select count(albumentry_id) from albumentries where (not require_login or (require_login and require_login=?))";
        try (var connection = ds.getConnection()) {
            try(var statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, isLoggedIn);
                try (var results = statement.executeQuery()) {
                    if (results.next()) {
                        return results.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    private static DataSource createEmptyBase(String dbname) throws Exception {
        var derbyDataSourceFactory = new DerbyDataSourceFactory();
        var properties = new Properties();
        properties.setProperty(DataSourceFactory.JDBC_URL, "jdbc:derby:memory:" + dbname + ";create=true");
        var emptyDatasource = derbyDataSourceFactory.createDataSource(properties);
        try (var connection = emptyDatasource.getConnection()) {
            var oldAlbumLiquibase = new OldAlbumLiquibase();
            oldAlbumLiquibase.createInitialSchema(connection);
        }

        try (var connection = emptyDatasource.getConnection()) {
            var oldAlbumLiquibase = new OldAlbumLiquibase();
            oldAlbumLiquibase.updateSchema(connection);
        }

        return emptyDatasource;
    }

    static DataSource createNewTestDatabase(String databasename) throws Exception, SQLException {
        var database = createEmptyBase(databasename);
        var logservice = new MockLogService();
        var preHook = new OldAlbumDerbyTestDatabase();
        preHook.setLogService(logservice);
        preHook.activate();
        preHook.prepare(database);
        return database;
    }

    private void setDatabaseContentAsLiquibaseChangelog(DataSource datasource, String contentLiquibaseChangelog) throws Exception {
        var contentByFileName = new HashMap<String, String>();
        contentByFileName.put("dumproutes.sql", contentLiquibaseChangelog);
        try(var connection = datasource.getConnection()) {
            try(var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection))) {
                Map<String, Object> scopeObjects = Map.of(
                    Scope.Attr.database.name(), database,
                    Scope.Attr.resourceAccessor.name(), new MockResourceAccessor(contentByFileName));

                Scope.child(scopeObjects, (ScopedRunner<?>) () -> new CommandScope("update")
                            .addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database)
                            .addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, "dumproutes.sql")
                            .addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, new ChangeLogParameters(database))
                            .execute());
            }
        }
    }

    private void addAlbumEntry(Connection connection, int parent, String path, boolean album, String title, String description, String imageUrl, String thumbnailUrl, int sort, Date lastmodified, String contenttype, int size) throws Exception {
        var sql = "insert into albumentries (parent, localpath, album, title, description, imageurl, thumbnailurl, sort, lastmodified, contenttype, contentlength) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(var statement = connection.prepareStatement(sql)) {
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

    private Date parseDate(String iso8601date) {
        return Date.from(Instant.parse(iso8601date));
    }

    private ZipEntry findZipEntryFor(File downloadAlbum, String picturefilename) throws Exception {

        try (var zipfile = new ZipInputStream(new FileInputStream(downloadAlbum))) {
            var zipEntry = zipfile.getNextEntry();
            while(zipEntry != null) {
                if (picturefilename.equals(zipEntry.getName())) {
                    return zipEntry;
                }

                zipEntry = zipfile.getNextEntry();
            }
        }

        return null;
    }

    HttpConnectionFactory mockHttpConnectionReturningClasspathResource(String classpathResource, long lastModifiedTime) throws IOException {
        var connectionFactory = mock(HttpConnectionFactory.class);
        var inputstream = getClass().getClassLoader().getResourceAsStream(classpathResource);
        var connection = mock(HttpURLConnection.class);
        when(connection.getLastModified()).thenReturn(lastModifiedTime);
        when(connection.getInputStream()).thenReturn(inputstream);
        when(connectionFactory.connect(anyString())).thenReturn(connection);
        return connectionFactory;
    }

    long findLastModifiedTimeOfClasspathResource(String classpathResource) throws IOException, URISyntaxException {
        var imageFileAttributes = Files.readAttributes(Path.of(getClass().getClassLoader().getResource(classpathResource).toURI()), BasicFileAttributes.class);
        var lastModifiedTime = imageFileAttributes.lastModifiedTime().toMillis();
        return lastModifiedTime;
    }

}
