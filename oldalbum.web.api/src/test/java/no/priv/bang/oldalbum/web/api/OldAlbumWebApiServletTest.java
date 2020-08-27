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
package no.priv.bang.oldalbum.web.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static javax.ws.rs.core.MediaType.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.glassfish.jersey.server.ServerProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.osgi.service.log.LogService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletOutputStream;

import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class OldAlbumWebApiServletTest {
    final static ObjectMapper mapper = new ObjectMapper();
    private static List<AlbumEntry> allroutes;
    static String dumpedroutes = loadClasspathResourceIntoString("dumproutes.sql");

    @BeforeAll
    static void beforeAllTests() throws Exception {
        allroutes = mapper.readValue(OldAlbumWebApiServletTest.class.getClassLoader().getResourceAsStream("allroutes.json"), new TypeReference<List<AlbumEntry>>() {});
    }

    @Test
    void testFetchRoutes() throws Exception {
        MockLogService logservice = new MockLogService();
        OldAlbumService backendService = mock(OldAlbumService.class);
        when(backendService.fetchAllRoutes()).thenReturn(allroutes);
        OldAlbumWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice);
        HttpServletRequest request = buildGetUrl("/allroutes");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        List<AlbumEntry> routes = mapper.readValue(getBinaryContent(response), new TypeReference<List<AlbumEntry>>() { });
        assertThat(routes.size()).isPositive();
    }

    @Test
    void testDumpRoutesSql() throws Exception {
        MockLogService logservice = new MockLogService();
        OldAlbumService backendService = mock(OldAlbumService.class);
        when(backendService.dumpDatabaseSql()).thenReturn(dumpedroutes);
        OldAlbumWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice);
        HttpServletRequest request = buildGetUrl("/dumproutessql");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals("application/sql", response.getContentType());
        String sql = response.getOutputStreamContent();
        assertThat(sql).contains("--liquibase formatted sql");
    }

    @Test
    void testModifyalbum() throws Exception {
        AlbumEntry modifiedAlbum = new AlbumEntry(2, 1, "/moto/", true, "Album has been updated", "This is an updated description", null, null, 1, null, null, 0, 2);
        MockLogService logservice = new MockLogService();
        OldAlbumService backendService = mock(OldAlbumService.class);
        when(backendService.updateEntry(any())).thenReturn(Arrays.asList(modifiedAlbum));
        OldAlbumWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice);
        HttpServletRequest request = buildPostUrl("/modifyalbum", modifiedAlbum);
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        List<AlbumEntry> routes = mapper.readValue(getBinaryContent(response), new TypeReference<List<AlbumEntry>>() { });
        assertThat(routes.size()).isPositive();
    }

    @Test
    void testAddAlbum() throws Exception {
        AlbumEntry albumToAdd = new AlbumEntry(0, 1, "/newalbum/", true, "A new album", "A new album for new pictures", null, null, 2, null, null, 0, 0);
        MockLogService logservice = new MockLogService();
        OldAlbumService backendService = mock(OldAlbumService.class);
        when(backendService.addEntry(any())).thenReturn(Arrays.asList(albumToAdd));
        OldAlbumWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice);
        HttpServletRequest request = buildPostUrl("/addalbum", albumToAdd);
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        List<AlbumEntry> routes = mapper.readValue(getBinaryContent(response), new TypeReference<List<AlbumEntry>>() { });
        assertThat(routes.size()).isPositive();
    }

    @Test
    void testAddPicture() throws Exception {
        AlbumEntry pictureToAdd = new AlbumEntry(0, 1, "/newalbum/", true, "A new album", "A new album for new pictures", null, null, 2, null, null, 0, 0);
        MockLogService logservice = new MockLogService();
        OldAlbumService backendService = mock(OldAlbumService.class);
        when(backendService.addEntry(any())).thenReturn(Arrays.asList(pictureToAdd));
        OldAlbumWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice);
        HttpServletRequest request = buildPostUrl("/addpicture", pictureToAdd);
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        List<AlbumEntry> routes = mapper.readValue(getBinaryContent(response), new TypeReference<List<AlbumEntry>>() { });
        assertThat(routes.size()).isPositive();
    }

    @Test
    void testDeleteEntry() throws Exception {
        AlbumEntry pictureToDelete = new AlbumEntry(7, 3, "/oldalbum/moto/places/grava3", false, "", "Tyrigrava, view from the north. Lotsa bikes here too", "https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg", "https://www.bang.priv.no/sb/pics/moto/places/icons/grava3.gif", 3, new Date(), "image/jpeg", 71072, 0);
        MockLogService logservice = new MockLogService();
        OldAlbumService backendService = mock(OldAlbumService.class);
        OldAlbumWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice);
        HttpServletRequest request = buildPostUrl("/deleteentry", pictureToDelete);
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        List<AlbumEntry> routes = mapper.readValue(getBinaryContent(response), new TypeReference<List<AlbumEntry>>() { });
        assertEquals(0, routes.size());
    }

    @Test
    void testMoveEntryUp() throws Exception {
        AlbumEntry albumToMove = new AlbumEntry(2, 1, "/moto/", true, "Album has been updated", "This is an updated description", null, null, 2, null, null, 0, 2);
        AlbumEntry movedAlbum = new AlbumEntry(2, 1, "/moto/", true, "Album has been updated", "This is an updated description", null, null, 1, null, null, 0, 2);
        MockLogService logservice = new MockLogService();
        OldAlbumService backendService = mock(OldAlbumService.class);
        when(backendService.moveEntryUp(any())).thenReturn(Arrays.asList(movedAlbum));
        OldAlbumWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice);
        HttpServletRequest request = buildPostUrl("/movealbumentryup", albumToMove);
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        List<AlbumEntry> routes = mapper.readValue(getBinaryContent(response), new TypeReference<List<AlbumEntry>>() { });
        AlbumEntry updatedAlbum = routes.stream().filter(r -> r.getId() == 2).findFirst().get();
        assertThat(albumToMove.getSort()).isGreaterThan(updatedAlbum.getSort());
    }

    @Test
    void testMoveEntryDown() throws Exception {
        AlbumEntry albumToMove = new AlbumEntry(2, 1, "/moto/", true, "Album has been updated", "This is an updated description", null, null, 1, null, null, 0, 2);
        AlbumEntry movedAlbum = new AlbumEntry(2, 1, "/moto/", true, "Album has been updated", "This is an updated description", null, null, 2, null, null, 0, 2);
        MockLogService logservice = new MockLogService();
        OldAlbumService backendService = mock(OldAlbumService.class);
        when(backendService.moveEntryDown(any())).thenReturn(Arrays.asList(movedAlbum));
        OldAlbumWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice);
        HttpServletRequest request = buildPostUrl("/movealbumentrydown", albumToMove);
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        List<AlbumEntry> routes = mapper.readValue(getBinaryContent(response), new TypeReference<List<AlbumEntry>>() { });
        AlbumEntry updatedAlbum = routes.stream().filter(r -> r.getId() == 2).findFirst().get();
        assertThat(albumToMove.getSort()).isLessThan(updatedAlbum.getSort());
    }

    private HttpServletRequest buildPostUrl(String resource, Object body) throws Exception {
        MockHttpServletRequest request = buildRequest(resource);
        request.setMethod("POST");
        request.setContentType(APPLICATION_JSON);
        request.setHeader("content-type", APPLICATION_JSON);
        request.setBodyContent(mapper.writeValueAsString(body));
        return request;
    }

    private HttpServletRequest buildGetUrl(String resource) {
        MockHttpServletRequest request = buildRequest(resource);
        request.setMethod("GET");
        return request;
    }

    private MockHttpServletRequest buildRequest(String resource) {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setProtocol("HTTP/1.1");
        request.setRequestURL("http://localhost:8181/oldalbum/api" + resource);
        request.setRequestURI("/oldalbum/api" + resource);
        request.setContextPath("/oldalbum");
        request.setServletPath("/api");
        request.setSession(session);
        return request;
    }

    private OldAlbumWebApiServlet simulateDSComponentActivationAndWebWhiteboardConfiguration(OldAlbumService oldAlbumService, LogService logservice) throws Exception {
        OldAlbumWebApiServlet servlet = new OldAlbumWebApiServlet();
        servlet.setLogService(logservice);
        servlet.setOldAlbumService(oldAlbumService);
        servlet.activate();
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);
        return servlet;
    }

    private ServletConfig createServletConfigWithApplicationAndPackagenameForJerseyResources() {
        ServletConfig config = mock(ServletConfig.class);
        when(config.getInitParameterNames()).thenReturn(Collections.enumeration(Arrays.asList(ServerProperties.PROVIDER_PACKAGES)));
        when(config.getInitParameter(eq(ServerProperties.PROVIDER_PACKAGES))).thenReturn("no.priv.bang.oldalbum.web.api.resources");
        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("/authservice");
        when(config.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttributeNames()).thenReturn(Collections.emptyEnumeration());
        return config;
    }

    private byte[] getBinaryContent(MockHttpServletResponse response) throws Exception {
        MockServletOutputStream outputstream = (MockServletOutputStream) response.getOutputStream();
        return outputstream.getBinaryContent();
    }

    private static String loadClasspathResourceIntoString(String resource) {
        InputStream resourceStream = OldAlbumWebApiServletTest.class.getClassLoader().getResourceAsStream(resource);
        StringBuilder builder = new StringBuilder();
        try(Reader reader = new BufferedReader(new InputStreamReader(resourceStream, StandardCharsets.UTF_8))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                builder.append((char) c);
            }
        } catch (IOException e) {
            return "";
        }
        return builder.toString();
    }

}
