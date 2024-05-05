/*
 * Copyright 2019-2024 Steinar Bang
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

class LoginresultatTest {

    @Test
    void testCreate() {
        var suksess = true;
        var authorized = true;
        var feilmelding = "Feil passord";
        var originalRequestUrl = "http://localhost:8181/handlereg/hurtigregistrering";
        var brukernavn = "jad";
        var bean = Loginresultat.with()
            .brukernavn(brukernavn)
            .suksess(suksess)
            .feilmelding(feilmelding)
            .authorized(authorized)
            .originalRequestUrl(originalRequestUrl)
            .build();
        assertTrue(bean.suksess());
        assertEquals(feilmelding, bean.feilmelding());
        assertTrue(bean.authorized());
        assertEquals(originalRequestUrl, bean.originalRequestUrl());
        assertEquals(brukernavn, bean.brukernavn());
    }

    @Test
    void testNoargsConstructor() {
        var bean = Loginresultat.with().build();
        assertFalse(bean.suksess());
        assertNull(bean.feilmelding());
        assertFalse(bean.authorized());
        assertNull(bean.originalRequestUrl());
    }

}
