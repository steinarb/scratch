/*
 * Copyright 2018-2021 Steinar Bang
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
import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;

class NyHandlingTest {

    @Test
    void testAllValues() {
        String username = "jad";
        int accountid = 2;
        int storeId = 2;
        double belop = 42.0;
        Date now = new Date();
        NyHandling bean = NyHandling.with()
            .username(username)
            .accountid(accountid)
            .storeId(storeId)
            .belop(belop)
            .handletidspunkt(now)
            .build();
        assertEquals(username, bean.getUsername());
        assertEquals(accountid, bean.getAccountid());
        assertEquals(storeId, bean.getStoreId());
        assertEquals(belop, bean.getBelop(), 1.0);
        assertEquals(now, bean.getHandletidspunkt());
    }

    @Test
    void testNoArgsConstructor() {
        NyHandling bean = NyHandling.with().build();
        assertNull(bean.getUsername());
        assertEquals(-1, bean.getAccountid());
        assertEquals(-1, bean.getStoreId());
        assertEquals(0.0, bean.getBelop(), 1.0);
        assertNull(bean.getHandletidspunkt());
    }

    @Test
    void testToString() {
        NyHandling bean = NyHandling.with().build();
        assertThat(bean.toString()).startsWith("NyHandling [");
    }

}
