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
package no.priv.bang.oldalbum.roleadder.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserManagementService;

class ShiroRoleAdderForOldalbumTestEnvironmentTest {

    @Test
    void testActivate() {
        var useradmin = mock(UserManagementService.class);
        var roleadder = new ShiroRoleAdderForOldalbumTestEnvironment();
        roleadder.addUseradmin(useradmin);
        roleadder.activate(Collections.emptyMap());
        verify(useradmin, times(1)).getRoles();
        verify(useradmin, times(1)).getUser(anyString());
    }

    @Test
    void testActivateModifyNotAllowed() {
        var useradmin = mock(UserManagementService.class);
        var roleadder = new ShiroRoleAdderForOldalbumTestEnvironment();
        roleadder.addUseradmin(useradmin);
        var config = new HashMap<String, Object>();
        config.put("allowModify", "false");
        roleadder.activate(config );
        verify(useradmin, times(0)).getRoles();
        verify(useradmin, times(0)).getUser(anyString());
    }

    @Test
    void testActivateChangeAdminUsername() {
        var useradmin = mock(UserManagementService.class);
        when(useradmin.getUser(anyString())).thenThrow(AuthserviceException.class);
        var newuser = User.with().userid(2).username("imagemaster").email("admin@company.com").firstname("Ad").lastname("Min").build();
        when(useradmin.addUser(any())).thenReturn(Collections.singletonList(newuser));
        var roleadder = new ShiroRoleAdderForOldalbumTestEnvironment();
        roleadder.addUseradmin(useradmin);
        var config = new HashMap<String, Object>();
        config.put("username", "imagemaster");
        roleadder.activate(config );
        verify(useradmin, times(1)).getUser(anyString());
        verify(useradmin, times(1)).addUser(any());
    }

    @Test
    void testActivateChangeAdminPassword() {
        var useradmin = mock(UserManagementService.class);
        var admin = User.with().userid(0).username("admin").email("admin@admin.com").firstname("Admin").lastname("Istrator").build();
        when(useradmin.getUser("admin")).thenReturn(admin);
        var roleadder = new ShiroRoleAdderForOldalbumTestEnvironment();
        roleadder.addUseradmin(useradmin);
        var config = new HashMap<String, Object>();
        config.put("password", "zekret");
        roleadder.activate(config );
        verify(useradmin, times(1)).getUser(anyString());
        verify(useradmin, times(1)).updatePassword(any());
    }

    @Test
    void testAddOldalbumRoleWhenRoleDoesntExist() {
        var useradmin = mock(UserManagementService.class);
        var roleadder = new ShiroRoleAdderForOldalbumTestEnvironment();
        roleadder.addUseradmin(useradmin);
        var role = roleadder.addOldalbumadminRole();
        assertEquals("oldalbumadmin", role.rolename());
        assertEquals("Created by oldalbum.roleadder.test", role.description());
    }

    @Test
    void testAddOldalbumRoleWhenRoleAlreadyExist() {
        var useradmin = mock(UserManagementService.class);
        var existingRole = Role.with().id(0).rolename("oldalbumadmin").description("Already exists").build();
        when(useradmin.getRoles()).thenReturn(Arrays.asList(existingRole));
        var roleadder = new ShiroRoleAdderForOldalbumTestEnvironment();
        roleadder.addUseradmin(useradmin);
        var role = roleadder.addOldalbumadminRole();
        assertEquals("oldalbumadmin", role.rolename());
        assertEquals("Already exists", role.description());
    }

    @Test
    void testGiveUserAdminOldalbumRole() {
        var useradmin = mock(UserManagementService.class);
        var admin = User.with().userid(0).username("admin").email("admin@admin.com").firstname("Admin").lastname("Istrator").build();
        var roleadder = new ShiroRoleAdderForOldalbumTestEnvironment();
        roleadder.addUseradmin(useradmin);
        var role = roleadder.addOldalbumadminRole();
        var adminroles = roleadder.addRoleToAdmin(admin, role);
        assertNotNull(adminroles);
    }

    @Test
    void testGiveUserAdminOldalbumRoleWhenUserAdminNotPresent() {
        var useradmin = mock(UserManagementService.class);
        var roleadder = new ShiroRoleAdderForOldalbumTestEnvironment();
        roleadder.addUseradmin(useradmin);
        var role = roleadder.addOldalbumadminRole();
        var adminroles = roleadder.addRoleToAdmin(null, role);
        assertNull(adminroles);
    }

}
