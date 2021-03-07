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

class UserAndPasswordsTest {

    @Test
    void test() {
        User user = User.with()
            .userid(42)
            .username("jdoe")
            .email("ldoe365@gmail.com")
            .firstname("John")
            .lastname("Doe")
            .build();
        String password1 = "secret";
        String password2 = "secret";
        boolean passwordsNotIdentical = true;
        UserAndPasswords userPasswords = UserAndPasswords.with()
            .user(user)
            .password1(password1)
            .password2(password2)
            .passwordsNotIdentical(passwordsNotIdentical)
            .build();
        assertEquals(user.getUserid(), userPasswords.getUser().getUserid());
        assertEquals(password1, userPasswords.getPassword1());
        assertEquals(password2, userPasswords.getPassword2());
        assertEquals(passwordsNotIdentical, userPasswords.isPasswordsNotIdentical());
    }

    @Test
    void testNoargsConstructor() {
        UserAndPasswords userPasswords = UserAndPasswords.with().build();
        assertNull(userPasswords.getUser());
        assertNull(userPasswords.getPassword1());
        assertNull(userPasswords.getPassword2());
        assertFalse(userPasswords.isPasswordsNotIdentical());
    }

}
