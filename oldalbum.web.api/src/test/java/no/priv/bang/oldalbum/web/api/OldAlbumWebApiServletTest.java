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
package no.priv.bang.oldalbum.web.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static javax.ws.rs.core.MediaType.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.glassfish.jersey.server.ServerProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.osgi.service.log.LogService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletOutputStream;

import no.priv.bang.oldalbum.services.OldAlbumException;
import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.oldalbum.services.bean.ImageMetadata;
import no.priv.bang.oldalbum.services.bean.ImageRequest;
import no.priv.bang.oldalbum.services.bean.LocaleBean;
import no.priv.bang.oldalbum.services.bean.LoginResult;
import no.priv.bang.oldalbum.web.api.resources.ErrorMessage;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.UserManagementService;

class OldAlbumWebApiServletTest extends ShiroTestBase {
    private final static Locale NB_NO = Locale.forLanguageTag("nb-no");
    private final static Locale EN_UK = Locale.forLanguageTag("en-uk");

    final static ObjectMapper mapper = new ObjectMapper();
    private static List<AlbumEntry> allroutes;
    static String dumpedroutes = loadClasspathResourceIntoString("dumproutes.sql");

    @BeforeAll
    static void beforeAllTests() throws Exception {
        allroutes = mapper.readValue(OldAlbumWebApiServletTest.class.getClassLoader().getResourceAsStream("allroutes.json"), new TypeReference<List<AlbumEntry>>() {});
    }

    @BeforeEach
    void beforeEachTest() {
        removeWebSubjectFromThread();
    }

