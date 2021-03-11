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

import org.junit.jupiter.api.Test;

class UserTest {

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
        assertEquals(userid, user.getUserid());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(firstname, user.getFirstname());
        assertEquals(lastname, user.getLastname());
    }

    @Test
    void testCopy() {
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
        User copy = User.with(user).build();
        assertEquals(userid, copy.getUserid());
        assertEquals(username, copy.getUsername());
        assertEquals(email, copy.getEmail());
        assertEquals(firstname, copy.getFirstname());
        assertEquals(lastname, copy.getLastname());
    }

    @Test
    void testCreateNoargs() {
        User user = User.with().build();
        assertEquals(-1, user.getUserid());
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getFirstname());
        assertNull(user.getLastname());
    }

}
