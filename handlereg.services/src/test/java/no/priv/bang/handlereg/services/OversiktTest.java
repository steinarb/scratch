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

import org.junit.jupiter.api.Test;

class OversiktTest {

    @Test
    void testAllValues() {
        Oversikt bean = Oversikt.with()
            .accountid(1)
            .brukernavn("jad")
            .email("janedoe21@gmail.com")
            .fornavn("Jane")
            .etternavn("Doe")
            .balanse(1041)
            .sumPreviousMonth(8900)
            .sumThisMonth(310)
            .build();
        assertEquals(1, bean.getAccountid());
        assertEquals("jad", bean.getBrukernavn());
        assertEquals("janedoe21@gmail.com", bean.getEmail());
        assertEquals("Jane", bean.getFornavn());
        assertEquals("Doe", bean.getEtternavn());
        assertEquals(1041.0, bean.getBalanse(), 0.1);
        assertEquals(8900, bean.getSumPreviousMonth());
        assertEquals(310, bean.getSumThisMonth());
        assertThat(bean.toString()).startsWith("Oversikt [");
    }

    @Test
    void testAllValuesNoargsConstructor() {
        Oversikt bean = Oversikt.with().build();
        assertEquals(-1, bean.getAccountid());
        assertNull(bean.getBrukernavn());
        assertNull(bean.getFornavn());
        assertNull(bean.getEtternavn());
        assertEquals(0.0, bean.getBalanse(), 0.1);
    }

}
