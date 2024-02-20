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
package no.priv.bang.handlereg.web.api.resources;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import javax.ws.rs.InternalServerErrorException;

import org.junit.jupiter.api.Test;

import no.priv.bang.handlereg.services.Butikk;
import no.priv.bang.handlereg.services.HandleregException;
import no.priv.bang.handlereg.services.HandleregService;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class ButikkResourceTest {

    @Test
    void testGetButikker() {
        var logservice = new MockLogService();
        var handlereg = mock(HandleregService.class);
        when(handlereg.finnButikker()).thenReturn(Arrays.asList(Butikk.with().build()));
        var resource = new ButikkResource();
        resource.setLogservice(logservice);
        resource.handlereg = handlereg;

        var butikker = resource.getButikker();
        assertThat(butikker).isNotEmpty();
    }

    @Test
    void testGetButikkerWhenExceptionIsThrown() {
        var logservice = new MockLogService();
        var handlereg = mock(HandleregService.class);
        when(handlereg.finnButikker()).thenThrow(HandleregException.class);
        var resource = new ButikkResource();
        resource.setLogservice(logservice);
        resource.handlereg = handlereg;

        assertThrows(InternalServerErrorException.class, () -> {
                resource.getButikker();
            });
    }

    @Test
    void testLeggTilButikk() {
        var logservice = new MockLogService();
        var handlereg = mock(HandleregService.class);
        var nybutikk = Butikk.with().butikknavn("Spar Høydalsmo").build();
        when(handlereg.leggTilButikk(any())).thenReturn(Arrays.asList(nybutikk));
        var resource = new ButikkResource();
        resource.setLogservice(logservice);
        resource.handlereg = handlereg;

        var butikker = resource.leggTilButikk(nybutikk);
        assertEquals(nybutikk, butikker.get(0));
    }

    @Test
    void testLeggTilButikkWhenExceptionIsThrown() {
        var logservice = new MockLogService();
        var handlereg = mock(HandleregService.class);
        when(handlereg.leggTilButikk(any())).thenThrow(HandleregException.class);
        var resource = new ButikkResource();
        resource.setLogservice(logservice);
        resource.handlereg = handlereg;

        var butikk = Butikk.with().build();
        assertThrows(InternalServerErrorException.class, () -> {
                resource.leggTilButikk(butikk);
            });
    }

    @Test
    void testEndreButikk() {
        var logservice = new MockLogService();
        var handlereg = mock(HandleregService.class);
        var endretbutikk = Butikk.with().butikknavn("Spar Høydalsmo").build();
        when(handlereg.endreButikk(any())).thenReturn(Arrays.asList(endretbutikk));
        var resource = new ButikkResource();
        resource.setLogservice(logservice);
        resource.handlereg = handlereg;

        var butikker = resource.endreButikk(endretbutikk);
        assertEquals(endretbutikk, butikker.get(0));
    }

    @Test
    void testEndreButikkWhenExceptionIsThrown() {
        var logservice = new MockLogService();
        var handlereg = mock(HandleregService.class);
        when(handlereg.endreButikk(any())).thenThrow(HandleregException.class);
        var resource = new ButikkResource();
        resource.setLogservice(logservice);
        resource.handlereg = handlereg;

        var butikk = Butikk.with().build();
        assertThrows(InternalServerErrorException.class, () -> {
                resource.endreButikk(butikk);
            });
    }

}
