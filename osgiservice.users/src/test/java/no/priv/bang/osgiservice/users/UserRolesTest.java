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

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class UserRolesTest {

    @Test
    void testCreate() {
        int userid = 42;
        String username = "jdoe";
        String email = "jdoe31@gmail.com";
        String firstname = "John";
        String lastname = "Doe";
        User user = User.with()
            .userid(userid)
            .username(username)
            .email(email)
            .firstname(firstname)
            .lastname(lastname)
            .build();
        int id = 42;
        String rolename = "admin";
        String description = "This is an administrator";
        Role role = Role.with().id(id).rolename(rolename).description(description).build();
        List<Role> roles = Arrays.asList(role);
        UserRoles userroles = UserRoles.with().user(user).roles(roles).build();

        assertEquals(user, userroles.getUser());
        assertEquals(role, userroles.getRoles().get(0));
    }

    @Test
    void testNoargsConstructor() {
        UserRoles userroles = UserRoles.with().build();

        assertNull(userroles.getUser());
        assertNull(userroles.getRoles());
    }

}
