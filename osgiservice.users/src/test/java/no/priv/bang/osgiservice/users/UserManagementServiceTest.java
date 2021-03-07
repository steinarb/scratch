/*
 * Copyright 2019-2021 Steinar Bang
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
package no.priv.bang.osgiservice.users;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class UserManagementServiceTest {

    @Test
    void testServiceDefinition() throws Exception {
        UserManagementService service = mock(UserManagementService.class);
        when(service.getUsers()).thenReturn(Arrays.asList(User.with().userid(42).username("jdoe").email("jdoe42@gmail.com").firstname("John").lastname("Doe").build()));
        List<User> users = service.getUsers();
        User user = users.get(0);
        List<User> updatedUsers = service.modifyUser(user);
        assertEquals(0, updatedUsers.size());
        UserAndPasswords userPasswords = UserAndPasswords.with().build();
        List<User> usersAfterPasswordUpdate = service.updatePassword(userPasswords);
        assertEquals(0, usersAfterPasswordUpdate.size());
        User newUser = User.with().userid(-1).username("nuser").email("nuser@gmail.com").firstname("New").lastname("User").build();
        UserAndPasswords newUserWithPasswords = UserAndPasswords.with()
            .user(newUser)
            .password1("secret")
            .password2("secret")
            .passwordsNotIdentical(false)
            .build();
        List<User> usersAfterUserCreate = service.addUser(newUserWithPasswords);
        assertEquals(0, usersAfterUserCreate.size());

        String username = "jad";
        User otherUser = service.getUser(username);
        assertNull(otherUser);
        List<Role> rolesForOtherUser = service.getRolesForUser(username);
        assertEquals(0, rolesForOtherUser.size());
        List<Permission> permissionsForOtherUser = service.getPermissionsForUser(username);
        assertEquals(0, permissionsForOtherUser.size());

        List<Role> roles = service.getRoles();
        assertEquals(0, roles.size());
        Role role = Role.with().build();
        List<Role> rolesAfterModification = service.modifyRole(role);
        assertEquals(0, rolesAfterModification.size());
        Role newRole = Role.with().build();
        List<Role> rolesAfterAddition = service.addRole(newRole);
        assertEquals(0, rolesAfterAddition.size());

        List<Permission> permissions = service.getPermissions();
        assertEquals(0, permissions.size());
        Permission permission = Permission.with().build();
        List<Permission> permissionsAfterModification = service.modifyPermission(permission);
        assertEquals(0, permissionsAfterModification.size());
        Permission newPermission = Permission.with().build();
        List<Permission> permissionsAfterAddition = service.addPermission(newPermission);
        assertEquals(0, permissionsAfterAddition.size());

        Map<String, List<Role>> userRoles = service.getUserRoles();
        assertEquals(0, userRoles.size());
        UserRoles userroles = UserRoles.with().user(user).roles(roles).build();
        Map<String, List<Role>> userRolesAfterAdd = service.addUserRoles(userroles);
        assertEquals(0, userRolesAfterAdd.size());
        Map<String, List<Role>> userRolesAfterRemove = service.removeUserRoles(userroles);
        assertEquals(0, userRolesAfterRemove.size());

        Map<String, List<Permission>> rolesPermissions = service.getRolesPermissions();
        assertEquals(0, rolesPermissions.size());
        RolePermissions rolepermissions = RolePermissions.with().role(role).permissions(permissions).build();
        Map<String, List<Permission>> rolesPermissionsAfterAdd = service.addRolePermissions(rolepermissions);
        assertEquals(0, rolesPermissionsAfterAdd.size());
        Map<String, List<Permission>> rolesPermissionsAfterRemove = service.removeRolePermissions(rolepermissions);
        assertEquals(0, rolesPermissionsAfterRemove.size());
    }

}
