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
package no.priv.bang.oldalbum.web.frontend;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;

import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
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

import static java.lang.String.format;
import static java.util.Optional.of;
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
        var oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths(false)).thenReturn(Arrays.asList("/moto/"));
        var entry = AlbumEntry.with().id(2).parent(1).path("/moto/places/").album(true).title("Motorcyle meeting places").description("Places motorcylists meet").sort(1).childcount(4).build();
        when(oldalbum.getAlbumEntryFromPath(anyString())).thenReturn(entry);
        var album = AlbumEntry.with().id(1).parent(1).path("/moto/").album(true).title("Motorcyle pictures").description("Two wheeled wonders").sort(1).childcount(4).build();
        when(oldalbum.getAlbumEntry(anyInt())).thenReturn(of(album));
        var prevEntry = AlbumEntry.with().id(4).parent(1).path("/moto/vfr96/").album(true).title("My VFR750F in 1996").description("In may 1996, I bought a 1995 VFR750F, registered in october 1995").sort(1).childcount(13).build();
        when(oldalbum.getPreviousAlbumEntry(anyInt())).thenReturn(of(prevEntry));
        var nextEntry = AlbumEntry.with().id(14).parent(1).path("/moto/vfr96/").album(true).title("FJ 1100").description("The bike in question is a 1986 Yamaha FJ 1100, with 52000km on the clock").sort(1).childcount(5).build();
        when(oldalbum.getNextAlbumEntry(anyInt())).thenReturn(of(nextEntry));
        var grava1 = AlbumEntry.with().id(3).parent(2).path("/moto/places/grava1").album(false).title("Tyrigrava").description("On gamle Mossevei").imageUrl("https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg").thumbnailUrl("https://www.bang.priv.no/sb/pics/moto/places/icons/grava1.gif").sort(1).lastModified(new Date()).contentType("image/jpeg").contentLength(71072).build();
        var grava2 = AlbumEntry.with().id(4).parent(2).path("/moto/places/grava2").album(false).title("Tyrigrava south view").description("View from the south").imageUrl("https://www.bang.priv.no/sb/pics/moto/places/grava2.jpg").thumbnailUrl("https://www.bang.priv.no/sb/pics/moto/places/icons/grava2.gif").sort(2).lastModified(new Date()).contentType("image/jpeg").contentLength(71072).build();
        var grava3 = AlbumEntry.with().id(5).parent(2).path("/moto/places/grava3").album(false).title("Tyrigrava Wednesday").description("Huge number of motorcyles on Wednesdays").imageUrl("https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg").thumbnailUrl("https://www.bang.priv.no/sb/pics/moto/places/icons/grava3.gif").sort(3).lastModified(new Date()).contentType("image/jpeg").contentLength(71072).build();
        var hove1 = AlbumEntry.with().id(6).parent(2).path("/moto/places/hove1").album(false).title("Hove fjellgaard").description("Meeting place in Ål in Hallingdal").imageUrl("https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg").thumbnailUrl("https://www.bang.priv.no/sb/pics/moto/places/icons/grava3.gif").sort(4).lastModified(new Date()).contentType("image/jpeg").contentLength(71072).build();
        var album1 = AlbumEntry.with().id(7).parent(2).path("/moto/places/album1").album(true).title("Sub album").description("In another album resides other pictures").sort(5).childcount(0).build();
        var children = Arrays.asList(grava1, grava2, grava3, album1, hove1);
        when(oldalbum.getChildren(anyInt())).thenReturn(children);
        var logservice = new MockLogService();
        var servlet = new OldalbumServlet();
        var servletConfig = mock(ServletConfig.class);
        when(servletConfig.getInitParameter("from")).thenReturn("to");
        servlet.init(servletConfig);
        servlet.setLogService(logservice);
        servlet.setOldalbumService(oldalbum);
        servlet.activate();
        var request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/oldalbum/moto/places/");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/oldalbum/moto/places/"));
        when(request.getPathInfo()).thenReturn("/moto/");
        var response = new MockHttpServletResponse();

        servlet.service(request, response);

        assertEquals(SC_OK, response.getStatus());
        assertEquals("text/html", response.getContentType());
        assertThat(response.getBufferSize()).isPositive();
        assertThat(response.getOutputStreamContent())
            .contains("og:url")
            .contains("og:title")
            .contains("twitter:card")
            .contains("twitter:title")
            .contains("og:description")
            .contains("twitter:description")
            .contains("og:image")
            .doesNotContain("twitter:image")
            .contains("<h1>" + entry.title())
            .contains("<p><em>" + entry.description())
            .contains("href=\"/oldalbum" + album.path())
            .contains("href=\"/oldalbum" + prevEntry.path())
            .contains("href=\"/oldalbum" + nextEntry.path());
        for (var child : children) {
            assertThat(response.getOutputStreamContent()).contains("href=\"/oldalbum" + child.path());
        }
    }

    @Test
    void testGetPicture() throws Exception {
        var oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths(false)).thenReturn(Arrays.asList("/moto/"));
        var entry = AlbumEntry.with()
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
        var album = AlbumEntry.with().id(2).parent(1).path("/moto/places/").album(true).title("Motorcyle meeting places").description("Places motorcylists meet").sort(1).childcount(4).build();
        when(oldalbum.getAlbumEntry(anyInt())).thenReturn(of(album));
        var prevEntry = AlbumEntry.with().id(4).parent(2).path("/moto/places/grava2").album(false).title("Tyrigrava").description("On gamle Mossevei").imageUrl("https://www.bang.priv.no/sb/pics/moto/places/grava2.jpg").thumbnailUrl("https://www.bang.priv.no/sb/pics/moto/places/icons/grava2.gif").sort(2).lastModified(new Date()).contentType("image/jpeg").contentLength(71072).build();
        when(oldalbum.getPreviousAlbumEntry(anyInt())).thenReturn(of(prevEntry));
        var nextEntry = AlbumEntry.with().id(5).parent(2).path("/moto/places/grava3").album(false).title("Tyrigrava").description("On gamle Mossevei").imageUrl("https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg").thumbnailUrl("https://www.bang.priv.no/sb/pics/moto/places/icons/grava3.gif").sort(2).lastModified(new Date()).contentType("image/jpeg").contentLength(71072).build();
        when(oldalbum.getNextAlbumEntry(anyInt())).thenReturn(of(nextEntry));
        var logservice = new MockLogService();
        var servlet = new OldalbumServlet();
        var servletConfig = mock(ServletConfig.class);
        when(servletConfig.getInitParameter("from")).thenReturn("to");
        servlet.init(servletConfig);
        servlet.setLogService(logservice);
        servlet.setOldalbumService(oldalbum);
        servlet.activate();
        var request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/oldalbum/moto/places/grava1");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/oldalbum/moto/places/grava1"));
        when(request.getPathInfo()).thenReturn("/moto/");
        var response = new MockHttpServletResponse();

        servlet.service(request, response);

        assertEquals(SC_OK, response.getStatus());
        assertEquals("text/html", response.getContentType());
        assertThat(response.getBufferSize()).isPositive();
        assertThat(response.getOutputStreamContent())
            .contains("og:url")
            .contains("og:title")
            .contains("twitter:card")
            .contains("twitter:title")
            .contains("og:description")
            .contains("twitter:description")
            .contains("og:image")
            .contains("twitter:image")
            .contains("<h1>" + entry.title())
            .contains("href=\"/oldalbum" + album.path())
            .contains("href=\"/oldalbum" + prevEntry.path())
            .contains("href=\"/oldalbum" + nextEntry.path())
            .contains("src=\"" + entry.imageUrl())
            .contains("<p><em>" + entry.description());
    }

    @Test
    void testGetEmptyAlbum() throws Exception {
        var oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths(false)).thenReturn(Arrays.asList("/moto/"));
        var entry = AlbumEntry.with().id(2).parent(1).path("/moto/places/").album(true).sort(1).childcount(4).build();
        when(oldalbum.getAlbumEntryFromPath(anyString())).thenReturn(entry);
        var logservice = new MockLogService();
        var servlet = new OldalbumServlet();
        var servletConfig = mock(ServletConfig.class);
        when(servletConfig.getInitParameter("from")).thenReturn("to");
        servlet.init(servletConfig);
        servlet.setLogService(logservice);
        servlet.setOldalbumService(oldalbum);
        servlet.activate();
        var request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/oldalbum/moto/");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/oldalbum/moto/"));
        when(request.getPathInfo()).thenReturn("/moto/");
        var response = new MockHttpServletResponse();

        servlet.service(request, response);

        assertEquals(SC_OK, response.getStatus());
        assertEquals("text/html", response.getContentType());
        assertThat(response.getBufferSize()).isPositive();
        assertThat(response.getOutputStreamContent())
            .contains("og:url")
            .doesNotContain("og:title")
            .contains("twitter:card")
            .doesNotContain("twitter:title")
            .doesNotContain("og:description")
            .doesNotContain("twitter:description")
            .doesNotContain("og:image")
            .doesNotContain("twitter:image");
    }

    @Test
    void testGetAlbumWithEmptyValues() throws Exception {
        var oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths(false)).thenReturn(Arrays.asList("/moto/"));
        var entry = AlbumEntry.with().id(2).parent(1).path("/moto/places/").album(true).title("").description("").imageUrl("").thumbnailUrl("").sort(1).childcount(4).build();
        when(oldalbum.getAlbumEntryFromPath(anyString())).thenReturn(entry);
        var logservice = new MockLogService();
        var servlet = new OldalbumServlet();
        var servletConfig = mock(ServletConfig.class);
        when(servletConfig.getInitParameter("from")).thenReturn("to");
        servlet.init(servletConfig);
        servlet.setLogService(logservice);
        servlet.setOldalbumService(oldalbum);
        servlet.activate();
        var request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/oldalbum/moto/");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/oldalbum/moto/"));
        when(request.getPathInfo()).thenReturn("/moto/");
        var response = new MockHttpServletResponse();

        servlet.service(request, response);

        assertEquals(SC_OK, response.getStatus());
        assertEquals("text/html", response.getContentType());
        assertThat(response.getBufferSize()).isPositive();
        assertThat(response.getOutputStreamContent())
            .contains("og:url")
            .doesNotContain("og:title")
            .doesNotContain("twitter:title")
            .doesNotContain("og:description")
            .doesNotContain("twitter:description")
            .doesNotContain("og:image")
            .doesNotContain("twitter:image");
    }

    @Test
    void testGetNullAlbumEntry() throws Exception {
        var oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths(false)).thenReturn(Arrays.asList("/moto/"));
        var logservice = new MockLogService();
        var servlet = new OldalbumServlet();
        var servletConfig = mock(ServletConfig.class);
        when(servletConfig.getInitParameter("from")).thenReturn("to");
        servlet.init(servletConfig);
        servlet.setLogService(logservice);
        servlet.setOldalbumService(oldalbum);
        servlet.activate();
        var request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/oldalbum/moto/");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/oldalbum/moto/"));
        when(request.getPathInfo()).thenReturn("/moto/");
        var response = new MockHttpServletResponse();

        servlet.service(request, response);

        assertEquals(SC_OK, response.getStatus());
        assertEquals("text/html", response.getContentType());
        assertThat(response.getBufferSize()).isPositive();
        assertThat(response.getOutputStreamContent())
            .contains("og:url")
            .doesNotContain("og:title")
            .doesNotContain("twitter:title")
            .doesNotContain("og:description")
            .doesNotContain("twitter:description")
            .doesNotContain("og:image")
            .doesNotContain("twitter:image");
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
        var oldalbum = mock(OldAlbumService.class);
        var logservice = new MockLogService();
        var request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/oldalbum/useradmin"));
        when(request.getServletPath()).thenReturn("/frontend-karaf-demo");
        var response = new MockHttpServletResponse();

        var servlet = new OldalbumServlet();
        servlet.setLogService(logservice);
        servlet.setOldalbumService(oldalbum);
        servlet.activate();

        servlet.service(request, response);

        assertEquals(SC_FOUND, response.getStatus());
    }

    @Test
    void testDoGetResponseThrowsIOException() throws Exception {
        var oldalbum = mock(OldAlbumService.class);
        var logservice = new MockLogService();
        var request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/oldalbum/favicon.ico");
        when(request.getPathInfo()).thenReturn("favicon.ico");
        var response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);
        response.resetAll();
        var streamThrowingIOException = mock(ServletOutputStream.class);
        doThrow(IOException.class).when(streamThrowingIOException).write(anyInt());
        when(response.getOutputStream()).thenReturn(streamThrowingIOException);

        var servlet = new OldalbumServlet();
        servlet.setLogService(logservice);
        servlet.setOldalbumService(oldalbum);
        servlet.activate();

        servlet.service(request, response);

        assertEquals(SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test
    void testDoGetResponseStreamMethodThrowsIOException() throws Exception {
        var oldalbum = mock(OldAlbumService.class);
        var logservice = new MockLogService();
        var request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/oldalbum/useradmin/");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8181/oldalbum/useradmin/"));
        when(request.getPathInfo()).thenReturn("/");
        var response = mock(MockHttpServletResponse.class, CALLS_REAL_METHODS);
        response.resetAll();
        when(response.getOutputStream()).thenThrow(IOException.class);

        var servlet = new OldalbumServlet();
        servlet.setLogService(logservice);
        servlet.setOldalbumService(oldalbum);
        servlet.activate();

        servlet.service(request, response);

        assertEquals(SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test
    void testDoGetResourceNotFound() throws Exception {
        var oldalbum = mock(OldAlbumService.class);
        var logservice = new MockLogService();
        var request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("http://localhost:8181/oldalbum/useradmin/static/nosuchname.png");
        when(request.getPathInfo()).thenReturn("/static/nosuchname.png");
        var response = new MockHttpServletResponse();

        var servlet = new OldalbumServlet();
        servlet.setLogService(logservice);
        servlet.setOldalbumService(oldalbum);
        servlet.activate();

        servlet.service(request, response);

        assertEquals(SC_NOT_FOUND, response.getStatusCode());
        assertEquals("text/html", response.getContentType());
        assertThat(response.getOutputStreamContent()).contains("bundle.js");
    }

    @Test
    void testDoGetRoutesWhenNotLoggedIn() {
        var oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths(false)).thenReturn(Arrays.asList("/path1", "/path2"));
        when(oldalbum.getPaths(true)).thenReturn(Arrays.asList("/path1", "/path2", "/path3"));
        var logservice = new MockLogService();

        var servlet = new OldalbumServlet();
        servlet.setLogService(logservice);
        servlet.setOldalbumService(oldalbum);
        servlet.activate();

        var routes = servlet.getRoutes();

        assertThat(routes).hasSize(6 + 2);
    }

    @Test
    void testDoGetRoutesWhenLoggedIn() {
        var oldalbum = mock(OldAlbumService.class);
        when(oldalbum.getPaths(false)).thenReturn(Arrays.asList("/path1", "/path2"));
        when(oldalbum.getPaths(true)).thenReturn(Arrays.asList("/path1", "/path2", "/path3"));
        var logservice = new MockLogService();

        var servlet = new OldalbumServlet();
        servlet.setLogService(logservice);
        servlet.setOldalbumService(oldalbum);
        servlet.activate();

        loginUser("jad", "1ad");

        var routes = servlet.getRoutes();

        assertThat(routes).hasSize(6 + 3);
    }

    @Test
    void testIsNullOrBlankOnNull() {
        var servlet = new OldalbumServlet();
        assertTrue(servlet.isNullOrBlank(null));
    }

    @Test
    void testIsNullOrBlankOnEmpty() {
        var servlet = new OldalbumServlet();
        assertTrue(servlet.isNullOrBlank(""));
    }

    @Test
    void testIsNullOrBlankOnBlank() {
        var servlet = new OldalbumServlet();
        assertTrue(servlet.isNullOrBlank(" "));
    }

    @Test
    void testIsNullOrBlankOnNonEmpty() {
        var servlet = new OldalbumServlet();
        assertFalse(servlet.isNullOrBlank("xyzzy"));
    }

    @Test
    void testFindLastPartOfPath() {
        var servlet = new OldalbumServlet();
        assertThat(servlet.findLastPartOfPath(AlbumEntry.with().path("/moto/vfr96/acirc1").build())).isEqualTo("acirc1");
    }

    @Test
    void testFindLastPartOfPathWithNullPath() {
        var servlet = new OldalbumServlet();
        assertThat(servlet.findLastPartOfPath(AlbumEntry.with().build())).isEqualTo("");
    }

    @Test
    void testFindLastPartOfPathWithEmptyPath() {
        var servlet = new OldalbumServlet();
        assertThat(servlet.findLastPartOfPath(AlbumEntry.with().path("").build())).isEqualTo("");
    }

    @Test
    void testFindLastPartOfPathWithRandomString() {
        var servlet = new OldalbumServlet();
        assertThat(servlet.findLastPartOfPath(AlbumEntry.with().path("xyzzy").build())).isEqualTo("xyzzy");
    }

    @Test
    void testFormatDateAndSizeOnJustCurrentDate() {
        var servlet = new OldalbumServlet();
        var now = new Date();
        var formattedNow = servlet.formatDateAndSize(AlbumEntry.with().lastModified(now).build());
        assertThat(formattedNow).isEqualTo(format("%tF ", now));
    }

    @Test
    void testFormatDateAndSizeOnNullDate() {
        var servlet = new OldalbumServlet();
        var formattedNow = servlet.formatDateAndSize(AlbumEntry.with().build());
        assertThat(formattedNow).isEqualTo("");
    }

    @Test
    void testFormatDateAndSizeOn1BFileWithNullDate() {
        var servlet = new OldalbumServlet();
        var formattedNow = servlet.formatDateAndSize(AlbumEntry.with().contentLength(1).build());
        assertThat(formattedNow).isEqualTo("1B");
    }

    @Test
    void testFormatDateAndSizeOn84kBFileWithNullDate() {
        var servlet = new OldalbumServlet();
        var formattedNow = servlet.formatDateAndSize(AlbumEntry.with().contentLength(84323).build());
        assertThat(formattedNow).isEqualTo("84kB");
    }

    @Test
    void testFormatDateAndSizeOn7MBFileWithNullDate() {
        var servlet = new OldalbumServlet();
        var formattedNow = servlet.formatDateAndSize(AlbumEntry.with().contentLength(6718072).build());
        assertThat(formattedNow).isEqualTo("7MB");
    }

    @Test
    void testFormatDateAndSizeOnNegativeSizeFileWithNullDate() {
        var servlet = new OldalbumServlet();
        var formattedNow = servlet.formatDateAndSize(AlbumEntry.with().contentLength(-1).build());
        assertThat(formattedNow).isEqualTo("");
    }

    protected void loginUser(String username, String password) {
        var token = new UsernamePasswordToken(username, password.toCharArray(), true);
        subject.login(token);
    }

    protected WebSubject createSubjectAndBindItToThread() {
        return createSubjectAndBindItToThread(getSecurityManager());
    }

    protected WebSubject createSubjectAndBindItToThread(WebSecurityManager webSecurityManager) {
        var session = mock(HttpSession.class);
        var dummyrequest = new MockHttpServletRequest();
        dummyrequest.setSession(session);
        var dummyresponse = new MockHttpServletResponse();
        return createSubjectAndBindItToThread(webSecurityManager, dummyrequest, dummyresponse);
    }

    protected WebSubject createSubjectAndBindItToThread(WebSecurityManager webSecurityManager, HttpServletRequest request, HttpServletResponse response) {
        var webSubject = new WebSubject.Builder(webSecurityManager, request, response).buildWebSubject();
        ThreadContext.bind(webSubject);
        return webSubject;
    }

    public static WebSecurityManager getSecurityManager() {
        if (securitymanager == null) {
            var environment = new IniWebEnvironment();
            environment.setIni(Ini.fromResourcePath("classpath:test.shiro.ini"));
            environment.init();
            securitymanager = environment.getWebSecurityManager();
        }

        return securitymanager;
    }

}
