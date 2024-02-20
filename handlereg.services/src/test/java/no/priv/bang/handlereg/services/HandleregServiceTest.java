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
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;


class HandleregServiceTest {

    @Test
    void testOfAllOfTheMethods() {
        var service = mock(HandleregService.class);
        var brukernavn = "jad";
        var oversikt =  service.finnOversikt(brukernavn);
        assertNull(oversikt);
        int userId = 2;
        var transactions = service.findLastTransactions(userId);
        assertEquals(0, transactions.size());
        var handling = NyHandling.with().build();
        var nyoversikt = service.registrerHandling(handling);
        assertNull(nyoversikt);
        var butikker = service.finnButikker();
        assertEquals(0, butikker.size());
        var nybutikk = Butikk.with().build();
        var oppdaterteButikker = service.leggTilButikk(nybutikk);
        assertEquals(0, oppdaterteButikker.size());
        var endredeButikker = service.endreButikk(nybutikk);
        assertEquals(0, endredeButikker.size());
        var sumPrButikk = service.sumOverButikk();
        assertEquals(0, sumPrButikk.size());
        var antallHandlerIButikk = service.antallHandlingerIButikk();
        assertEquals(0, antallHandlerIButikk.size());
        var sisteHandelIButikk = service.sisteHandelIButikk();
        assertEquals(0, sisteHandelIButikk.size());
        var totaltHandlebelopPrAar = service.totaltHandlebelopPrAar();
        assertEquals(0, totaltHandlebelopPrAar.size());
        var totaltHandlebelopPrAarOgMaaned = service.totaltHandlebelopPrAarOgMaaned();
        assertEquals(0, totaltHandlebelopPrAarOgMaaned.size());
        var favoritter = service.finnFavoritter(null);
        assertEquals(0, favoritter.size());
        var nyFavoritt = NyFavoritt.with().brukernavn("jd").build();
        var enMerFavoritt = service.leggTilFavoritt(nyFavoritt);
        assertEquals(0, enMerFavoritt.size());
        var skalSlettes = Favoritt.with().build();
        var favoritterMinusSlettet = service.slettFavoritt(skalSlettes);
        assertEquals(0, favoritterMinusSlettet.size());
        var parSomSkalBytteRekkfolge = Favorittpar.with().build();
        var favoritterByttet = service.byttRekkefolge(parSomSkalBytteRekkfolge);
        assertEquals(0, favoritterByttet.size());
    }

}
