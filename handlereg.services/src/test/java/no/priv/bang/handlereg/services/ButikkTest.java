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

import org.junit.jupiter.api.Test;

class ButikkTest {

    @Test
    void testAllValues() {
        int storeId = 1;
        String storeName = "Spar fjelheimen";
        int gruppe = 2;
        int rekkefolge = 140;
        Butikk bean = Butikk.with().storeId(storeId).butikknavn(storeName).gruppe(gruppe).rekkefolge(rekkefolge).build();
        assertEquals(storeId, bean.getStoreId());
        assertEquals(storeName, bean.getButikknavn());
        assertEquals(gruppe, bean.getGruppe());
        assertEquals(rekkefolge, bean.getRekkefolge());
    }

    @Test
    void testCopy() {
        int storeId = 1;
        String storeName = "Spar fjelheimen";
        int gruppe = 2;
        int rekkefolge = 140;
        Butikk bean = Butikk.with().storeId(storeId).butikknavn(storeName).gruppe(gruppe).rekkefolge(rekkefolge).build();
        Butikk copy = Butikk.with(bean).build();
        assertEquals(storeId, copy.getStoreId());
        assertEquals(storeName, copy.getButikknavn());
        assertEquals(gruppe, copy.getGruppe());
        assertEquals(rekkefolge, copy.getRekkefolge());
    }

    @Test
    void testConstructorWithoutId() {
        String storeName = "Spar fjelheimen";
        int gruppe = 2;
        int rekkefolge = 140;
        Butikk bean = Butikk.with().butikknavn(storeName).gruppe(gruppe).rekkefolge(rekkefolge).build();
        assertEquals(storeName, bean.getButikknavn());
        assertEquals(gruppe, bean.getGruppe());
        assertEquals(rekkefolge, bean.getRekkefolge());
    }

    @Test
    void testNameOnlyConstructor() {
        String storeName = "Spar fjelheimen";
        Butikk bean = Butikk.with().butikknavn(storeName).build();
        assertEquals(storeName, bean.getButikknavn());
    }

    @Test
    void testNoArgsConstructor() {
        Butikk bean = Butikk.with().build();
        assertEquals(-1, bean.getStoreId());
        assertNull(bean.getButikknavn());
        assertEquals(0, bean.getGruppe());
        assertEquals(0, bean.getRekkefolge());
    }

}