    @Test
    void testCheckLogin() throws Exception {
        createSubjectAndBindItToThread();
        var logservice = new MockLogService();
        var backendService = mock(OldAlbumService.class);
        when(backendService.fetchAllRoutes(any(), anyBoolean())).thenReturn(allroutes);
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice, useradmin);
        var request = buildGetUrl("/login");
        var response = new MockHttpServletResponse();
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var loginresult = mapper.readValue(getBinaryContent(response), LoginResult.class);
        assertTrue(loginresult.isCanLogin());
    }

    @Test
    void testFetchRoutes() throws Exception {
        var logservice = new MockLogService();
        var backendService = mock(OldAlbumService.class);
        when(backendService.fetchAllRoutes(any(), anyBoolean())).thenReturn(allroutes);
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice, useradmin);
        var request = buildGetUrl("/allroutes");
        var response = new MockHttpServletResponse();
        createSubjectAndBindItToThread();
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var routes = mapper.readValue(getBinaryContent(response), new TypeReference<List<AlbumEntry>>() { });
        assertThat(routes).isNotEmpty();
    }

    @Test
    void testDumpRoutesSql() throws Exception {
        var logservice = new MockLogService();
        var backendService = mock(OldAlbumService.class);
        when(backendService.dumpDatabaseSql(null, false)).thenReturn(dumpedroutes);
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice, useradmin);
        var request = buildGetUrl("/dumproutessql");
        var response = new MockHttpServletResponse();
        createSubjectAndBindItToThread();
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals("application/sql", response.getContentType());
        var sql = response.getOutputStreamContent();
        assertThat(sql).contains("--liquibase formatted sql");
    }

    @Test
    void testModifyalbum() throws Exception {
        var modifiedAlbum = AlbumEntry.with().id(2).parent(1).path("/moto/").album(true).title("Album has been updated").description("This is an updated description").sort(1).childcount(2).build();
        var logservice = new MockLogService();
        var backendService = mock(OldAlbumService.class);
        when(backendService.updateEntry(any())).thenReturn(Arrays.asList(modifiedAlbum));
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice, useradmin);
        var request = buildPostUrl("/modifyalbum", modifiedAlbum);
        var response = new MockHttpServletResponse();
        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var routes = mapper.readValue(getBinaryContent(response), new TypeReference<List<AlbumEntry>>() { });
        assertThat(routes).isNotEmpty();
    }

    @Test
    void testModifyalbumLoggedInAsUserWithoutOldalbumadmin() throws Exception {
        var modifiedAlbum = AlbumEntry.with().id(2).parent(1).path("/moto/").album(true).title("Album has been updated").description("This is an updated description").sort(1).childcount(2).build();
        var logservice = new MockLogService();
        var backendService = mock(OldAlbumService.class);
        when(backendService.updateEntry(any())).thenReturn(Arrays.asList(modifiedAlbum));
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice, useradmin);
        var request = buildPostUrl("/modifyalbum", modifiedAlbum);
        var response = new MockHttpServletResponse();
        createSubjectAndBindItToThread();
        loginUser("jad", "1ad");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
    }

    @Test
    void testModifyalbumWhenNotLoggedIn() throws Exception {
        var modifiedAlbum = AlbumEntry.with().id(2).parent(1).path("/moto/").album(true).title("Album has been updated").description("This is an updated description").sort(1).childcount(2).build();
        var logservice = new MockLogService();
        var backendService = mock(OldAlbumService.class);
        when(backendService.updateEntry(any())).thenReturn(Arrays.asList(modifiedAlbum));
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice, useradmin);
        var request = buildPostUrl("/modifyalbum", modifiedAlbum);
        var response = new MockHttpServletResponse();
        createSubjectAndBindItToThread();
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    void testAddAlbum() throws Exception {
        var albumToAdd = AlbumEntry.with().parent(1).path("/newalbum/").album(true).title("A new album").description("A new album for new pictures").sort(2).build();
        var logservice = new MockLogService();
        var backendService = mock(OldAlbumService.class);
        when(backendService.addEntry(any())).thenReturn(Arrays.asList(albumToAdd));
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice, useradmin);
        var request = buildPostUrl("/addalbum", albumToAdd);
        var response = new MockHttpServletResponse();
        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var routes = mapper.readValue(getBinaryContent(response), new TypeReference<List<AlbumEntry>>() { });
        assertThat(routes).isNotEmpty();
    }

    @Test
    void testAddPicture() throws Exception {
        var pictureToAdd = AlbumEntry.with().parent(1).path("/newalbum/").album(true).title("A new album").description("A new album for new pictures").sort(2).build();
        var logservice = new MockLogService();
        var backendService = mock(OldAlbumService.class);
        when(backendService.addEntry(any())).thenReturn(Arrays.asList(pictureToAdd));
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice, useradmin);
        var request = buildPostUrl("/addpicture", pictureToAdd);
        var response = new MockHttpServletResponse();
        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var routes = mapper.readValue(getBinaryContent(response), new TypeReference<List<AlbumEntry>>() { });
        assertThat(routes).isNotEmpty();
    }

    @Test
    void testDeleteEntry() throws Exception {
        var pictureToDelete = AlbumEntry.with().id(7).parent(3).path("/oldalbum/moto/places/grava3").album(false).title("").description("Tyrigrava, view from the north. Lotsa bikes here too").imageUrl("https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg").thumbnailUrl("https://www.bang.priv.no/sb/pics/moto/places/icons/grava3.gif").sort(3).lastModified(new Date()).contentType("image/jpeg").contentLength(71072).build();
        var logservice = new MockLogService();
        var backendService = mock(OldAlbumService.class);
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice, useradmin);
        var request = buildPostUrl("/deleteentry", pictureToDelete);
        var response = new MockHttpServletResponse();
        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var routes = mapper.readValue(getBinaryContent(response), new TypeReference<List<AlbumEntry>>() { });
        assertEquals(0, routes.size());
    }

    @Test
    void testDeleteSelection() throws Exception {
        var selection = Arrays.asList(7);
        var logservice = new MockLogService();
        var backendService = mock(OldAlbumService.class);
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice, useradmin);
        var request = buildPostUrl("/deleteselection", selection);
        var response = new MockHttpServletResponse();
        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var routes = mapper.readValue(getBinaryContent(response), new TypeReference<List<AlbumEntry>>() { });
        assertEquals(0, routes.size());
    }

    @Test
    void testMoveEntryUp() throws Exception {
        var albumToMove = AlbumEntry.with().id(2).parent(1).path("/moto/").album(true).title("Album has been updated").description("This is an updated description").sort(2).childcount(2).build();
        var movedAlbum = AlbumEntry.with().id(2).parent(1).path("/moto/").album(true).title("Album has been updated").description("This is an updated description").sort(1).childcount(2).build();
        var logservice = new MockLogService();
        var backendService = mock(OldAlbumService.class);
        when(backendService.moveEntryUp(any())).thenReturn(Arrays.asList(movedAlbum));
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice, useradmin);
        var request = buildPostUrl("/movealbumentryup", albumToMove);
        var response = new MockHttpServletResponse();
        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var routes = mapper.readValue(getBinaryContent(response), new TypeReference<List<AlbumEntry>>() { });
        var updatedAlbum = routes.stream().filter(r -> r.getId() == 2).findFirst().get();
        assertThat(albumToMove.getSort()).isGreaterThan(updatedAlbum.getSort());
    }

    @Test
    void testMoveEntryDown() throws Exception {
        var albumToMove = AlbumEntry.with().id(2).parent(1).path("/moto/").album(true).title("Album has been updated").description("This is an updated description").sort(1).childcount(2).build();
        var movedAlbum = AlbumEntry.with().id(2).parent(1).path("/moto/").album(true).title("Album has been updated").description("This is an updated description").sort(2).childcount(2).build();
        var logservice = new MockLogService();
        var backendService = mock(OldAlbumService.class);
        when(backendService.moveEntryDown(any())).thenReturn(Arrays.asList(movedAlbum));
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice, useradmin);
        var request = buildPostUrl("/movealbumentrydown", albumToMove);
        var response = new MockHttpServletResponse();
        createSubjectAndBindItToThread();
        loginUser("admin", "admin");
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var routes = mapper.readValue(getBinaryContent(response), new TypeReference<List<AlbumEntry>>() { });
        var updatedAlbum = routes.stream().filter(r -> r.getId() == 2).findFirst().get();
        assertThat(albumToMove.getSort()).isLessThan(updatedAlbum.getSort());
    }

    @Test
    void testDownloadImage() throws Exception {
        var albumEntryId = 9;
        var imageUrl = "https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg";
        var lastModifiedDate = new Date();
        var entry = AlbumEntry.with().id(albumEntryId).album(false).path("/moto/places/grava1").imageUrl(imageUrl).lastModified(lastModifiedDate).build();
        var streamingOutput = new StreamingOutput() {

                @Override
                public void write(OutputStream output) throws IOException, WebApplicationException {
                    var inputStream = getClass().getClassLoader().getResourceAsStream("allroutes.json");
                    inputStream.transferTo(output);
                }
            };
        var backend = mock(OldAlbumService.class);
        when(backend.getAlbumEntry(anyInt())).thenReturn(Optional.of(entry));
        when(backend.downloadAlbumEntry(anyInt())).thenReturn(streamingOutput);
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backend, logservice, useradmin);
        var request = buildGetUrl("/image/download/" + albumEntryId);
        var response = new MockHttpServletResponse();

        servlet.service(request, response);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getContentType());
        var argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(backend).downloadAlbumEntry(argumentCaptor.capture());
        assertEquals(albumEntryId, argumentCaptor.getValue());
    }

    @Test
    void testDownloadImageWhenExceptionIsThrown() throws Exception {
        var albumEntryId = 9;
        var backend = mock(OldAlbumService.class);
        when(backend.downloadAlbumEntry(anyInt())).thenThrow(OldAlbumException.class);
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backend, logservice, useradmin);
        var request = buildGetUrl("/image/download/" + albumEntryId);
        var response = new MockHttpServletResponse();

        var originalNumberOfLogmessages = logservice.getLogmessages().size();
        servlet.service(request, response);

        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertEquals(MediaType.TEXT_PLAIN, response.getContentType());
        assertThat(logservice.getLogmessages()).hasSizeGreaterThan(originalNumberOfLogmessages);
    }

    @Test
    void testDownloadSelectedImages() throws Exception {
        var albumId = 4;
        var album = AlbumEntry.with().id(albumId).parent(2).album(true).path("/moto/vfr96/").title("My VFR750F in 1996").description("In may 1996, I bought a 1995 VFR750F, registered in october 1995, with 3400km on the clock when I bought it. This picture archive, contains pictures from my first (but hopefully not last) season, on a VFR.").build();
        var streamingOutput = new StreamingOutput() {

                @Override
                public void write(OutputStream output) throws IOException, WebApplicationException {
                    var inputStream = getClass().getClassLoader().getResourceAsStream("allroutes.json");
                    inputStream.transferTo(output);
                }
            };
        var backend = mock(OldAlbumService.class);
        when(backend.getAlbumEntry(albumId)).thenReturn(Optional.of(album));
        when(backend.downloadAlbumEntrySelection(anyList())).thenReturn(streamingOutput);
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backend, logservice, useradmin);
        var request = buildGetUrl("/image/downloadselection/" + albumId);
        request.setQueryString("id=9&id=10&id=12");
        var response = new MockHttpServletResponse();

        servlet.service(request, response);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getContentType());
        @SuppressWarnings("unchecked")
            ArgumentCaptor<List<Integer>> argumentCaptor = ArgumentCaptor.forClass((List.class));
        verify(backend).downloadAlbumEntrySelection(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).hasSize(3);
    }

    @Test
    void testGetMetadata() throws Exception {
        var logservice = new MockLogService();
        var backendService = mock(OldAlbumService.class);
        var mockMetadata = ImageMetadata.with()
            .status(200)
            .lastModified(new Date())
            .contentType("image/jpeg")
            .contentLength(128000)
            .build();
        when(backendService.readMetadata(anyString())).thenReturn(mockMetadata);
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice, useradmin);
        var url = "https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg";
        var request = buildPostUrl("/image/metadata", ImageRequest.with().url(url).build());
        var response = new MockHttpServletResponse();
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        var metadata = mapper.readValue(getBinaryContent(response), ImageMetadata.class);
        assertEquals(200, metadata.getStatus());
        assertThat(metadata.getLastModified()).isAfter(Date.from(Instant.EPOCH));
        assertEquals("image/jpeg", metadata.getContentType());
        assertThat(metadata.getContentLength()).isPositive();
    }

    @Test
    void testDefaultLocale() throws Exception {
        // Set up REST API servlet with mocked services
        var oldalbum = mock(OldAlbumService.class);
        when(oldalbum.defaultLocale()).thenReturn(NB_NO);
        var logservice = new MockLogService();

        var useradmin = mock(UserManagementService.class);
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(oldalbum, logservice, useradmin);

        // Create the request and response
        var request = buildGetUrl("/defaultlocale");
        var response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        var defaultLocale = mapper.readValue(response.getOutputStreamBinaryContent(), Locale.class);
        assertEquals(NB_NO, defaultLocale);
    }
    @Test
    void testAvailableLocales() throws Exception {
        // Set up REST API servlet with mocked services
        var oldalbum = mock(OldAlbumService.class);
        when(oldalbum.availableLocales()).thenReturn(Collections.singletonList(Locale.forLanguageTag("nb-NO")).stream().map(l -> LocaleBean.with().locale(l).build()).collect(Collectors.toList()));
        var logservice = new MockLogService();

        var useradmin = mock(UserManagementService.class);
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(oldalbum, logservice, useradmin);

        // Create the request and response
        var request = buildGetUrl("/availablelocales");
        var response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        var availableLocales = mapper.readValue(response.getOutputStreamBinaryContent(), new TypeReference<List<LocaleBean>>() {});
        assertThat(availableLocales).isNotEmpty().contains(LocaleBean.with().locale(Locale.forLanguageTag("nb-NO")).build());
    }

    @Test
    void testDisplayTexts() throws Exception {
        // Set up REST API servlet with mocked services
        var oldalbum = mock(OldAlbumService.class);
        var texts = new HashMap<String, String>();
        texts.put("date", "Dato");
        when(oldalbum.displayTexts(NB_NO)).thenReturn(texts);
        var logservice = new MockLogService();

        var useradmin = mock(UserManagementService.class);
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(oldalbum, logservice, useradmin);

        // Create the request and response
        var request = buildGetUrl("/displaytexts");
        request.setQueryString("locale=nb_NO");
        var response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getContentType());
        var displayTexts = mapper.readValue(response.getOutputStreamBinaryContent(), new TypeReference<Map<String, String>>() {});
        assertThat(displayTexts).isNotEmpty();
    }

    @Test
    void testDisplayTextsWithUnknownLocale() throws Exception {
        // Set up REST API servlet with mocked services
        var oldalbum = mock(OldAlbumService.class);
        var texts = new HashMap<String, String>();
        texts.put("date", "Dato");
        when(oldalbum.displayTexts(EN_UK)).thenThrow(MissingResourceException.class);
        var logservice = new MockLogService();

        var useradmin = mock(UserManagementService.class);
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(oldalbum, logservice, useradmin);

        // Create the request and response
        var request = buildGetUrl("/displaytexts");
        request.setQueryString("locale=en_UK");
        var response = new MockHttpServletResponse();

        // Run the method under test
        servlet.service(request, response);

        // Check the response
        assertEquals(500, response.getStatus());
        assertEquals("application/json", response.getContentType());
        var errorMessage = mapper.readValue(response.getOutputStreamBinaryContent(), ErrorMessage.class);
        assertEquals(500, errorMessage.getStatus());
        assertThat(errorMessage.getMessage()).startsWith("Unknown locale");
    }

    private HttpServletRequest buildPostUrl(String resource, Object body) throws Exception {
        var request = buildRequest(resource);
        request.setMethod("POST");
        request.setContentType(APPLICATION_JSON);
        request.setHeader("content-type", APPLICATION_JSON);
        request.setBodyContent(mapper.writeValueAsString(body));
        return request;
    }

    private MockHttpServletRequest buildGetUrl(String resource) {
        var request = buildRequest(resource);
        request.setMethod("GET");
        return request;
    }

    private MockHttpServletRequest buildRequest(String resource) {
        var session = new MockHttpSession();
        var request = new MockHttpServletRequest();
        request.setProtocol("HTTP/1.1");
        request.setRequestURL("http://localhost:8181/oldalbum/api" + resource);
        request.setRequestURI("/oldalbum/api" + resource);
        request.setContextPath("/oldalbum");
        request.setServletPath("/api");
        request.setSession(session);
        return request;
    }

    private OldAlbumWebApiServlet simulateDSComponentActivationAndWebWhiteboardConfiguration(OldAlbumService oldAlbumService, LogService logservice, UserManagementService useradmin) throws Exception {
        var servlet = new OldAlbumWebApiServlet();
        servlet.setLogService(logservice);
        servlet.setOldAlbumService(oldAlbumService);
        servlet.setUseradmin(useradmin);
        servlet.activate();
        var config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);
        return servlet;
    }

    private ServletConfig createServletConfigWithApplicationAndPackagenameForJerseyResources() {
        var config = mock(ServletConfig.class);
        when(config.getInitParameterNames()).thenReturn(Collections.enumeration(Arrays.asList(ServerProperties.PROVIDER_PACKAGES)));
        when(config.getInitParameter(ServerProperties.PROVIDER_PACKAGES)).thenReturn("no.priv.bang.oldalbum.web.api.resources");
        var servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("/authservice");
        when(config.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttributeNames()).thenReturn(Collections.emptyEnumeration());
        return config;
    }

    private byte[] getBinaryContent(MockHttpServletResponse response) throws Exception {
        var outputstream = (MockServletOutputStream) response.getOutputStream();
        return outputstream.getBinaryContent();
    }

    private static String loadClasspathResourceIntoString(String resource) {
        var resourceStream = OldAlbumWebApiServletTest.class.getClassLoader().getResourceAsStream(resource);
        var builder = new StringBuilder();
        try(var reader = new BufferedReader(new InputStreamReader(resourceStream, StandardCharsets.UTF_8))) {
            var c = 0;
            while ((c = reader.read()) != -1) {
                builder.append((char) c);
            }
        } catch (IOException e) {
            return "";
        }
        return builder.toString();
    }

}
