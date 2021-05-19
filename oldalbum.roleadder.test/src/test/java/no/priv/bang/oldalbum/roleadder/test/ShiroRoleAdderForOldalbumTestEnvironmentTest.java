/*
 * Copyright 2020-2021 Steinar Bang
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
import java.util.Map;

import org.junit.jupiter.api.Test;

import no.priv.bang.authservice.definitions.AuthserviceException;
import no.priv.bang.osgiservice.users.Role;
import no.priv.bang.osgiservice.users.User;
import no.priv.bang.osgiservice.users.UserManagementService;
import no.priv.bang.osgiservice.users.UserRoles;

class ShiroRoleAdderForOldalbumTestEnvironmentTest {

    @Test
    void testActivate() {
        UserManagementService useradmin = mock(UserManagementService.class);
        ShiroRoleAdderForOldalbumTestEnvironment roleadder = new ShiroRoleAdderForOldalbumTestEnvironment();
        roleadder.addUseradmin(useradmin);
        roleadder.activate(Collections.emptyMap());
        verify(useradmin, times(1)).getRoles();
        verify(useradmin, times(1)).getUser(anyString());
    }

    @Test
    void testActivateModifyNotAllowed() {
        UserManagementService useradmin = mock(UserManagementService.class);
        ShiroRoleAdderForOldalbumTestEnvironment roleadder = new ShiroRoleAdderForOldalbumTestEnvironment();
        roleadder.addUseradmin(useradmin);
        Map<String, Object> config = new HashMap<>();
        config.put("allowModify", "false");
        roleadder.activate(config );
        verify(useradmin, times(0)).getRoles();
        verify(useradmin, times(0)).getUser(anyString());
    }

    @Test
    void testActivateChangeAdminUsername() {
        UserManagementService useradmin = mock(UserManagementService.class);
        when(useradmin.getUser(anyString())).thenThrow(AuthserviceException.class);
        User newuser = User.with().userid(2).username("imagemaster").email("admin@company.com").firstname("Ad").lastname("Min").build();
        when(useradmin.addUser(any())).thenReturn(Collections.singletonList(newuser));
        ShiroRoleAdderForOldalbumTestEnvironment roleadder = new ShiroRoleAdderForOldalbumTestEnvironment();
        roleadder.addUseradmin(useradmin);
        Map<String, Object> config = new HashMap<>();
        config.put("username", "imagemaster");
        roleadder.activate(config );
        verify(useradmin, times(1)).getUser(anyString());
        verify(useradmin, times(1)).addUser(any());
    }

    @Test
    void testActivateChangeAdminPassword() {
        UserManagementService useradmin = mock(UserManagementService.class);
        User admin = User.with().userid(0).username("admin").email("admin@admin.com").firstname("Admin").lastname("Istrator").build();
        when(useradmin.getUser("admin")).thenReturn(admin);
        ShiroRoleAdderForOldalbumTestEnvironment roleadder = new ShiroRoleAdderForOldalbumTestEnvironment();
        roleadder.addUseradmin(useradmin);
        Map<String, Object> config = new HashMap<>();
        config.put("password", "zekret");
        roleadder.activate(config );
        verify(useradmin, times(1)).getUser(anyString());
        verify(useradmin, times(1)).updatePassword(any());
    }

    @Test
    void testAddOldalbumRoleWhenRoleDoesntExist() {
        UserManagementService useradmin = mock(UserManagementService.class);
        ShiroRoleAdderForOldalbumTestEnvironment roleadder = new ShiroRoleAdderForOldalbumTestEnvironment();
        roleadder.addUseradmin(useradmin);
        Role role = roleadder.addOldalbumadminRole();
        assertEquals("oldalbumadmin", role.getRolename());
        assertEquals("Created by oldalbum.roleadder.test", role.getDescription());
    }

    @Test
    void testAddOldalbumRoleWhenRoleAlreadyExist() {
        UserManagementService useradmin = mock(UserManagementService.class);
        Role existingRole = Role.with().id(0).rolename("oldalbumadmin").description("Already exists").build();
        when(useradmin.getRoles()).thenReturn(Arrays.asList(existingRole));
        ShiroRoleAdderForOldalbumTestEnvironment roleadder = new ShiroRoleAdderForOldalbumTestEnvironment();
        roleadder.addUseradmin(useradmin);
        Role role = roleadder.addOldalbumadminRole();
        assertEquals("oldalbumadmin", role.getRolename());
        assertEquals("Already exists", role.getDescription());
    }

    @Test
    void testGiveUserAdminOldalbumRole() {
        UserManagementService useradmin = mock(UserManagementService.class);
        User admin = User.with().userid(0).username("admin").email("admin@admin.com").firstname("Admin").lastname("Istrator").build();
        ShiroRoleAdderForOldalbumTestEnvironment roleadder = new ShiroRoleAdderForOldalbumTestEnvironment();
        roleadder.addUseradmin(useradmin);
        Role role = roleadder.addOldalbumadminRole();
        UserRoles adminroles = roleadder.addRoleToAdmin(admin, role);
        assertNotNull(adminroles);
    }

    @Test
    void testGiveUserAdminOldalbumRoleWhenUserAdminNotPresent() {
        UserManagementService useradmin = mock(UserManagementService.class);
        ShiroRoleAdderForOldalbumTestEnvironment roleadder = new ShiroRoleAdderForOldalbumTestEnvironment();
        roleadder.addUseradmin(useradmin);
        Role role = roleadder.addOldalbumadminRole();
        UserRoles adminroles = roleadder.addRoleToAdmin(null, role);
        assertNull(adminroles);
    }

}
