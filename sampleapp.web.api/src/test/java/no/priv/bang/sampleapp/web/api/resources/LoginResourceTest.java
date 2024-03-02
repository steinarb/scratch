/*
 * Copyright 2021-2024 Steinar Bang
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
package no.priv.bang.sampleapp.web.api.resources;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Base64;

import javax.ws.rs.InternalServerErrorException;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockServletContext;

import no.priv.bang.sampleapp.services.SampleappService;
import no.priv.bang.sampleapp.services.beans.Credentials;
import no.priv.bang.sampleapp.web.api.AbstractShiroTest;
import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserManagementService;

class LoginResourceTest extends AbstractShiroTest {

    @BeforeAll
    static void beforeAll() {
        readTestShiroIni();
    }

    @AfterAll
    static void afterAll() {
        tearDownShiro();
    }

    @BeforeEach
    void beforeEach() {
        var subject = new Subject.Builder(getSecurityManager()).buildSubject();
        setSubject(subject);
    }

    @AfterEach
    void afterEach( ) {
        clearSubject();
    }

    @Test
    void testLogin() {
        var username = "jd";
        var password = Base64.getEncoder().encodeToString("johnnyBoi".getBytes());
        var sampleapp = mock(SampleappService.class);
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getUser(anyString())).thenReturn(User.with().username(username).build());
        var logservice = new MockLogService();
        var httpRequest = new MockHttpServletRequest().setRequestURI("/sampleapp/");
        var webcontext = new MockServletContext();
        webcontext.setContextPath("/sampleapp");
        var resource = new LoginResource();
        resource.webcontext = webcontext;
        resource.request = httpRequest;
        resource.setLogservice(logservice);
        resource.sampleapp = sampleapp;
        resource.useradmin = useradmin;
        createSubjectAndBindItToThread();
        WebUtils.saveRequest(httpRequest);
        var locale = "nb_NO";
        var credentials = Credentials.with().username(username).password(password).build();
        var resultat = resource.login(locale, credentials);
        assertTrue(resultat.getSuccess());
        assertEquals(username, resultat.getUser().getUsername());
        assertEquals("/", resultat.getOriginalRequestUrl());
    }

    @Test
    void testLoginDifferentPath() {
        var username = "jd";
        var password = Base64.getEncoder().encodeToString("johnnyBoi".getBytes());
        var sampleapp = mock(SampleappService.class);
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getUser(anyString())).thenReturn(User.with().username(username).build());
        var logservice = new MockLogService();
        var httpRequest = new MockHttpServletRequest().setRequestURI("/sampleapp/counter");
        var webcontext = new MockServletContext();
        webcontext.setContextPath("/sampleapp");
        var resource = new LoginResource();
        resource.webcontext = webcontext;
        resource.request = httpRequest;
        resource.setLogservice(logservice);
        resource.sampleapp = sampleapp;
        resource.useradmin = useradmin;
        WebUtils.saveRequest(httpRequest);
        var locale = "nb_NO";
        var credentials = Credentials.with().username(username).password(password).build();
        var resultat = resource.login(locale, credentials);
        assertTrue(resultat.getSuccess());
        assertEquals(username, resultat.getUser().getUsername());
        assertEquals("/counter", resultat.getOriginalRequestUrl());
    }

    @Test
    void testLoginWithEmptyWebContextPath() {
        var username = "jd";
        var password = Base64.getEncoder().encodeToString("johnnyBoi".getBytes());
        var sampleapp = mock(SampleappService.class);
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getUser(anyString())).thenReturn(User.with().username(username).build());
        var logservice = new MockLogService();
        var httpRequest = new MockHttpServletRequest().setRequestURI("/");
        var webcontext = new MockServletContext();
        webcontext.setContextPath("");
        var resource = new LoginResource();
        resource.webcontext = webcontext;
        resource.request = httpRequest;
        resource.setLogservice(logservice);
        resource.sampleapp = sampleapp;
        resource.useradmin = useradmin;
        createSubjectAndBindItToThread();
        WebUtils.saveRequest(httpRequest);
        var locale = "nb_NO";
        var credentials = Credentials.with().username(username).password(password).build();
        var resultat = resource.login(locale, credentials);
        assertTrue(resultat.getSuccess());
        assertEquals(username, resultat.getUser().getUsername());
        assertEquals("/", resultat.getOriginalRequestUrl());
    }

    private void createSubjectAndBindItToThread() {
        // TODO Auto-generated method stub

    }

    @Test
    void testLoginNoWebContext() {
        var username = "jd";
        var password = Base64.getEncoder().encodeToString("johnnyBoi".getBytes());
        var sampleapp = mock(SampleappService.class);
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getUser(anyString())).thenReturn(User.with().username(username).build());
        var logservice = new MockLogService();
        var httpRequest = new MockHttpServletRequest().setRequestURI("/sampleapp/");
        var resource = new LoginResource();
        resource.request = httpRequest;
        resource.setLogservice(logservice);
        resource.sampleapp = sampleapp;
        resource.useradmin = useradmin;
        createSubjectAndBindItToThread();
        WebUtils.saveRequest(httpRequest);
        var locale = "nb_NO";
        var credentials = Credentials.with().username(username).password(password).build();
        assertThrows(InternalServerErrorException.class, () -> resource.login(locale, credentials));
        assertThat(logservice.getLogmessages()).hasSize(1);
        assertThat(logservice.getLogmessages().get(0)).contains("NullPointerException");
    }

    @Test
    void testLoginByUserWithoutRole() {
        var request = new MockHttpServletRequest();
        var logservice = new MockLogService();
        var sampleapp = mock(SampleappService.class);
        var useradmin = mock(UserManagementService.class);
        var webcontext = new MockServletContext();
        webcontext.setContextPath("/sampleapp");
        var resource = new LoginResource();
        resource.webcontext = webcontext;
        resource.request = request;
        resource.setLogservice(logservice);
        resource.sampleapp = sampleapp;
        resource.useradmin = useradmin;
        var username = "jd";
        var password = Base64.getEncoder().encodeToString("johnnyBoi".getBytes());
        createSubjectAndBindItToThread();
        var credentials = Credentials.with().username(username).password(password).build();
        var locale = "nb_NO";
        var result = resource.login(locale, credentials);
        assertTrue(result.getSuccess());
        assertFalse(result.isAuthorized());
    }

    @Test
    void testLoginWithOriginalRequestUrl() {
        var request = new MockHttpServletRequest()
            .setContextPath("/sampleapp");
        var logservice = new MockLogService();
        var sampleapp = mock(SampleappService.class);
        var useradmin = mock(UserManagementService.class);
        var webcontext = new MockServletContext();
        webcontext.setContextPath("/sampleapp");
        var resource = new LoginResource();
        resource.webcontext = webcontext;
        resource.request = request;
        resource.setLogservice(logservice);
        resource.sampleapp = sampleapp;
        resource.useradmin = useradmin;
        var username = "jad";
        var password = Base64.getEncoder().encodeToString("1ad".getBytes());
        var originalRequest = new MockHttpServletRequest();
        originalRequest.setRequestURI("/sampleapp/");
        createSubjectFromOriginalRequestAndBindItToThread(originalRequest);
        WebUtils.saveRequest(originalRequest);
        var credentials = Credentials.with().username(username).password(password).build();
        var locale = "nb_NO";
        var result = resource.login(locale, credentials);
        assertTrue(result.getSuccess());
        assertTrue(result.isAuthorized());
        assertEquals("/", result.getOriginalRequestUrl());
    }

    private void createSubjectFromOriginalRequestAndBindItToThread(MockHttpServletRequest originalRequest) {
        // TODO Auto-generated method stub

    }

    @Test
    void testLoginFeilPassord() {
        var sampleapp = mock(SampleappService.class);
        when(sampleapp.displayText(anyString(), anyString())).thenReturn("Feil passord");
        var logservice = new MockLogService();
        var resource = new LoginResource();
        resource.sampleapp = sampleapp;
        resource.setLogservice(logservice);
        var username = "jd";
        var password = Base64.getEncoder().encodeToString("feil".getBytes());
        createSubjectAndBindItToThread();
        var credentials = Credentials.with().username(username).password(password).build();
        var locale = "nb_NO";
        var result = resource.login(locale, credentials);
        assertFalse(result.getSuccess());
        assertThat(result.getErrormessage()).startsWith("Feil passord");
    }

    @Test
    void testLoginUkjentBrukernavn() {
        var sampleapp = mock(SampleappService.class);
        when(sampleapp.displayText(anyString(), anyString())).thenReturn("Ukjent konto");
        var logservice = new MockLogService();
        var resource = new LoginResource();
        resource.sampleapp = sampleapp;
        resource.setLogservice(logservice);
        var username = "jdd";
        var password = Base64.getEncoder().encodeToString("feil".getBytes());
        createSubjectAndBindItToThread();
        var credentials = Credentials.with().username(username).password(password).build();
        var locale = "nb_NO";
        var result = resource.login(locale, credentials);
        assertThat(result.getErrormessage()).startsWith("Ukjent konto");
    }

    @Test
    void testFindUserSafelyWithUnknownUsername() {
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getUser(anyString())).thenThrow(AuthserviceException.class);
        var resource = new LoginResource();
        resource.useradmin = useradmin;
        var user = resource.findUserSafely("null");
        assertNull(user.getUsername());
    }

    @Test
    void testLogout() {
        var locale = "nb_NO";
        var sampleapp = mock(SampleappService.class);
        when(sampleapp.displayText(anyString(), anyString())).thenReturn("Logget ut");
        var resource = new LoginResource();
        resource.sampleapp = sampleapp;
        var subject = getSubject();

        var loginresult = resource.logout(locale);
        assertFalse(loginresult.getSuccess());
        assertEquals("Logget ut", loginresult.getErrormessage());
        assertFalse(loginresult.isAuthorized());
        assertFalse(subject.isAuthenticated()); // Verify user has been logged out
    }

    @Test
    void testGetLoginstateWhenLoggedIn() {
        var locale = "nb_NO";
        var sampleapp = mock(SampleappService.class);
        when(sampleapp.displayText(anyString(), anyString())).thenReturn("Bruker er logget inn og har tilgang");
        var useradmin = mock(UserManagementService.class);
        var resource = new LoginResource();
        resource.sampleapp = sampleapp;
        resource.useradmin = useradmin;
        var username = "jad";
        var password = "1ad";
        var subject = getSubject();
        var token = new UsernamePasswordToken(username, password.toCharArray(), true);
        subject.login(token);

        var loginresult = resource.loginstate(locale);
        assertTrue(loginresult.getSuccess());
        assertEquals("Bruker er logget inn og har tilgang", loginresult.getErrormessage());
        assertTrue(loginresult.isAuthorized());
    }

    @Test
    void testGetLoginstateWhenLoggedInButUserDoesntHaveRoleSampleappuser() {
        var locale = "nb_NO";
        var sampleapp = mock(SampleappService.class);
        when(sampleapp.displayText(anyString(), anyString())).thenReturn("Bruker er logget inn men mangler tilgang");
        var useradmin = mock(UserManagementService.class);
        var resource = new LoginResource();
        resource.sampleapp = sampleapp;
        resource.useradmin = useradmin;
        var username = "jd";
        var password = "johnnyBoi";
        var subject = getSubject();
        var token = new UsernamePasswordToken(username, password.toCharArray(), true);
        subject.login(token);

        var loginresult = resource.loginstate(locale);
        assertTrue(loginresult.getSuccess());
        assertEquals("Bruker er logget inn men mangler tilgang", loginresult.getErrormessage());
        assertFalse(loginresult.isAuthorized());
    }

    @Test
    void testGetLoginstateWhenNotLoggedIn() {
        var locale = "nb_NO";
        var sampleapp = mock(SampleappService.class);
        when(sampleapp.displayText(anyString(), anyString())).thenReturn("Bruker er ikke logget inn");
        var useradmin = mock(UserManagementService.class);
        var resource = new LoginResource();
        resource.sampleapp = sampleapp;
        resource.useradmin = useradmin;
        createSubjectAndBindItToThread();

        var loginresult = resource.loginstate(locale);
        assertFalse(loginresult.getSuccess());
        assertEquals("Bruker er ikke logget inn", loginresult.getErrormessage());
        assertFalse(loginresult.isAuthorized());
    }

}
