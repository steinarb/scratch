package no.priv.bang.oldalbum.web.security.resources;
/*
 * Copyright 2024-2026 Steinar Bang
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
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URI;

import javax.servlet.http.HttpSession;
import javax.ws.rs.InternalServerErrorException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.subject.WebSubject;
import org.junit.jupiter.api.Test;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;

import no.priv.bang.oldalbum.testutilities.ShiroTestBase;

class LoginResourceTest extends ShiroTestBase {

    @Test
    void testGetLogin() {
        var originalUri = URI.create("https://mysite.com/localpath");
        var session = mock(HttpSession.class);
        var dummyrequest = MockHttpServletRequest.getRequest(originalUri);
        dummyrequest.setSession(session);
        var dummyresponse = new MockHttpServletResponse();
        createSubjectAndBindItToThread(dummyrequest, dummyresponse);
        var resource = new LoginResource();
        var htmlfile = resource.getLogin(null);
        var html = (String) htmlfile.getEntity();
        assertThat(html).contains(originalUri.getPath());
    }

    @Test
    void testPostLogin() {
        var session = mock(HttpSession.class);
        var dummyrequest = new MockHttpServletRequest();
        dummyrequest.setSession(session);
        var dummyresponse = new MockHttpServletResponse();
        createSubjectAndBindItToThread(dummyrequest, dummyresponse);
        var resource = new LoginResource();
        var username = "admin";
        var password = "admin";
        var redirectUrl = "https://myserver.com/resource";
        var response = resource.postLogin(username, password, redirectUrl);
        assertEquals(302, response.getStatus());
        assertEquals(redirectUrl, response.getLocation().toString());
    }

    @Test
    void testPostLoginWithNullRedirectUrl() {
        var session = mock(HttpSession.class);
        var dummyrequest = new MockHttpServletRequest();
        dummyrequest.setSession(session);
        var dummyresponse = new MockHttpServletResponse();
        createSubjectAndBindItToThread(dummyrequest, dummyresponse);
        var resource = new LoginResource();
        var username = "admin";
        var password = "admin";
        var response = resource.postLogin(username, password, null);
        assertEquals(302, response.getStatus());
        assertEquals("", response.getLocation().toString());
    }

    @Test
    void testPostLoginWithUnknownUser() {
        var dummyrequest = new MockHttpServletRequest();
        var dummyresponse = new MockHttpServletResponse();
        createSubjectAndBindItToThread(dummyrequest, dummyresponse);
        var resource = new LoginResource();
        var username = "notauser";
        var password = "admin";
        var redirectUrl = "https://myserver.com/resource";
        var response = resource.postLogin(username, password, redirectUrl);
        assertEquals(401, response.getStatus());
        assertThat(response.getEntity().toString()).contains("unknown user");
    }

    @Test
    void testPostLoginWithWrongPassword() {
        var dummyrequest = new MockHttpServletRequest();
        var dummyresponse = new MockHttpServletResponse();
        createSubjectAndBindItToThread(dummyrequest, dummyresponse);
        var resource = new LoginResource();
        var username = "admin";
        var password = "wrongpassword";
        var redirectUrl = "https://myserver.com/resource";
        var response = resource.postLogin(username, password, redirectUrl);
        assertEquals(401, response.getStatus());
        assertThat(response.getEntity().toString()).contains("wrong password");
    }

    @Test
    void testPostLoginWhenFailedLoginLimitIsReached() {
        try {
            lockAccount("jad");
            createSubjectThrowingExceptionAndBindItToThread(ExcessiveAttemptsException.class);
            var resource = new LoginResource();
            var username = "jad";
            var password = "wrong";
            var redirectUrl = "https://myserver.com/resource";
            var response = resource.postLogin(username, password, redirectUrl);
            assertEquals(401, response.getStatus());
            assertThat(response.getEntity().toString()).contains("maximum failed logins limit reached, account is locked. contact system administrator");
        } finally {
            unlockAccount("jad");
        }
    }

    @Test
    void testPostLoginWithLockedAccount() {
        try {
            lockAccount("jad");
            var dummyrequest = new MockHttpServletRequest();
            var dummyresponse = new MockHttpServletResponse();
            createSubjectAndBindItToThread(dummyrequest, dummyresponse);
            var resource = new LoginResource();
            var username = "jad";
            var password = "wrong";
            var redirectUrl = "https://myserver.com/resource";
            var response = resource.postLogin(username, password, redirectUrl);
            assertEquals(401, response.getStatus());
            assertThat(response.getEntity().toString()).contains("locked account");
        } finally {
            unlockAccount("jad");
        }
    }

    @Test
    void testPostLoginWithAuthenticationException() {
        createSubjectThrowingExceptionAndBindItToThread(AuthenticationException.class);
        var resource = new LoginResource();
        var username = "jad";
        var password = "wrong";
        var redirectUrl = "https://myserver.com/resource";
        var response = resource.postLogin(username, password, redirectUrl);
        assertEquals(401, response.getStatus());
        assertThat(response.getEntity().toString()).contains("general authentication error");
    }

    @Test
    void testLoginWithUnexpectedException() {
        createSubjectThrowingExceptionAndBindItToThread(IllegalArgumentException.class);
        var resource = new LoginResource();
        var username = "jad";
        var password = "wrong";
        var redirectUrl = "https://myserver.com/resource";
        assertThrows(InternalServerErrorException.class, () -> resource.postLogin(username, password, redirectUrl));
    }

    private void lockAccount(String username) {
        getShiroAccountFromRealm(username).setLocked(true);
    }

    private void unlockAccount(String username) {
        getShiroAccountFromRealm(username).setLocked(false);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private WebSubject createSubjectThrowingExceptionAndBindItToThread(Class exceptionClass) {
        var subject = mock(WebSubject.class);
        doThrow(exceptionClass).when(subject).login(any());
        ThreadContext.bind(subject);
        return subject;
    }

}
