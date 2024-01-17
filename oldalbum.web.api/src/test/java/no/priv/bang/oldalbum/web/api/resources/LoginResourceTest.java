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
import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.Credentials;
import no.priv.bang.oldalbum.services.bean.LoginResult;
import no.priv.bang.oldalbum.web.api.ShiroTestBase;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.UserManagementService;

class LoginResourceTest extends ShiroTestBase {

    @Test
    void testLoginCheck() {
        OldAlbumService oldalbum = new OldAlbumServiceProvider();
        UserManagementService useradmin = mock(UserManagementService.class);
        Role oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        LoginResource resource = new LoginResource();
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        String username = "admin";
        String password = "admin";
        createSubjectAndBindItToThread();
        Credentials credentials = Credentials.with().username(username).password(password).build();
        String locale = "";
        resource.login(locale, credentials); // Ensure user is logged in

        LoginResult result = resource.loginCheck();
        assertTrue(result.getSuccess());
        assertTrue(result.isCanModifyAlbum());
        assertTrue(result.isCanLogin());
    }

    @Test
    void testLoginCheckWhenNotLoggedIn() {
        OldAlbumService oldalbum = new OldAlbumServiceProvider();
        UserManagementService useradmin = mock(UserManagementService.class);
        Role oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        LoginResource resource = new LoginResource();
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        createSubjectAndBindItToThread();

        LoginResult result = resource.loginCheck();
        assertFalse(result.getSuccess());
        assertFalse(result.isCanModifyAlbum());
    }

    @Test
    void testLoginCheckWhenNotLoggedInAndOldalbumadminRoleNotPresent() {
        OldAlbumService oldalbum = new OldAlbumServiceProvider();
        UserManagementService useradmin = mock(UserManagementService.class);
        LoginResource resource = new LoginResource();
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        createSubjectAndBindItToThread();

        LoginResult result = resource.loginCheck();
        assertFalse(result.getSuccess());
        assertFalse(result.isCanModifyAlbum());
        assertFalse(result.isCanLogin());
    }

    @Test
    void testLogin() {
        OldAlbumService oldalbum = new OldAlbumServiceProvider();
        UserManagementService useradmin = mock(UserManagementService.class);
        Role oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        LoginResource resource = new LoginResource();
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        String username = "admin";
        String password = "admin";
        createSubjectAndBindItToThread();
        Credentials credentials = Credentials.with().username(username).password(password).build();
        String locale = "";
        LoginResult result = resource.login(locale, credentials);
        assertTrue(result.getSuccess());
        assertTrue(result.isCanModifyAlbum());
        assertNull(result.getOriginalRequestUri());
    }

    @Test
    void testLoginWithOriginalRequestUri() {
        OldAlbumService oldalbum = new OldAlbumServiceProvider();
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

        String locale = "";
        var result = resource.login(locale, credentials);
        assertTrue(result.getSuccess());
        assertTrue(result.isCanModifyAlbum());
        assertEquals("/slides/", result.getOriginalRequestUri());
    }

    @Test
    void testLoginModifyNotAllowed() {
        OldAlbumService oldalbum = new OldAlbumServiceProvider();
        UserManagementService useradmin = mock(UserManagementService.class);
        Role oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        LoginResource resource = new LoginResource();
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        String username = "jd";
        String password = "johnnyBoi";
        createSubjectAndBindItToThread();
        Credentials credentials = Credentials.with().username(username).password(password).build();
        String locale = "";
        LoginResult result = resource.login(locale, credentials);
        assertTrue(result.getSuccess());
        assertFalse(result.isCanModifyAlbum());
    }

    @Test
    void testLoginWrongPassword() {
        OldAlbumService oldalbum = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        Role oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        LoginResource resource = new LoginResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        String username = "jd";
        String password = "feil";
        createSubjectAndBindItToThread();
        Credentials credentials = Credentials.with().username(username).password(password).build();
        String locale = "en_GB";
        LoginResult result = resource.login(locale, credentials);
        assertFalse(result.getSuccess());
        assertThat(result.getErrormessage()).startsWith("Wrong password");
    }

    @Test
    void testLoginUknownUser() {
        OldAlbumService oldalbum = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        Role oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        LoginResource resource = new LoginResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        String username = "jdd";
        String password = "feil";
        createSubjectAndBindItToThread();
        Credentials credentials = Credentials.with().username(username).password(password).build();
        String locale = "en_GB";
        LoginResult result = resource.login(locale, credentials);
        assertThat(result.getErrormessage()).startsWith("Unknown account");
    }

    @Test
    void testLoginLockedUser() {
        OldAlbumService oldalbum = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        Role oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        LoginResource resource = new LoginResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        String username = "lockeduser";
        String password = "lockpw";
        createSubjectAndBindItToThread();
        Credentials credentials = Credentials.with().username(username).password(password).build();
        String locale = "en_GB";
        LoginResult result = resource.login(locale, credentials);
        assertThat(result.getErrormessage()).startsWith("Locked account");
    }

    @Test
    void testLoginGenericAuthenticationException() {
        OldAlbumService oldalbum = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        Role oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        LoginResource resource = new LoginResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        String username = "lockeduser";
        String password = "lockpw";
        WebSecurityManager securityManager = mock(WebSecurityManager.class);
        WebSubject subject = mock(WebSubject.class);
        doThrow(AuthenticationException.class).when(subject).login(any());
        when(securityManager.createSubject(any())).thenReturn(subject);
        createSubjectAndBindItToThread(securityManager);
        Credentials credentials = Credentials.with().username(username).password(password).build();
        String locale = "en_GB";
        LoginResult result = resource.login(locale, credentials);
        assertThat(result.getErrormessage()).startsWith("Unknown login error");
    }

    @Test
    void testLoginInternalServerError() {
        OldAlbumService oldalbum = new OldAlbumServiceProvider();
        MockLogService logservice = new MockLogService();
        UserManagementService useradmin = mock(UserManagementService.class);
        Role oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        LoginResource resource = new LoginResource();
        resource.setLogservice(logservice);
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        String username = "lockeduser";
        String password = "lockpw";
        WebSecurityManager securityManager = mock(WebSecurityManager.class);
        WebSubject subject = mock(WebSubject.class);
        doThrow(RuntimeException.class).when(subject).login(any());
        when(securityManager.createSubject(any())).thenReturn(subject);
        createSubjectAndBindItToThread(securityManager);
        Credentials credentials = Credentials.with().username(username).password(password).build();
        String locale = "";
        assertThrows(InternalServerErrorException.class, () -> {
                resource.login(locale, credentials);
            });
        assertThat(logservice.getLogmessages()).isNotEmpty();
        assertThat(logservice.getLogmessages().get(0)).contains("Login error: internal server error");
    }

    @Test
    void testLogout() {
        OldAlbumService oldalbum = new OldAlbumServiceProvider();
        UserManagementService useradmin = mock(UserManagementService.class);
        Role oldalbumadmin = Role.with().id(7).rolename("oldalbumadmin").description("Modify albums").build();
        when(useradmin.getRoles()).thenReturn(Collections.singletonList(oldalbumadmin));
        LoginResource resource = new LoginResource();
        resource.useradmin = useradmin;
        resource.oldalbum = oldalbum;
        String username = "jd";
        String password = "johnnyBoi";
        createSubjectAndBindItToThread();
        Credentials credentials = Credentials.with().username(username).password(password).build();
        String locale = "";
        LoginResult resultLogin = resource.login(locale, credentials);
        assertTrue(resultLogin.getSuccess());
        LoginResult resultLogout = resource.logout(credentials);
        assertFalse(resultLogout.getSuccess());
    }

}
