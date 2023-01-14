/*
 * Copyright 2020-2023 Steinar Bang
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

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;

import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.Ini;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.subject.WebSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static javax.servlet.http.HttpServletResponse.*;

class OldalbumServletTest {

    private static WebSecurityManager securitymanager;
    private WebSubject subject;

    @BeforeEach
    void setup() {
        subject = createSubjectAndBindItToThread();
    }

    @Test
    void testGetAlbum() throws Exception {
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths(false)).thenReturn(Arrays.asList("/moto/"));
        AlbumEntry entry = AlbumEntry.with().id(2).parent(1).path("/moto/places/").album(true).title("Motorcyle meeting places").description("Places motorcylists meet").sort(1).childcount(4).build();
        when(oldalbum.getAlbumEntryFromPath(anyString())).thenReturn(entry);
        AlbumEntry grava1 = AlbumEntry.with().id(3).parent(2).path("/moto/places/grava1").album(false).title("Tyrigrava").description("On gamle Mossevei").imageUrl("https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg").thumbnailUrl("https://www.bang.priv.no/sb/pics/moto/places/icons/grava1.gif").sort(1).lastModified(new Date()).contentType("image/jpeg").contentLength(71072).build();
        AlbumEntry grava2 = AlbumEntry.with().id(4).parent(2).path("/moto/places/grava2").album(false).title("Tyrigrava south view").description("View from the south").imageUrl("https://www.bang.priv.no/sb/pics/moto/places/grava2.jpg").thumbnailUrl("https://www.bang.priv.no/sb/pics/moto/places/icons/grava2.gif").sort(2).lastModified(new Date()).contentType("image/jpeg").contentLength(71072).build();
        AlbumEntry grava3 = AlbumEntry.with().id(5).parent(2).path("/moto/places/grava3").album(false).title("Tyrigrava Wednesday").description("Huge number of motorcyles on Wednesdays").imageUrl("https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg").thumbnailUrl("https://www.bang.priv.no/sb/pics/moto/places/icons/grava3.gif").sort(3).lastModified(new Date()).contentType("image/jpeg").contentLength(71072).build();
        AlbumEntry hove1 = AlbumEntry.with().id(6).parent(2).path("/moto/places/hove1").album(false).title("Hove fjellgaard").description("Meeting place in Ål in Hallingdal").imageUrl("https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg").thumbnailUrl("https://www.bang.priv.no/sb/pics/moto/places/icons/grava3.gif").sort(4).lastModified(new Date()).contentType("image/jpeg").contentLength(71072).build();
        AlbumEntry album1 = AlbumEntry.with().id(7).parent(2).path("/moto/places/album1").album(true).title("Sub album").description("In another album resides other pictures").sort(5).childcount(0).build();
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
        assertThat(response.getOutputStreamContent()).contains("twitter:card");
        assertThat(response.getOutputStreamContent()).contains("twitter:title");
        assertThat(response.getOutputStreamContent()).contains("og:description");
        assertThat(response.getOutputStreamContent()).contains("twitter:description");
        assertThat(response.getOutputStreamContent()).contains("og:image");
        assertThat(response.getOutputStreamContent()).doesNotContain("twitter:image");
    }

    @Test
    void testGetPicture() throws Exception {
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths(false)).thenReturn(Arrays.asList("/moto/"));
        AlbumEntry entry = AlbumEntry.with()
            .id(3)
            .parent(2)
            .path("/moto/places/grava1")
            .album(false)
            .title("Tyrigrava")
            .description("On gamle Mossevei")
            .imageUrl("https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg")
            .thumbnailUrl("https://www.bang.priv.no/sb/pics/moto/places/icons/grava1.gif")
            .sort(1)
            .lastModified(new Date())
            .contentType("image/jpeg")
            .contentLength(71072)
            .build();
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
        assertThat(response.getOutputStreamContent()).contains("twitter:card");
        assertThat(response.getOutputStreamContent()).contains("twitter:title");
        assertThat(response.getOutputStreamContent()).contains("og:description");
        assertThat(response.getOutputStreamContent()).contains("twitter:description");
        assertThat(response.getOutputStreamContent()).contains("og:image");
        assertThat(response.getOutputStreamContent()).contains("twitter:image");
    }

    @Test
    void testGetEmptyAlbum() throws Exception {
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths(false)).thenReturn(Arrays.asList("/moto/"));
        AlbumEntry entry = AlbumEntry.with().id(2).parent(1).path("/moto/places/").album(true).sort(1).childcount(4).build();
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
        assertThat(response.getOutputStreamContent()).contains("twitter:card");
        assertThat(response.getOutputStreamContent()).doesNotContain("twitter:title");
        assertThat(response.getOutputStreamContent()).doesNotContain("og:description");
        assertThat(response.getOutputStreamContent()).doesNotContain("twitter:description");
        assertThat(response.getOutputStreamContent()).doesNotContain("og:image");
        assertThat(response.getOutputStreamContent()).doesNotContain("twitter:image");
    }

    @Test
    void testGetAlbumWithEmptyValues() throws Exception {
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths(false)).thenReturn(Arrays.asList("/moto/"));
        AlbumEntry entry = AlbumEntry.with().id(2).parent(1).path("/moto/places/").album(true).title("").description("").imageUrl("").thumbnailUrl("").sort(1).childcount(4).build();
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
    void testGetNullAlbumEntry() throws Exception {
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths(false)).thenReturn(Arrays.asList("/moto/"));
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
    void testGetNotFound() throws Exception {
        var oldalbum = mock(OldAlbumService.class);
        var request = new MockHttpServletRequest();
        request.setRequestURL("http://localhost:8181/someapp");
        request.setPathInfo("/notfound.html");
        var response = new MockHttpServletResponse();
        var logservice = new MockLogService();
        var servlet = new OldalbumServlet();
        servlet.setOldalbumService(oldalbum);
        servlet.setLogService(logservice);
        servlet.activate();
        servlet.setLogService(logservice);

        servlet.service(request, response);
        assertEquals(SC_NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDoGetAddTrailingSlash() throws Exception {
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
    void testDoGetResponseThrowsIOException() throws Exception {
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

    @Test
    void testDoGetResponseStreamMethodThrowsIOException() throws Exception {
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
    void testDoGetResourceNotFound() throws Exception {
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

        assertEquals(SC_NOT_FOUND, response.getStatusCode());
        assertEquals("text/html", response.getContentType());
        assertThat(response.getOutputStreamContent()).contains("bundle.js");
    }

    @Test
    void testDoGetRoutesWhenNotLoggedIn() throws Exception {
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths(false)).thenReturn(Arrays.asList("/path1", "/path2"));
        when(oldalbum.getPaths(true)).thenReturn(Arrays.asList("/path1", "/path2", "/path3"));
        MockLogService logservice = new MockLogService();

        var servlet = new OldalbumServlet();
        servlet.setLogService(logservice);
        servlet.setOldalbumService(oldalbum);
        servlet.activate();

        var routes = servlet.getRoutes();

        assertThat(routes).hasSize(6 + 2);
    }

    @Test
    void testDoGetRoutesWhenLoggedIn() throws Exception {
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths(false)).thenReturn(Arrays.asList("/path1", "/path2"));
        when(oldalbum.getPaths(true)).thenReturn(Arrays.asList("/path1", "/path2", "/path3"));
        MockLogService logservice = new MockLogService();

        var servlet = new OldalbumServlet();
        servlet.setLogService(logservice);
        servlet.setOldalbumService(oldalbum);
        servlet.activate();

        loginUser("jad", "1ad");

        var routes = servlet.getRoutes();

        assertThat(routes).hasSize(6 + 3);
    }

    protected void loginUser(String username, String password) {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray(), true);
        subject.login(token);
    }

    protected WebSubject createSubjectAndBindItToThread() {
        return createSubjectAndBindItToThread(getSecurityManager());
    }

    protected WebSubject createSubjectAndBindItToThread(WebSecurityManager webSecurityManager) {
        HttpSession session = mock(HttpSession.class);
        MockHttpServletRequest dummyrequest = new MockHttpServletRequest();
        dummyrequest.setSession(session);
        MockHttpServletResponse dummyresponse = new MockHttpServletResponse();
        return createSubjectAndBindItToThread(webSecurityManager, dummyrequest, dummyresponse);
    }

    protected WebSubject createSubjectAndBindItToThread(WebSecurityManager webSecurityManager, HttpServletRequest request, HttpServletResponse response) {
        WebSubject subject = new WebSubject.Builder(webSecurityManager, request, response).buildWebSubject();
        ThreadContext.bind(subject);
        return subject;
    }

    public static WebSecurityManager getSecurityManager() {
        if (securitymanager == null) {
            IniWebEnvironment environment = new IniWebEnvironment();
            environment.setIni(Ini.fromResourcePath("classpath:test.shiro.ini"));
            environment.init();
            securitymanager = environment.getWebSecurityManager();
        }

        return securitymanager;
    }

}
