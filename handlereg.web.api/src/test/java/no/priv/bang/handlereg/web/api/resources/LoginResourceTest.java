/*
 * Copyright 2019-2024 Steinar Bang
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
package no.priv.bang.handlereg.web.api.resources;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;

import javax.ws.rs.InternalServerErrorException;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.util.WebUtils;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockServletContext;

import no.priv.bang.handlereg.services.Credentials;
import no.priv.bang.handlereg.web.api.ShiroTestBase;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class LoginResourceTest extends ShiroTestBase {

    @Test
    void testLogin() {
        var logservice = new MockLogService();
        var httpRequest = new MockHttpServletRequest().setRequestURI("/handlereg/");
        var webcontext = new MockServletContext();
        webcontext.setContextPath("/handlereg");
        var resource = new LoginResource();
        resource.webcontext = webcontext;
        resource.request = httpRequest;
        resource.setLogservice(logservice);
        var username = "jd";
        var password = Base64.getEncoder().encodeToString("johnnyBoi".getBytes());
        createSubjectAndBindItToThread();
        WebUtils.saveRequest(httpRequest);
        var credentials = Credentials.with().username(username).password(password).build();
        var resultat = resource.login(credentials);
        assertTrue(resultat.suksess());
        assertEquals(username, resultat.brukernavn());
        assertEquals("/", resultat.originalRequestUrl());
    }

    @Test
    void testLoginDifferentPath() {
        var logservice = new MockLogService();
        var httpRequest = new MockHttpServletRequest().setRequestURI("/handlereg/statistikk");
        var webcontext = new MockServletContext();
        webcontext.setContextPath("/handlereg");
        var resource = new LoginResource();
        resource.webcontext = webcontext;
        resource.request = httpRequest;
        resource.setLogservice(logservice);
        var username = "jd";
        var password = Base64.getEncoder().encodeToString("johnnyBoi".getBytes());
        createSubjectAndBindItToThread();
        WebUtils.saveRequest(httpRequest);
        var credentials = Credentials.with().username(username).password(password).build();
        var resultat = resource.login(credentials);
        assertTrue(resultat.suksess());
        assertEquals(username, resultat.brukernavn());
        assertEquals("/statistikk", resultat.originalRequestUrl());
    }

    @Test
    void testLoginWithEmptyWebContextPath() {
        var logservice = new MockLogService();
        var httpRequest = new MockHttpServletRequest().setRequestURI("/");
        var webcontext = new MockServletContext();
        webcontext.setContextPath("");
        var resource = new LoginResource();
        resource.webcontext = webcontext;
        resource.request = httpRequest;
        resource.setLogservice(logservice);
        var username = "jd";
        var password = Base64.getEncoder().encodeToString("johnnyBoi".getBytes());
        createSubjectAndBindItToThread();
        WebUtils.saveRequest(httpRequest);
        var credentials = Credentials.with().username(username).password(password).build();
        var resultat = resource.login(credentials);
        assertTrue(resultat.suksess());
        assertEquals(username, resultat.brukernavn());
        assertEquals("/", resultat.originalRequestUrl());
    }

    @Test
    void testLoginNoWebContext() {
        var logservice = new MockLogService();
        var httpRequest = new MockHttpServletRequest().setRequestURI("/handlereg/");
        var resource = new LoginResource();
        resource.request = httpRequest;
        resource.setLogservice(logservice);
        var username = "jd";
        var password = Base64.getEncoder().encodeToString("johnnyBoi".getBytes());
        createSubjectAndBindItToThread();
        WebUtils.saveRequest(httpRequest);
        var credentials = Credentials.with().username(username).password(password).build();
        assertThrows(InternalServerErrorException.class, () -> resource.login(credentials));
        assertThat(logservice.getLogmessages()).hasSize(1);
        assertThat(logservice.getLogmessages().get(0)).contains("NullPointerException");
    }

    @Test
    void testLoginNoSavedRequest() {
        var logservice = new MockLogService();
        var httpRequest = new MockHttpServletRequest().setRequestURI("/handlereg/");
        var webcontext = new MockServletContext();
        webcontext.setContextPath("/handlereg");
        var resource = new LoginResource();
        resource.webcontext = webcontext;
        resource.request = httpRequest;
        resource.setLogservice(logservice);
        var username = "jd";
        var password = Base64.getEncoder().encodeToString("johnnyBoi".getBytes());
        createSubjectAndBindItToThread();
        var credentials = Credentials.with().username(username).password(password).build();
        var resultat = resource.login(credentials);
        assertTrue(resultat.suksess());
        assertEquals(username, resultat.brukernavn());
        assertEquals("/", resultat.originalRequestUrl());
    }

    @Test
    void testLoginFeilPassord() {
        var logservice = new MockLogService();
        var resource = new LoginResource();
        resource.setLogservice(logservice);
        var username = "jd";
        var password = Base64.getEncoder().encodeToString("feil".getBytes());
        createSubjectAndBindItToThread();
        var credentials = Credentials.with().username(username).password(password).build();
        var resultat = resource.login(credentials);
        assertFalse(resultat.suksess());
        assertThat(resultat.feilmelding()).startsWith("Feil passord");
    }

    @Test
    void testLoginUkjentBrukernavn() {
        var logservice = new MockLogService();
        var resource = new LoginResource();
        resource.setLogservice(logservice);
        var username = "jdd";
        var password = Base64.getEncoder().encodeToString("feil".getBytes());
        createSubjectAndBindItToThread();
        var credentials = Credentials.with().username(username).password(password).build();
        var resultat = resource.login(credentials);
        assertThat(resultat.feilmelding()).startsWith("Ukjent konto");
    }

    @Test
    void testLogout() {
        var resource = new LoginResource();
        var username = "jd";
        var password = "johnnyBoi";
        var subject = createSubjectAndBindItToThread();
        var token = new UsernamePasswordToken(username, password.toCharArray(), true);
        subject.login(token);
        assertTrue(subject.isAuthenticated()); // Verify precondition user logged in

        var loginresultat = resource.logout();
        assertFalse(loginresultat.suksess());
        assertEquals("Logget ut", loginresultat.feilmelding());
        assertFalse(loginresultat.authorized());
        assertFalse(subject.isAuthenticated()); // Verify user has been logged out
    }

    @Test
    void testGetLogintilstandWhenLoggedIn() {
        var resource = new LoginResource();
        var username = "jd";
        var password = "johnnyBoi";
        var subject = createSubjectAndBindItToThread();
        var token = new UsernamePasswordToken(username, password.toCharArray(), true);
        subject.login(token);

        var loginresultat = resource.logintilstand();
        assertTrue(loginresultat.suksess());
        assertEquals("Bruker er logget inn og har tilgang", loginresultat.feilmelding());
        assertTrue(loginresultat.authorized());
        assertEquals(username, loginresultat.brukernavn());
    }

    @Test
    void testGetLogintilstandWhenLoggedInButUserDoesntHaveRoleHandleregbruker() {
        var resource = new LoginResource();
        var username = "jad";
        var password = "1ad";
        var subject = createSubjectAndBindItToThread();
        var token = new UsernamePasswordToken(username, password.toCharArray(), true);
        subject.login(token);

        var loginresultat = resource.logintilstand();
        assertTrue(loginresultat.suksess());
        assertEquals("Bruker er logget inn men mangler tilgang", loginresultat.feilmelding());
        assertFalse(loginresultat.authorized());
    }

    @Test
    void testGetLogintilstandWhenNotLoggedIn() {
        var resource = new LoginResource();
        createSubjectAndBindItToThread();

        var loginresultat = resource.logintilstand();
        assertFalse(loginresultat.suksess());
        assertEquals("Bruker er ikke logget inn", loginresultat.feilmelding());
        assertFalse(loginresultat.authorized());
    }

}
