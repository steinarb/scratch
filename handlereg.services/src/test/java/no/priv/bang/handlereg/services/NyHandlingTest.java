/*
 * Copyright 2018-2024 Steinar Bang
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
package no.priv.bang.handlereg.services;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

import org.junit.jupiter.api.Test;

class NyHandlingTest {

    @Test
    void testAllValues() {
        var username = "jad";
        var accountid = 2;
        var storeId = 2;
        var belop = 42.0;
        var now = new Date();
        var bean = NyHandling.with()
            .username(username)
            .accountid(accountid)
            .storeId(storeId)
            .belop(belop)
            .handletidspunkt(now)
            .build();
        assertEquals(username, bean.username());
        assertEquals(accountid, bean.accountid());
        assertEquals(storeId, bean.storeId());
        assertEquals(belop, bean.belop(), 1.0);
        assertEquals(now, bean.handletidspunkt());
    }

    @Test
    void testNoArgsConstructor() {
        var bean = NyHandling.with().build();
        assertNull(bean.username());
        assertEquals(-1, bean.accountid());
        assertEquals(-1, bean.storeId());
        assertEquals(0.0, bean.belop(), 1.0);
        assertNull(bean.handletidspunkt());
    }

}
