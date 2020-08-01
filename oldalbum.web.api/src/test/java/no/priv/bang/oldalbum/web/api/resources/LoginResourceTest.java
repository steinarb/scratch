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
package no.priv.bang.oldalbum.web.api.resources;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.InternalServerErrorException;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.subject.WebSubject;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import no.priv.bang.oldalbum.services.bean.Credentials;
import no.priv.bang.oldalbum.services.bean.LoginResult;
import no.priv.bang.oldalbum.web.api.ShiroTestBase;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class LoginResourceTest extends ShiroTestBase {

    @Test
    void testLogin() {
        LoginResource resource = new LoginResource();
        String username = "jd";
        String password = "johnnyBoi";
        createSubjectAndBindItToThread();
        Credentials credentials = new Credentials(username, password);
        LoginResult result = resource.login(credentials);
        assertTrue(result.getSuccess());
    }

    @Test
    void testLoginWrongPassword() {
        MockLogService logservice = new MockLogService();
        LoginResource resource = new LoginResource();
        resource.logservice = logservice;
        String username = "jd";
        String password = "feil";
        createSubjectAndBindItToThread();
        Credentials credentials = new Credentials(username, password);
        LoginResult result = resource.login(credentials);
        assertFalse(result.getSuccess());
        assertThat(result.getErrormessage()).startsWith("Wrong password");
    }

    @Test
    void testLoginUknownUser() {
        MockLogService logservice = new MockLogService();
        LoginResource resource = new LoginResource();
        resource.logservice = logservice;
        String username = "jdd";
        String password = "feil";
        createSubjectAndBindItToThread();
        Credentials credentials = new Credentials(username, password);
        LoginResult result = resource.login(credentials);
        assertThat(result.getErrormessage()).startsWith("Unknown account");
    }

    @Test
    void testLoginLockedUser() {
        MockLogService logservice = new MockLogService();
        LoginResource resource = new LoginResource();
        resource.logservice = logservice;
        String username = "lockeduser";
        String password = "lockpw";
        createSubjectAndBindItToThread();
        Credentials credentials = new Credentials(username, password);
        LoginResult result = resource.login(credentials);
        assertThat(result.getErrormessage()).startsWith("Locked account");
    }

    @Test
    void testLoginGenericAuthenticationException() {
        MockLogService logservice = new MockLogService();
        LoginResource resource = new LoginResource();
        resource.logservice = logservice;
        String username = "lockeduser";
        String password = "lockpw";
        WebSecurityManager securityManager = mock(WebSecurityManager.class);
        WebSubject subject = mock(WebSubject.class);
        doThrow(AuthenticationException.class).when(subject).login(any());
        when(securityManager.createSubject(any())).thenReturn(subject);
        createSubjectAndBindItToThread(securityManager);
        Credentials credentials = new Credentials(username, password);
        LoginResult result = resource.login(credentials);
        assertThat(result.getErrormessage()).startsWith("Unknown login error");
    }

    @Test
    void testLoginInternalServerError() {
        MockLogService logservice = new MockLogService();
        LoginResource resource = new LoginResource();
        resource.logservice = logservice;
        String username = "lockeduser";
        String password = "lockpw";
        WebSecurityManager securityManager = mock(WebSecurityManager.class);
        WebSubject subject = mock(WebSubject.class);
        doThrow(RuntimeException.class).when(subject).login(any());
        when(securityManager.createSubject(any())).thenReturn(subject);
        createSubjectAndBindItToThread(securityManager);
        Credentials credentials = new Credentials(username, password);
        assertThrows(InternalServerErrorException.class, () -> {
                resource.login(credentials);
            });
        assertThat(logservice.getLogmessages().size()).isPositive();
        assertThat(logservice.getLogmessages().get(0)).contains("Login error: internal server error");
    }

    @Test
    void testLogout() {
        LoginResource resource = new LoginResource();
        String username = "jd";
        String password = "johnnyBoi";
        createSubjectAndBindItToThread();
        Credentials credentials = new Credentials(username, password);
        LoginResult resultLogin = resource.login(credentials);
        assertTrue(resultLogin.getSuccess());
        LoginResult resultLogout = resource.logout(credentials);
        assertFalse(resultLogout.getSuccess());
    }

}
