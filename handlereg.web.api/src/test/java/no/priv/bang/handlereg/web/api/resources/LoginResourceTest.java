package no.priv.bang.handlereg.web.api.resources;
/*
 * Copyright 2019-2026 Steinar Bang
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

import static org.junit.jupiter.api.Assertions.*;

import java.util.Base64;

import javax.ws.rs.InternalServerErrorException;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.util.WebUtils;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockServletContext;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import no.priv.bang.handlereg.services.Credentials;
import no.priv.bang.handlereg.web.api.ShiroTestBase;

class LoginResourceTest extends ShiroTestBase {
    private static Logger logger;
    private ListAppender<ILoggingEvent> logappender;

    @BeforeEach
    void setup() {
        logger = (Logger) LoggerFactory.getLogger(LoginResource.class);
        logappender = new ListAppender<>();
        logappender.start();
        logger.addAppender(logappender);
    }

    @AfterEach
    void teardown() {
        logger.detachAppender(logappender);
        logappender.stop();
    }

    @Test
    void testLogin() {
        var httpRequest = new MockHttpServletRequest().setRequestURI("/handlereg/");
        var webcontext = new MockServletContext();
        webcontext.setContextPath("/handlereg");
        var resource = new LoginResource();
        resource.webcontext = webcontext;
        resource.request = httpRequest;
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
        var httpRequest = new MockHttpServletRequest().setRequestURI("/handlereg/statistikk");
        var webcontext = new MockServletContext();
        webcontext.setContextPath("/handlereg");
        var resource = new LoginResource();
        resource.webcontext = webcontext;
        resource.request = httpRequest;
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
        var httpRequest = new MockHttpServletRequest().setRequestURI("/");
        var webcontext = new MockServletContext();
        webcontext.setContextPath("");
        var resource = new LoginResource();
        resource.webcontext = webcontext;
        resource.request = httpRequest;
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
        var httpRequest = new MockHttpServletRequest().setRequestURI("/handlereg/");
        var resource = new LoginResource();
        resource.request = httpRequest;
        var username = "jd";
        var password = Base64.getEncoder().encodeToString("johnnyBoi".getBytes());
        createSubjectAndBindItToThread();
        WebUtils.saveRequest(httpRequest);
        var credentials = Credentials.with().username(username).password(password).build();
        assertThrows(InternalServerErrorException.class, () -> resource.login(credentials));
        assertThat(logappender.list).hasSize(1);
        assertThat(logappender.list.get(0).getFormattedMessage()).contains("Login error: internal server error");
    }

    @Test
    void testLoginNoSavedRequest() {
        var httpRequest = new MockHttpServletRequest().setRequestURI("/handlereg/");
        var webcontext = new MockServletContext();
        webcontext.setContextPath("/handlereg");
        var resource = new LoginResource();
        resource.webcontext = webcontext;
        resource.request = httpRequest;
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
        var resource = new LoginResource();
        var username = "jd";
        var password = Base64.getEncoder().encodeToString("feil".getBytes());
        createSubjectAndBindItToThread();
        var credentials = Credentials.with().username(username).password(password).build();
        var resultat = resource.login(credentials);
        assertFalse(resultat.suksess());
        assertThat(resultat.feilmelding()).startsWith("Feil passord");
    }

    @Test
    void testLoginGrenseForFeiledeInnloggingerNådd() {
        var resource = new LoginResource();
        var username = "jd";
        var password = Base64.getEncoder().encodeToString("feil".getBytes());
        createSubjectThrowingExceptionAndBindItToThread(ExcessiveAttemptsException.class);
        var credentials = Credentials.with().username(username).password(password).build();
        var resultat = resource.login(credentials);
        assertFalse(resultat.suksess());
        assertThat(resultat.feilmelding()).startsWith("Grense for feilede innlogginger nådd og konto er låst. Vennligst kontakt systemadministrator");
    }

    @Test
    void testLoginKontoLåst() {
        var resource = new LoginResource();
        var username = "jd";
        var password = Base64.getEncoder().encodeToString("feil".getBytes());
        createSubjectThrowingExceptionAndBindItToThread(LockedAccountException.class);
        var credentials = Credentials.with().username(username).password(password).build();
        var resultat = resource.login(credentials);
        assertFalse(resultat.suksess());
        assertThat(resultat.feilmelding()).startsWith("Låst konto");
    }

    @Test
    void testLoginGeneriskAutentiseringfeil() {
        var resource = new LoginResource();
        var username = "jd";
        var password = Base64.getEncoder().encodeToString("feil".getBytes());
        createSubjectThrowingExceptionAndBindItToThread(AuthenticationException.class);
        var credentials = Credentials.with().username(username).password(password).build();
        var resultat = resource.login(credentials);
        assertFalse(resultat.suksess());
        assertThat(resultat.feilmelding()).startsWith("Ukjent feil");
    }

    @Test
    void testLoginUkjentBrukernavn() {
        var resource = new LoginResource();
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
