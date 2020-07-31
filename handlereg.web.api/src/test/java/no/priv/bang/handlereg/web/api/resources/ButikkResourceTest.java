/*
 * Copyright 2019-2020 Steinar Bang
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.InternalServerErrorException;

import org.junit.jupiter.api.Test;

import no.priv.bang.handlereg.services.Butikk;
import no.priv.bang.handlereg.services.HandleregException;
import no.priv.bang.handlereg.services.HandleregService;
import no.priv.bang.handlereg.web.api.resources.ButikkResource;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class ButikkResourceTest {

    @Test
    void testGetButikker() {
        MockLogService logservice = new MockLogService();
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.finnButikker()).thenReturn(Arrays.asList(new Butikk()));
        ButikkResource resource = new ButikkResource();
        resource.logservice = logservice;
        resource.handlereg = handlereg;

        List<Butikk> butikker = resource.getButikker();
        assertThat(butikker.size()).isGreaterThan(0);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetButikkerWhenExceptionIsThrown() {
        MockLogService logservice = new MockLogService();
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.finnButikker()).thenThrow(HandleregException.class);
        ButikkResource resource = new ButikkResource();
        resource.logservice = logservice;
        resource.handlereg = handlereg;

        assertThrows(InternalServerErrorException.class, () -> {
                resource.getButikker();
            });
    }

    @Test
    void testLeggTilButikk() {
        MockLogService logservice = new MockLogService();
        HandleregService handlereg = mock(HandleregService.class);
        Butikk nybutikk = new Butikk("Spar Høydalsmo");
        when(handlereg.leggTilButikk(any())).thenReturn(Arrays.asList(nybutikk));
        ButikkResource resource = new ButikkResource();
        resource.logservice = logservice;
        resource.handlereg = handlereg;

        List<Butikk> butikker = resource.leggTilButikk(nybutikk);
        assertEquals(nybutikk, butikker.get(0));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testLeggTilButikkWhenExceptionIsThrown() {
        MockLogService logservice = new MockLogService();
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.leggTilButikk(any())).thenThrow(HandleregException.class);
        ButikkResource resource = new ButikkResource();
        resource.logservice = logservice;
        resource.handlereg = handlereg;

        Butikk butikk = new Butikk();
        assertThrows(InternalServerErrorException.class, () -> {
                resource.leggTilButikk(butikk);
            });
    }

    @Test
    void testEndreButikk() {
        MockLogService logservice = new MockLogService();
        HandleregService handlereg = mock(HandleregService.class);
        Butikk endretbutikk = new Butikk("Spar Høydalsmo");
        when(handlereg.endreButikk(any())).thenReturn(Arrays.asList(endretbutikk));
        ButikkResource resource = new ButikkResource();
        resource.logservice = logservice;
        resource.handlereg = handlereg;

        List<Butikk> butikker = resource.endreButikk(endretbutikk);
        assertEquals(endretbutikk, butikker.get(0));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testEndreButikkWhenExceptionIsThrown() {
        MockLogService logservice = new MockLogService();
        HandleregService handlereg = mock(HandleregService.class);
        when(handlereg.endreButikk(any())).thenThrow(HandleregException.class);
        ButikkResource resource = new ButikkResource();
        resource.logservice = logservice;
        resource.handlereg = handlereg;

        Butikk butikk = new Butikk();
        assertThrows(InternalServerErrorException.class, () -> {
                resource.endreButikk(butikk);
            });
    }

}
