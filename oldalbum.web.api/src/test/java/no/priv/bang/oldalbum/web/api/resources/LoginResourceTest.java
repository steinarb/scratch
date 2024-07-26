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

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.jupiter.api.Test;

import no.priv.bang.oldalbum.backend.OldAlbumServiceProvider;
import no.priv.bang.oldalbum.services.bean.Credentials;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.UserManagementService;

class LoginResourceTest extends no.priv.bang.oldalbum.testutilities.ShiroTestBase {

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
        var subject = SecurityUtils.getSubject();
        var token = new UsernamePasswordToken(username, password.toCharArray(), true);
        subject.login(token);

        var result = resource.loginCheck();
        assertTrue(result.success());
        assertTrue(result.canModifyAlbum());
        assertTrue(result.canLogin());
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
        assertFalse(result.success());
        assertFalse(result.canModifyAlbum());
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
        assertFalse(result.success());
        assertFalse(result.canModifyAlbum());
        assertFalse(result.canLogin());
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
        var subject = SecurityUtils.getSubject();
        var token = new UsernamePasswordToken(username, password.toCharArray(), true);
        subject.login(token);
        var resultLogout = resource.logout(credentials);
        assertFalse(resultLogout.success());
    }

}
