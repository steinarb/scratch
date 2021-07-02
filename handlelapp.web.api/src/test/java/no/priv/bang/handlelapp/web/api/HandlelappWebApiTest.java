/*
 * Copyright 2021 Steinar Bang
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
package no.priv.bang.handlelapp.web.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ServerProperties;
import org.junit.jupiter.api.Test;
import org.osgi.service.log.LogService;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletOutputStream;

import no.priv.bang.handlelapp.services.Credentials;
import no.priv.bang.handlelapp.services.HandlelappService;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class HandlelappWebApiTest extends ShiroTestBase {
    public static final ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .findAndRegisterModules();

    @Test
    void testLogin() throws Exception {
        String username = "jd";
        String password = "johnnyBoi";
        Credentials credentials = Credentials.with().username(username).password(password).build();
        MockLogService logservice = new MockLogService();
        HandlelappService handlelapp = mock(HandlelappService.class);
        HandlelappWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlelapp , logservice);
        createSubjectAndBindItToThread();
        MockHttpServletRequest request = buildPostUrl("/login");
        String postBody = mapper.writeValueAsString(credentials);
        request.setBodyContent(postBody);
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.service(request, response);
        assertEquals(200, response.getStatus());

    }

    @Test
    void testLoginWrongPassword() throws Exception {
        String username = "jd";
        String password = "johnniBoi";
        Credentials credentials = Credentials.with().username(username).password(password).build();
        MockLogService logservice = new MockLogService();
        HandlelappService handlelapp = mock(HandlelappService.class);
        HandlelappWebApi servlet = simulateDSComponentActivationAndWebWhiteboardConfiguration(handlelapp , logservice);
        createSubjectAndBindItToThread();
        MockHttpServletRequest request = buildPostUrl("/login");
        String postBody = mapper.writeValueAsString(credentials);
        request.setBodyContent(postBody);
        MockHttpServletResponse response = new MockHttpServletResponse();

        servlet.service(request, response);
        assertEquals(200, response.getStatus());

    }

    private byte[] getBinaryContent(MockHttpServletResponse response) throws IOException {
        MockServletOutputStream outputstream = (MockServletOutputStream) response.getOutputStream();
        return outputstream.getBinaryContent();
    }

    private MockHttpServletRequest buildGetUrl(String resource) {
        MockHttpServletRequest request = buildRequest(resource);
        request.setMethod("GET");
        return request;
    }

    private MockHttpServletRequest buildPostUrl(String resource) {
        String contenttype = MediaType.APPLICATION_JSON;
        MockHttpServletRequest request = buildRequest(resource);
        request.setMethod("POST");
        request.setContentType(contenttype);
        request.addHeader("Content-Type", contenttype);
        return request;
    }

    private MockHttpServletRequest buildRequest(String resource) {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setProtocol("HTTP/1.1");
        request.setRequestURL("http://localhost:8181/handlelapp/api" + resource);
        request.setRequestURI("/handlelapp/api" + resource);
        request.setContextPath("/handlelapp");
        request.setServletPath("/api");
        request.setSession(session);
        return request;
    }

    private HandlelappWebApi simulateDSComponentActivationAndWebWhiteboardConfiguration(HandlelappService handlelapp, LogService logservice) throws Exception {
        HandlelappWebApi servlet = new HandlelappWebApi();
        servlet.setLogService(logservice);
        servlet.setHandlelappService(handlelapp);
        servlet.activate();
        ServletConfig config = createServletConfigWithApplicationAndPackagenameForJerseyResources();
        servlet.init(config);
        return servlet;
    }

    private ServletConfig createServletConfigWithApplicationAndPackagenameForJerseyResources() {
        ServletConfig config = mock(ServletConfig.class);
        when(config.getInitParameterNames()).thenReturn(Collections.enumeration(Arrays.asList(ServerProperties.PROVIDER_PACKAGES)));
        when(config.getInitParameter(eq(ServerProperties.PROVIDER_PACKAGES))).thenReturn("no.priv.bang.handlelapp.web.api.resources");
        ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getContextPath()).thenReturn("/handlelapp");
        when(config.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttributeNames()).thenReturn(Collections.emptyEnumeration());
        return config;
    }
}
