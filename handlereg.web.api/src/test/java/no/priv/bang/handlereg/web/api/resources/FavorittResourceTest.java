/*
 * Copyright 2021 Steinar Bang
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
package no.priv.bang.handlereg.web.api.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import no.priv.bang.handlereg.services.Butikk;
import no.priv.bang.handlereg.services.Favoritt;
import no.priv.bang.handlereg.services.Favorittpar;
import no.priv.bang.handlereg.services.HandleregService;
import no.priv.bang.handlereg.services.NyFavoritt;

class FavorittResourceTest {

    @Test
    void testGetFavoritter() {
        HandleregService handlereg = mock(HandleregService.class);
        Favoritt favoritt1 = Favoritt.with().favouriteid(1).accountid(1).build();
        Favoritt favoritt2 = Favoritt.with().favouriteid(2).accountid(1).build();
        when(handlereg.finnFavoritter("jod")).thenReturn(Arrays.asList(favoritt1, favoritt2));
        FavorittResource resource = new FavorittResource();
        resource.handlereg = handlereg;

        String username = "jod";
        List<Favoritt> favoritter = resource.getFavoritter(username);
        assertThat(favoritter.size()).isPositive();
    }

    @Test
    void testLeggTilFavoritt() {
        HandleregService handlereg = mock(HandleregService.class);
        Favoritt favoritt1 = Favoritt.with().favouriteid(1).accountid(1).build();
        Butikk butikk = Butikk.with().storeId(1).butikknavn("Joker Fjellstu").build();
        Favoritt favoritt2 = Favoritt.with().favouriteid(2).accountid(1).store(butikk).build();
        when(handlereg.leggTilFavoritt(any())).thenReturn(Arrays.asList(favoritt1, favoritt2));
        FavorittResource resource = new FavorittResource();
        resource.handlereg = handlereg;

        String username = "jod";
        NyFavoritt nyFavoritt = NyFavoritt.with().brukernavn(username).butikk(butikk ).build();
        List<Favoritt> favoritter = resource.leggTilFavoritt(nyFavoritt);
        assertThat(favoritter.size()).isPositive();
    }

    @Test
    void testSlettFavoritt() {
        HandleregService handlereg = mock(HandleregService.class);
        Favoritt favoritt1 = Favoritt.with().favouriteid(1).accountid(1).build();
        Butikk butikk = Butikk.with().storeId(1).butikknavn("Joker Fjellstu").build();
        Favoritt favoritt2 = Favoritt.with().favouriteid(2).accountid(1).store(butikk).build();
        when(handlereg.slettFavoritt(any())).thenReturn(Arrays.asList(favoritt2));
        FavorittResource resource = new FavorittResource();
        resource.handlereg = handlereg;

        List<Favoritt> favoritter = resource.slettFavoritt(favoritt1);
        assertThat(favoritter.size()).isPositive();
        assertThat(favoritter).contains(favoritt2);
        assertThat(favoritter).doesNotContain(favoritt1);
        assertEquals(butikk, favoritter.get(0).getStore());
    }

    @Test
    void testByttRekkefolge() {
        HandleregService handlereg = mock(HandleregService.class);
        Favoritt favoritt1 = Favoritt.with().favouriteid(1).accountid(1).rekkefolge(2).build();
        Butikk butikk = Butikk.with().storeId(1).butikknavn("Joker Fjellstu").build();
        Favoritt favoritt2 = Favoritt.with().favouriteid(2).accountid(1).store(butikk).rekkefolge(1).build();
        when(handlereg.byttRekkefolge(any())).thenReturn(Arrays.asList(favoritt2, favoritt1));
        FavorittResource resource = new FavorittResource();
        resource.handlereg = handlereg;

        Favorittpar favoritterSomSkalBytteRekkefolge = Favorittpar.with()
            .forste(favoritt1)
            .andre(favoritt2)
            .build();
        List<Favoritt> favoritter = resource.byttRekkefolge(favoritterSomSkalBytteRekkefolge);
        assertThat(favoritter.size()).isPositive();
        assertThat(favoritter).containsSequence(favoritt2, favoritt1);
    }

}
