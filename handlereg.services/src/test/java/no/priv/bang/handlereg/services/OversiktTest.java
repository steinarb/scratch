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
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OversiktTest {

    @Test
    void testAllValues() {
        var bean = Oversikt.with()
            .accountid(1)
            .brukernavn("jad")
            .email("janedoe21@gmail.com")
            .fornavn("Jane")
            .etternavn("Doe")
            .balanse(1041)
            .sumPreviousMonth(8900)
            .sumThisMonth(310)
            .lastTransactionAmount(92.0)
            .lastTransactionStore(1)
            .build();
        assertEquals(1, bean.accountid());
        assertEquals("jad", bean.brukernavn());
        assertEquals("janedoe21@gmail.com", bean.email());
        assertEquals("Jane", bean.fornavn());
        assertEquals("Doe", bean.etternavn());
        assertEquals(1041.0, bean.balanse(), 0.1);
        assertEquals(8900, bean.sumPreviousMonth());
        assertEquals(310, bean.sumThisMonth());
        assertThat(bean.toString()).startsWith("Oversikt[");
    }

    @Test
    void testAllValuesNoargsConstructor() {
        var bean = Oversikt.with().build();
        assertEquals(-1, bean.accountid());
        assertNull(bean.brukernavn());
        assertNull(bean.fornavn());
        assertNull(bean.brukernavn());
        assertEquals(0.0, bean.balanse(), 0.1);
    }

}
