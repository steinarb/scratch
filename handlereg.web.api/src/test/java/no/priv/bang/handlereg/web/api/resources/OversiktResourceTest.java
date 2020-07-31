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

import javax.ws.rs.InternalServerErrorException;

import org.junit.jupiter.api.Test;

import no.priv.bang.handlereg.services.HandleregService;
import no.priv.bang.handlereg.services.Oversikt;
import no.priv.bang.handlereg.web.api.ShiroTestBase;
import no.priv.bang.handlereg.web.api.resources.OversiktResource;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class OversiktResourceTest extends ShiroTestBase {

    @Test
    void testGetOversikt() {
        HandleregService handlereg = mock(HandleregService.class);
        Oversikt jdOversikt = new Oversikt(1, "jd", "johndoe@gmail.com", "John", "Doe", 1500);
        when(handlereg.finnOversikt("jd")).thenReturn(jdOversikt);
        OversiktResource resource = new OversiktResource();
        resource.handlereg = handlereg;
        loginUser("jd", "johnnyBoi");

        Oversikt oversikt = resource.get();
        assertEquals("jd", oversikt.getBrukernavn());
    }

    @Test
    void testGetOversiktNotLoggedIn() {
        MockLogService logservice = new MockLogService();
        HandleregService handlereg = mock(HandleregService.class);
        Oversikt jdOversikt = new Oversikt(1, "jd", "johndoe@gmail.com", "John", "Doe", 1500);
        when(handlereg.finnOversikt("jd")).thenReturn(jdOversikt);
        OversiktResource resource = new OversiktResource();
        resource.logservice = logservice;
        resource.handlereg = handlereg;
        removeWebSubjectFromThread();

        assertThrows(InternalServerErrorException.class, () -> {
                resource.get();
            });
    }

}
