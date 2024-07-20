/*
 * Copyright 2024 Steinar Bang
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
package no.priv.bang.oldalbum.web.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.glassfish.jersey.server.ServerProperties.PROVIDER_PACKAGES;

import javax.servlet.ServletConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.junit.jupiter.api.Test;
import org.osgi.service.log.LogService;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletConfig;
import com.mockrunner.mock.web.MockServletContext;

import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class OldalbumLoginServletTest extends ShiroTestBase {

    @Test
    void testGetLoginPageIndexHtml() throws Exception {
        var logservice = new MockLogService();

        var request = buildGetLoginUrl();
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(logservice);

        createSubjectAndBindItToThread(request, response);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertThat(response.getOutputStreamContent()).contains("Oldalbum login");
    }

    @Test
    void testAuthenticate() throws Exception {
        var logservice = new MockLogService();

        var originalRequestUrl = "https://myserver.com/someresource";
        var request = buildPostToLoginUrl(originalRequestUrl);
        var body = UriBuilder.fromUri("http://localhost:8181/oldalbum")
            .queryParam("username", "admin")
            .queryParam("password", "admin")
            .build().getQuery();
        request.setBodyContent(body);
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(logservice);

        createSubjectAndBindItToThread(request, response);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_FOUND, response.getStatus());
        assertThat(response.getOutputStreamContent()).contains("Login successful");
    }

    @Test
    void testAuthenticateUnknownAccount() throws Exception {
        var logservice = new MockLogService();

        var originalRequestUrl = "https://myserver.com/someresource";
        var request = buildPostToLoginUrl(originalRequestUrl);
        var body = UriBuilder.fromUri("http://localhost:8181/oldalbum")
            .queryParam("username", "jjd")
            .queryParam("password", "admin")
            .build().getQuery();
        request.setBodyContent(body);
        var response = new MockHttpServletResponse();

        // Emulate DS component setup
        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(logservice);

        createSubjectAndBindItToThread(request, response);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertThat(response.getOutputStreamContent()).contains("unknown user");
    }

    @Test
    void testAuthenticateWrongPassword() throws Exception {
        var logservice = new MockLogService();

        var originalRequestUrl = "https://myserver.com/someresource";
        var request = buildPostToLoginUrl(originalRequestUrl);
        var body = UriBuilder.fromUri("http://localhost:8181/oldalbum")
            .queryParam("username", "admin")
            .queryParam("password", "wrongpass")
            .build().getQuery();
        request.setBodyContent(body);
        var response = new MockHttpServletResponse();

        var servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(logservice);

        createSubjectAndBindItToThread(request, response);

        servlet.service(request, response);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertThat(response.getOutputStreamContent()).contains("Error: wrong password");
    }

    private MockHttpServletRequest buildGetLoginUrl() {
        var session = new MockHttpSession();
        var request = new MockHttpServletRequest();
        request.setProtocol("HTTP/1.1");
        request.setMethod("GET");
        request.setRequestURL("http://localhost:8181/oldalbum/auth/login");
        request.setRequestURI("/oldalbum/auth/login");
        request.setContextPath("/oldalbum");
        request.setServletPath("/auth");
        request.setSession(session);
        return request;
    }

    private MockHttpServletRequest buildPostToLoginUrl(String originalUrl) {
        var contenttype = MediaType.APPLICATION_FORM_URLENCODED;
        var session = new MockHttpSession();
        var request = new MockHttpServletRequest();
        request.setProtocol("HTTP/1.1");
        request.setMethod("POST");
        request.setRequestURL("http://localhost:8181/oldalbum/auth/login");
        request.setRequestURI("/oldalbum/auth/login");
        request.setContextPath("/oldalbum");
        request.setServletPath("/auth");
        request.setContentType(contenttype);
        request.addHeader("Content-Type", contenttype);
        request.addCookie(new Cookie("NSREDIRECT", originalUrl));
        request.addHeader("Cookie", "NSREDIRECT=" + originalUrl);
        request.setSession(session);
        return request;
    }

    private OldalbumLoginServlet simulateDSComponentActivationAndWebWhiteboardConfiguration(LogService logservice) throws Exception {
        var servlet = new OldalbumLoginServlet();
        servlet.setLogService(logservice);
        servlet.activate();
        var config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);
        return servlet;
    }

    private ServletConfig createServletConfigWithApplicationAndPackagenameForJerseyResources() {
        var servletContext = new MockServletContext();
        servletContext.setContextPath("/oldalbum");
        servletContext.setServletContextName("oldalbum");
        var config = new MockServletConfig();
        config.setServletContext(servletContext);
        config.setInitParameter(PROVIDER_PACKAGES, "no.priv.bang.oldalbum.web.security.resources");
        return config;
    }

}
