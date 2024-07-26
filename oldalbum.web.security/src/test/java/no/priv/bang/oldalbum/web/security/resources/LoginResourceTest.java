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
package no.priv.bang.oldalbum.web.security.resources;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpSession;
import javax.ws.rs.InternalServerErrorException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.subject.WebSubject;
import org.junit.jupiter.api.Test;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;

import no.priv.bang.oldalbum.testutilities.ShiroTestBase;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class LoginResourceTest extends ShiroTestBase {

    @Test
    void testGetLogin() {
        var resource = new LoginResource();
        var originalUri = "https://mysite.com";
        var htmlfile = resource.getLogin(originalUri);
        var html = (String) htmlfile.getEntity();
        assertThat(html).contains(originalUri);
    }

    @Test
    void testPostLogin() {
        var logservice = new MockLogService();
        var session = mock(HttpSession.class);
        var dummyrequest = new MockHttpServletRequest();
        dummyrequest.setSession(session);
        var dummyresponse = new MockHttpServletResponse();
        createSubjectAndBindItToThread(dummyrequest, dummyresponse);
        var resource = new LoginResource();
        resource.setLogservice(logservice);
        var username = "admin";
        var password = "admin";
        var redirectUrl = "https://myserver.com/resource";
        var response = resource.postLogin(username, password, redirectUrl);
        assertEquals(302, response.getStatus());
        assertEquals(redirectUrl, response.getLocation().toString());
    }

    @Test
    void testPostLoginWithNullRedirectUrl() {
        var logservice = new MockLogService();
        var session = mock(HttpSession.class);
        var dummyrequest = new MockHttpServletRequest();
        dummyrequest.setSession(session);
        var dummyresponse = new MockHttpServletResponse();
        createSubjectAndBindItToThread(dummyrequest, dummyresponse);
        var resource = new LoginResource();
        resource.setLogservice(logservice);
        var username = "admin";
        var password = "admin";
        var response = resource.postLogin(username, password, null);
        assertEquals(302, response.getStatus());
        assertEquals("", response.getLocation().toString());
    }

    @Test
    void testPostLoginWithUnknownUser() {
        var logservice = new MockLogService();
        var dummyrequest = new MockHttpServletRequest();
        var dummyresponse = new MockHttpServletResponse();
        createSubjectAndBindItToThread(dummyrequest, dummyresponse);
        var resource = new LoginResource();
        resource.setLogservice(logservice);
        var username = "notauser";
        var password = "admin";
        var redirectUrl = "https://myserver.com/resource";
        var response = resource.postLogin(username, password, redirectUrl);
        assertEquals(401, response.getStatus());
    }

    @Test
    void testPostLoginWithWrongPassword() {
        var logservice = new MockLogService();
        var dummyrequest = new MockHttpServletRequest();
        var dummyresponse = new MockHttpServletResponse();
        createSubjectAndBindItToThread(dummyrequest, dummyresponse);
        var resource = new LoginResource();
        resource.setLogservice(logservice);
        var username = "admin";
        var password = "wrongpassword";
        var redirectUrl = "https://myserver.com/resource";
        var response = resource.postLogin(username, password, redirectUrl);
        assertEquals(401, response.getStatus());
    }

    @Test
    void testPostLoginWithLockedAccount() {
        try {
            lockAccount("jad");
            // Set up the request
            var logservice = new MockLogService();
            var dummyrequest = new MockHttpServletRequest();
            var dummyresponse = new MockHttpServletResponse();
            createSubjectAndBindItToThread(dummyrequest, dummyresponse);
            var resource = new LoginResource();
            resource.setLogservice(logservice);
            var username = "jad";
            var password = "wrong";
            var redirectUrl = "https://myserver.com/resource";
            var response = resource.postLogin(username, password, redirectUrl);
            assertEquals(401, response.getStatus());
        } finally {
            unlockAccount("jad");
        }
    }

    @Test
    void testPostLoginWithAuthenticationException() {
        createSubjectThrowingExceptionAndBindItToThread(AuthenticationException.class);
        var logservice = new MockLogService();
        var resource = new LoginResource();
        resource.setLogservice(logservice);
        var username = "jad";
        var password = "wrong";
        var redirectUrl = "https://myserver.com/resource";
        var response = resource.postLogin(username, password, redirectUrl);
        assertEquals(401, response.getStatus());
    }

    @Test
    void testLoginWithUnexpectedException() {
        createSubjectThrowingExceptionAndBindItToThread(IllegalArgumentException.class);
        var logservice = new MockLogService();
        var resource = new LoginResource();
        resource.setLogservice(logservice);
        var username = "jad";
        var password = "wrong";
        var redirectUrl = "https://myserver.com/resource";
        assertThrows(InternalServerErrorException.class, () -> {
            resource.postLogin(username, password, redirectUrl);
        });
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
