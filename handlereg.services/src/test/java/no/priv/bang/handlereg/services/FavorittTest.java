/*
 * Copyright 2021-2024 Steinar Bang
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FavorittTest {

    @Test
    void test() {
        var store = Butikk.with().storeId(13).build();
        var bean = Favoritt.with()
            .favouriteid(42)
            .accountid(3)
            .store(store)
            .rekkefolge(10)
            .build();
        assertEquals(42, bean.favouriteid());
        assertEquals(3, bean.accountid());
        assertEquals(store, bean.store());
        assertEquals(10, bean.rekkefolge());
    }

    @Test
    void testNoArgs() {
        var bean = Favoritt.with().build();
        assertEquals(-1, bean.favouriteid());
        assertEquals(-1, bean.accountid());
        assertNull(bean.store());
        assertEquals(-1, bean.rekkefolge());
    }

    @Test
    void testToString() {
        var bean = Favoritt.with().build();
        assertThat(bean.toString()).startsWith("Favoritt[");
    }

}
