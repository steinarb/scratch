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
package no.priv.bang.oldalbum.roleadder.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

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
        roleadder.activate();
        verify(useradmin, times(1)).getRoles();
        verify(useradmin, times(1)).getUser(anyString());
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
        Role existingRole = new Role(0, "oldalbumadmin", "Already exists");
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
        User admin = new User(0, "admin", "admin@admin.com", "Admin", "Istrator");
        when(useradmin.getUser(anyString())).thenReturn(admin);
        ShiroRoleAdderForOldalbumTestEnvironment roleadder = new ShiroRoleAdderForOldalbumTestEnvironment();
        roleadder.addUseradmin(useradmin);
        Role role = roleadder.addOldalbumadminRole();
        UserRoles adminroles = roleadder.addRoleToAdmin(role);
        assertNotNull(adminroles);
    }

    @Test
    void testGiveUserAdminOldalbumRoleWhenUserAdminNotPresent() {
        UserManagementService useradmin = mock(UserManagementService.class);
        ShiroRoleAdderForOldalbumTestEnvironment roleadder = new ShiroRoleAdderForOldalbumTestEnvironment();
        roleadder.addUseradmin(useradmin);
        Role role = roleadder.addOldalbumadminRole();
        UserRoles adminroles = roleadder.addRoleToAdmin(role);
        assertNull(adminroles);
    }

}
