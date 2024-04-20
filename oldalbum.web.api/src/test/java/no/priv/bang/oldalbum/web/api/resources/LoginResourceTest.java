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
package no.priv.bang.oldalbum.web.api.resources;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import javax.servlet.http.HttpSession;
import javax.ws.rs.InternalServerErrorException;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.subject.WebSubject;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;

import no.priv.bang.oldalbum.backend.OldAlbumServiceProvider;
import no.priv.bang.oldalbum.services.bean.Credentials;
import no.priv.bang.oldalbum.web.api.ShiroTestBase;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.UserManagementService;

class LoginResourceTest extends ShiroTestBase {

    @Test
    void testLoginCheck() {
        var oldalbum = new OldAlbumServiceProvider();
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var resource = new LoginResource();
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        var username = "admin";
        var password = "admin";
        createSubjectAndBindItToThread();
        var credentials = Credentials.with().username(username).password(password).build();
        var locale = "";
        resource.login(locale, credentials); // Ensure user is logged in

        var result = resource.loginCheck();
        assertTrue(result.getSuccess());
        assertTrue(result.isCanModifyAlbum());
        assertTrue(result.isCanLogin());
    }

    @Test
    void testLoginCheckWhenNotLoggedIn() {
        var oldalbum = new OldAlbumServiceProvider();
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var resource = new LoginResource();
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        createSubjectAndBindItToThread();

        var result = resource.loginCheck();
        assertFalse(result.getSuccess());
        assertFalse(result.isCanModifyAlbum());
    }

    @Test
    void testLoginCheckWhenNotLoggedInAndOldalbumadminRoleNotPresent() {
        var oldalbum = new OldAlbumServiceProvider();
        var useradmin = mock(UserManagementService.class);
        var resource = new LoginResource();
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        createSubjectAndBindItToThread();

        var result = resource.loginCheck();
        assertFalse(result.getSuccess());
        assertFalse(result.isCanModifyAlbum());
        assertFalse(result.isCanLogin());
    }

    @Test
    void testLogin() {
        var oldalbum = new OldAlbumServiceProvider();
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var resource = new LoginResource();
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        var username = "admin";
        var password = "admin";
        createSubjectAndBindItToThread();
        var credentials = Credentials.with().username(username).password(password).build();
        var locale = "";
        var result = resource.login(locale, credentials);
        assertTrue(result.getSuccess());
        assertTrue(result.isCanModifyAlbum());
        assertNull(result.getOriginalRequestUri());
    }

    @Test
    void testLoginWithOriginalRequestUri() {
        var oldalbum = new OldAlbumServiceProvider();
        var originalRequestUri = "/oldalbum/slides/";
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var session = mock(HttpSession.class);
        var request = new MockHttpServletRequest();
        request.setSession(session);
        request.setMethod("GET");
        request.setRequestURL("http://localhost:8181" + originalRequestUri);
        request.setRequestURI(originalRequestUri);
        var response = new MockHttpServletResponse();

        var resource = new LoginResource();
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        var username = "admin";
        var password = "admin";
        createSubjectAndBindItToThread(request, response);
        var credentials = Credentials.with().username(username).password(password).build();

        var locale = "";
        var result = resource.login(locale, credentials);
        assertTrue(result.getSuccess());
        assertTrue(result.isCanModifyAlbum());
        assertEquals("/slides/", result.getOriginalRequestUri());
    }

    @Test
    void testLoginWithClearedOriginalRequestUri() {
        var oldalbum = new OldAlbumServiceProvider();
        var originalRequestUri = "/oldalbum/slides/";
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var session = mock(HttpSession.class);
        var request = new MockHttpServletRequest();
        request.setSession(session);
        request.setMethod("GET");
        request.setRequestURL("http://localhost:8181" + originalRequestUri);
        request.setRequestURI(originalRequestUri);
        var response = new MockHttpServletResponse();

        var resource = new LoginResource();
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        var username = "admin";
        var password = "admin";
        createSubjectAndBindItToThread(request, response);
        var credentials = Credentials.with().username(username).password(password).build();

        var clearRequestResult = resource.clearOriginalRequestUrl();
        assertFalse(clearRequestResult.getSuccess());
        assertFalse(clearRequestResult.isCanModifyAlbum());
        assertNull(clearRequestResult.getOriginalRequestUri());

        var locale = "";
        var result = resource.login(locale, credentials);
        assertTrue(result.getSuccess());
        assertTrue(result.isCanModifyAlbum());
        assertNull(result.getOriginalRequestUri());
    }

