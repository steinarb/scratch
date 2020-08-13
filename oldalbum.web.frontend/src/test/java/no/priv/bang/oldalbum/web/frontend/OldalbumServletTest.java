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
import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import static javax.servlet.http.HttpServletResponse.*;

public class OldalbumServletTest {

    @Test
    public void testGetAlbum() throws Exception {
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths()).thenReturn(Arrays.asList("/moto/"));
        AlbumEntry entry = new AlbumEntry(2, 1, "/moto/places/", true, "Motorcyle meeting places", "Places motorcylists meet", null, null, 1);
        when(oldalbum.getAlbumEntryFromPath(anyString())).thenReturn(entry);
        AlbumEntry grava1 = new AlbumEntry(3, 2, "/moto/places/grava1", false, "Tyrigrava", "On gamle Mossevei", "https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg", "https://www.bang.priv.no/sb/pics/moto/places/icons/grava1.gif", 1);
        AlbumEntry grava2 = new AlbumEntry(4, 2, "/moto/places/grava2", false, "Tyrigrava south view", "View from the south", "https://www.bang.priv.no/sb/pics/moto/places/grava2.jpg", "https://www.bang.priv.no/sb/pics/moto/places/icons/grava2.gif", 2);
        AlbumEntry grava3 = new AlbumEntry(5, 2, "/moto/places/grava3", false, "Tyrigrava Wednesday", "Huge number of motorcyles on Wednesdays", "https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg", "https://www.bang.priv.no/sb/pics/moto/places/icons/grava3.gif", 3);
        AlbumEntry hove1 = new AlbumEntry(6, 2, "/moto/places/hove1", false, "Hove fjellgaard", "Meeting place in Ål in Hallingdal", "https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg", "https://www.bang.priv.no/sb/pics/moto/places/icons/grava3.gif", 4);
        AlbumEntry album1 = new AlbumEntry(7, 2, "/moto/places/album1", true, "Sub album", "In another album resides other pictures", null, null, 5);
        List<AlbumEntry> children = Arrays.asList(grava1, grava2, grava3, album1, hove1);
        when(oldalbum.getChildren(anyInt())).thenReturn(children);
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
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/oldalbum/moto/"));
        when(request.getPathInfo()).thenReturn("/moto/");
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.service(request, response);

        assertEquals(SC_OK, response.getStatus());
        assertEquals("text/html", response.getContentType());
        assertThat(response.getBufferSize()).isPositive();
        assertThat(response.getOutputStreamContent()).contains("og:url");
        assertThat(response.getOutputStreamContent()).contains("og:title");
        assertThat(response.getOutputStreamContent()).contains("twitter:title");
        assertThat(response.getOutputStreamContent()).contains("og:description");
        assertThat(response.getOutputStreamContent()).contains("twitter:description");
        assertThat(response.getOutputStreamContent()).contains("og:image");
        assertThat(response.getOutputStreamContent()).doesNotContain("twitter:image");
    }

    @Test
    public void testGetPicture() throws Exception {
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths()).thenReturn(Arrays.asList("/moto/"));
        AlbumEntry entry = new AlbumEntry(3, 2, "/moto/places/grava1", false, "Tyrigrava", "On gamle Mossevei", "https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg", "https://www.bang.priv.no/sb/pics/moto/places/icons/grava1.gif", 1);
        when(oldalbum.getAlbumEntryFromPath(anyString())).thenReturn(entry);
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
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/oldalbum/moto/"));
        when(request.getPathInfo()).thenReturn("/moto/");
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.service(request, response);

        assertEquals(SC_OK, response.getStatus());
        assertEquals("text/html", response.getContentType());
        assertThat(response.getBufferSize()).isPositive();
        assertThat(response.getOutputStreamContent()).contains("og:url");
        assertThat(response.getOutputStreamContent()).contains("og:title");
        assertThat(response.getOutputStreamContent()).contains("twitter:title");
        assertThat(response.getOutputStreamContent()).contains("og:description");
        assertThat(response.getOutputStreamContent()).contains("twitter:description");
        assertThat(response.getOutputStreamContent()).contains("og:image");
        assertThat(response.getOutputStreamContent()).contains("twitter:image");
    }

    @Test
    public void testGetEmptyAlbum() throws Exception {
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths()).thenReturn(Arrays.asList("/moto/"));
        AlbumEntry entry = new AlbumEntry(2, 1, "/moto/places/", true, null, null, null, null, 1);
        when(oldalbum.getAlbumEntryFromPath(anyString())).thenReturn(entry);
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
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/oldalbum/moto/"));
        when(request.getPathInfo()).thenReturn("/moto/");
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.service(request, response);

        assertEquals(SC_OK, response.getStatus());
        assertEquals("text/html", response.getContentType());
        assertThat(response.getBufferSize()).isPositive();
        assertThat(response.getOutputStreamContent()).contains("og:url");
        assertThat(response.getOutputStreamContent()).doesNotContain("og:title");
        assertThat(response.getOutputStreamContent()).doesNotContain("twitter:title");
        assertThat(response.getOutputStreamContent()).doesNotContain("og:description");
        assertThat(response.getOutputStreamContent()).doesNotContain("twitter:description");
        assertThat(response.getOutputStreamContent()).doesNotContain("og:image");
        assertThat(response.getOutputStreamContent()).doesNotContain("twitter:image");
    }

    @Test
    public void testGetAlbumWithEmptyValues() throws Exception {
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths()).thenReturn(Arrays.asList("/moto/"));
        AlbumEntry entry = new AlbumEntry(2, 1, "/moto/places/", true, "", "", "", "", 1);
        when(oldalbum.getAlbumEntryFromPath(anyString())).thenReturn(entry);
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
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/oldalbum/moto/"));
        when(request.getPathInfo()).thenReturn("/moto/");
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.service(request, response);

        assertEquals(SC_OK, response.getStatus());
        assertEquals("text/html", response.getContentType());
        assertThat(response.getBufferSize()).isPositive();
        assertThat(response.getOutputStreamContent()).contains("og:url");
        assertThat(response.getOutputStreamContent()).doesNotContain("og:title");
        assertThat(response.getOutputStreamContent()).doesNotContain("twitter:title");
        assertThat(response.getOutputStreamContent()).doesNotContain("og:description");
        assertThat(response.getOutputStreamContent()).doesNotContain("twitter:description");
        assertThat(response.getOutputStreamContent()).doesNotContain("og:image");
        assertThat(response.getOutputStreamContent()).doesNotContain("twitter:image");
    }

    @Test
    public void testGetNullAlbumEntry() throws Exception {
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
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/oldalbum/moto/"));
        when(request.getPathInfo()).thenReturn("/moto/");
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.service(request, response);

        assertEquals(SC_OK, response.getStatus());
        assertEquals("text/html", response.getContentType());
        assertThat(response.getBufferSize()).isPositive();
        assertThat(response.getOutputStreamContent()).contains("og:url");
        assertThat(response.getOutputStreamContent()).doesNotContain("og:title");
        assertThat(response.getOutputStreamContent()).doesNotContain("twitter:title");
        assertThat(response.getOutputStreamContent()).doesNotContain("og:description");
        assertThat(response.getOutputStreamContent()).doesNotContain("twitter:description");
        assertThat(response.getOutputStreamContent()).doesNotContain("og:image");
        assertThat(response.getOutputStreamContent()).doesNotContain("twitter:image");
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
        MockLogService logservice = new MockLogService();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/oldalbum/favicon.ico");
        when(request.getPathInfo()).thenReturn("favicon.ico");
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
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/oldalbum/useradmin/"));
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
