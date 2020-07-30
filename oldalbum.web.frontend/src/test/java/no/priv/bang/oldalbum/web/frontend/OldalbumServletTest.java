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
package no.priv.bang.oldalbum.web.frontend;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;

import com.mockrunner.mock.web.MockHttpServletResponse;

import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import static javax.servlet.http.HttpServletResponse.*;

public class OldalbumServletTest {

    @Test
    public void testGet() throws Exception {
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths()).thenReturn(Arrays.asList("/moto/"));
        MockLogService logservice = new MockLogService();
        OldalbumServlet servlet = new OldalbumServlet();
        ServletConfig servletConfig = mock(ServletConfig.class);
        when(servletConfig.getInitParameter("from")).thenReturn("to");
        servlet.init(servletConfig);
        servlet.setLogService(logservice);
        servlet.setOldalbumService(oldalbum);
        servlet.activate();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/oldalbum/moto/");
        when(request.getPathInfo()).thenReturn("/moto/");
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.service(request, response);

        assertEquals(SC_OK, response.getStatus());
        assertEquals("text/html", response.getContentType());
        assertThat(response.getBufferSize()).isPositive();
    }


    @Test
    public void testDoGetAddTrailingSlash() throws Exception {
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        MockLogService logservice = new MockLogService();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/oldalbum/useradmin"));
        when(request.getServletPath()).thenReturn("/frontend-karaf-demo");
        MockHttpServletResponse response = new MockHttpServletResponse();

        OldalbumServlet servlet = new OldalbumServlet();
        servlet.setLogService(logservice);
        servlet.setOldalbumService(oldalbum);
        servlet.activate();

        servlet.service(request, response);

        assertEquals(SC_FOUND, response.getStatus());
    }

    @Test
    public void testDoGetResponseThrowsIOException() throws Exception {
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths()).thenReturn(Arrays.asList("/moto/"));
        MockLogService logservice = new MockLogService();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/oldalbum/moto/");
        when(request.getPathInfo()).thenReturn("/moto/");
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);
        response.resetAll();
        ServletOutputStream streamThrowingIOException = mock(ServletOutputStream.class);
        doThrow(IOException.class).when(streamThrowingIOException).write(anyInt());
        when(response.getOutputStream()).thenReturn(streamThrowingIOException);

        OldalbumServlet servlet = new OldalbumServlet();
        servlet.setLogService(logservice);
        servlet.setOldalbumService(oldalbum);
        servlet.activate();

        servlet.service(request, response);

        assertEquals(SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDoGetResponseStreamMethodThrowsIOException() throws Exception {
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        MockLogService logservice = new MockLogService();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/oldalbum/useradmin/");
        when(request.getPathInfo()).thenReturn("/");
        MockHttpServletResponse response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);
        response.resetAll();
        when(response.getOutputStream()).thenThrow(IOException.class);

        OldalbumServlet servlet = new OldalbumServlet();
        servlet.setLogService(logservice);
        servlet.setOldalbumService(oldalbum);
        servlet.activate();

        servlet.service(request, response);

        assertEquals(SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test
    public void testDoGetResourceNotFound() throws Exception {
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        MockLogService logservice = new MockLogService();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/oldalbum/useradmin/static/nosuchname.png");
        when(request.getPathInfo()).thenReturn("/static/nosuchname.png");
        MockHttpServletResponse response = new MockHttpServletResponse();

        OldalbumServlet servlet = new OldalbumServlet();
        servlet.setLogService(logservice);
        servlet.setOldalbumService(oldalbum);
        servlet.activate();

        servlet.service(request, response);

        assertEquals(SC_NOT_FOUND, response.getErrorCode());
    }

}
