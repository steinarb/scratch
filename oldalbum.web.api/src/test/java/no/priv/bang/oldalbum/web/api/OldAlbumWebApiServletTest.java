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
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
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

    @BeforeAll
    static void beforeAllTests() throws Exception {
        allroutes = mapper.readValue(OldAlbumWebApiServletTest.class.getClassLoader().getResourceAsStream("allroutes.json"), new TypeReference<List<AlbumEntry>>() {});
    }

    @Test
    void testFetchRoutes() throws Exception {
        MockLogService logservice = new MockLogService();
        OldAlbumService backendService = mock(OldAlbumService.class);
        when(backendService.fetchRoutes()).thenReturn(allroutes);
        OldAlbumWebApiServlet servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(backendService, logservice);
        HttpServletRequest request = buildGetUrl("/fetchroutes");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        List<AlbumEntry> routes = mapper.readValue(getBinaryContent(response), new TypeReference<List<AlbumEntry>>() { });
        assertThat(routes.size()).isPositive();
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

}
