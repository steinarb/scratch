/*
 * Copyright 2018 Steinar Bang
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
package no.bang.priv.handlereg.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OversiktTest {

    @Test
    void testAllValues() {
        Oversikt bean = new Oversikt(1, "jad", "janedoe21@gmail.com", "Jane", "Doe", 1041);
        assertEquals(1, bean.getAccountid());
        assertEquals("jad", bean.getBrukernavn());
        assertEquals("janedoe21@gmail.com", bean.getEmail());
        assertEquals("Jane", bean.getFornavn());
        assertEquals("Doe", bean.getEtternavn());
        assertEquals(1041.0, bean.getBalanse(), 0.1);
        assertThat(bean.toString()).startsWith("Oversikt [");
    }

    @Test
    void testAllValuesNoargsConstructor() {
        Oversikt bean = new Oversikt();
        assertEquals(-1, bean.getAccountid());
        assertNull(bean.getBrukernavn());
        assertNull(bean.getFornavn());
        assertNull(bean.getEtternavn());
        assertEquals(0.0, bean.getBalanse(), 0.1);
    }

}
