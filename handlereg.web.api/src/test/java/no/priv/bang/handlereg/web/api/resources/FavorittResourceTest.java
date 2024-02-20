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
package no.priv.bang.handlereg.web.api.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import no.priv.bang.handlereg.services.Butikk;
import no.priv.bang.handlereg.services.Favoritt;
import no.priv.bang.handlereg.services.Favorittpar;
import no.priv.bang.handlereg.services.HandleregService;
import no.priv.bang.handlereg.services.NyFavoritt;

class FavorittResourceTest {

    @Test
    void testGetFavoritter() {
        var handlereg = mock(HandleregService.class);
        var favoritt1 = Favoritt.with().favouriteid(1).accountid(1).build();
        var favoritt2 = Favoritt.with().favouriteid(2).accountid(1).build();
        when(handlereg.finnFavoritter("jod")).thenReturn(Arrays.asList(favoritt1, favoritt2));
        var resource = new FavorittResource();
        resource.handlereg = handlereg;

        var username = "jod";
        var favoritter = resource.getFavoritter(username);
        assertThat(favoritter).isNotEmpty();
    }

    @Test
    void testLeggTilFavoritt() {
        var handlereg = mock(HandleregService.class);
        var favoritt1 = Favoritt.with().favouriteid(1).accountid(1).build();
        var butikk = Butikk.with().storeId(1).butikknavn("Joker Fjellstu").build();
        var favoritt2 = Favoritt.with().favouriteid(2).accountid(1).store(butikk).build();
        when(handlereg.leggTilFavoritt(any())).thenReturn(Arrays.asList(favoritt1, favoritt2));
        var resource = new FavorittResource();
        resource.handlereg = handlereg;

        var username = "jod";
        var nyFavoritt = NyFavoritt.with().brukernavn(username).butikk(butikk ).build();
        var favoritter = resource.leggTilFavoritt(nyFavoritt);
        assertThat(favoritter).isNotEmpty();
    }

    @Test
    void testSlettFavoritt() {
        var handlereg = mock(HandleregService.class);
        var favoritt1 = Favoritt.with().favouriteid(1).accountid(1).build();
        var butikk = Butikk.with().storeId(1).butikknavn("Joker Fjellstu").build();
        var favoritt2 = Favoritt.with().favouriteid(2).accountid(1).store(butikk).build();
        when(handlereg.slettFavoritt(any())).thenReturn(Arrays.asList(favoritt2));
        var resource = new FavorittResource();
        resource.handlereg = handlereg;

        var favoritter = resource.slettFavoritt(favoritt1);
        assertThat(favoritter)
            .isNotEmpty()
            .contains(favoritt2)
            .doesNotContain(favoritt1);
        assertEquals(butikk, favoritter.get(0).getStore());
    }

    @Test
    void testByttRekkefolge() {
        var handlereg = mock(HandleregService.class);
        var favoritt1 = Favoritt.with().favouriteid(1).accountid(1).rekkefolge(2).build();
        var butikk = Butikk.with().storeId(1).butikknavn("Joker Fjellstu").build();
        var favoritt2 = Favoritt.with().favouriteid(2).accountid(1).store(butikk).rekkefolge(1).build();
        when(handlereg.byttRekkefolge(any())).thenReturn(Arrays.asList(favoritt2, favoritt1));
        var resource = new FavorittResource();
        resource.handlereg = handlereg;

        var favoritterSomSkalBytteRekkefolge = Favorittpar.with()
            .forste(favoritt1)
            .andre(favoritt2)
            .build();
        var favoritter = resource.byttRekkefolge(favoritterSomSkalBytteRekkefolge);
        assertThat(favoritter)
            .isNotEmpty()
            .containsSequence(favoritt2, favoritt1);
    }

}
