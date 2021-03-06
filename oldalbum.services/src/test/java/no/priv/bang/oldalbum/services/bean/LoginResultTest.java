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
package no.priv.bang.oldalbum.services.bean;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LoginResultTest {

    @Test
    void testCreate() {
        boolean success = true;
        String username = "admin";
        String errormessage = "Wrong password";
        boolean canModifyAlbum = true;
        boolean canLogin = true;
        LoginResult bean = LoginResult.with()
            .success(success)
            .username(username)
            .errormessage(errormessage)
            .canModifyAlbum(canModifyAlbum)
            .canLogin(canLogin)
            .build();
        assertTrue(bean.getSuccess());
        assertEquals(username, bean.getUsername());
        assertEquals(errormessage, bean.getErrormessage());
        assertTrue(bean.isCanModifyAlbum());
        assertTrue(bean.isCanLogin());
    }

    @Test
    void testNoargsConstructor() {
        LoginResult bean = LoginResult.with().build();
        assertFalse(bean.getSuccess());
        assertNull(bean.getUsername());
        assertNull(bean.getErrormessage());
        assertFalse(bean.isCanModifyAlbum());
        assertFalse(bean.isCanLogin());
    }

}