    @Test
    void testLoginModifyNotAllowed() {
        var oldalbum = new OldAlbumServiceProvider();
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var resource = new LoginResource();
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        var username = "jd";
        var password = "johnnyBoi";
        createSubjectAndBindItToThread();
        var credentials = Credentials.with().username(username).password(password).build();
        var locale = "";
        var result = resource.login(locale, credentials);
        assertTrue(result.getSuccess());
        assertFalse(result.isCanModifyAlbum());
    }

    @Test
    void testLoginWrongPassword() {
        var oldalbum = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var resource = new LoginResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        var username = "jd";
        var password = "feil";
        createSubjectAndBindItToThread();
        var credentials = Credentials.with().username(username).password(password).build();
        var locale = "en_GB";
        var result = resource.login(locale, credentials);
        assertFalse(result.getSuccess());
        assertThat(result.getErrormessage()).startsWith("Wrong password");
    }

    @Test
    void testLoginUknownUser() {
        var oldalbum = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var resource = new LoginResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        var username = "jdd";
        var password = "feil";
        createSubjectAndBindItToThread();
        var credentials = Credentials.with().username(username).password(password).build();
        var locale = "en_GB";
        var result = resource.login(locale, credentials);
        assertThat(result.getErrormessage()).startsWith("Unknown account");
    }

    @Test
    void testLoginLockedUser() {
        var oldalbum = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var resource = new LoginResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        var username = "lockeduser";
        var password = "lockpw";
        createSubjectAndBindItToThread();
        var credentials = Credentials.with().username(username).password(password).build();
        var locale = "en_GB";
        var result = resource.login(locale, credentials);
        assertThat(result.getErrormessage()).startsWith("Locked account");
    }

    @Test
    void testLoginGenericAuthenticationException() {
        var oldalbum = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var resource = new LoginResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        var username = "lockeduser";
        var password = "lockpw";
        var securityManager = mock(WebSecurityManager.class);
        var subject = mock(WebSubject.class);
        doThrow(AuthenticationException.class).when(subject).login(any());
        when(securityManager.createSubject(any())).thenReturn(subject);
        createSubjectAndBindItToThread(securityManager);
        var credentials = Credentials.with().username(username).password(password).build();
        var locale = "en_GB";
        var result = resource.login(locale, credentials);
        assertThat(result.getErrormessage()).startsWith("Unknown login error");
    }

    @Test
    void testLoginInternalServerError() {
        var oldalbum = new OldAlbumServiceProvider();
        var logservice = new MockLogService();
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var resource = new LoginResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        var username = "lockeduser";
        var password = "lockpw";
        var securityManager = mock(WebSecurityManager.class);
        var subject = mock(WebSubject.class);
        doThrow(RuntimeException.class).when(subject).login(any());
        when(securityManager.createSubject(any())).thenReturn(subject);
        createSubjectAndBindItToThread(securityManager);
        var credentials = Credentials.with().username(username).password(password).build();
        var locale = "";
        assertThrows(InternalServerErrorException.class, () -> {
                resource.login(locale, credentials);
            });
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).contains("Login error: internal server error");
    }

    @Test
    void testLogout() {
        var oldalbum = new OldAlbumServiceProvider();
        var useradmin = mock(UserManagementService.class);
        var oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        var resource = new LoginResource();
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        var username = "jd";
        var password = "johnnyBoi";
        createSubjectAndBindItToThread();
        var credentials = Credentials.with().username(username).password(password).build();
        var locale = "";
        var resultLogin = resource.login(locale, credentials);
        assertTrue(resultLogin.getSuccess());
        var resultLogout = resource.logout(credentials);
        assertFalse(resultLogout.getSuccess());
    }

}
