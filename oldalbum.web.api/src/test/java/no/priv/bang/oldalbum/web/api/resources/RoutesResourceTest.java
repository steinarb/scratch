/*
 * Copyright 2020 Steinar Bang
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
package no.priv.bang.oldalbum.web.api.resources;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;

class RoutesResourceTest {
    final static ObjectMapper mapper = new ObjectMapper();
    private static List<AlbumEntry> allroutes;

    @BeforeAll
    static void beforeAllTests() throws Exception {
        allroutes = mapper.readValue(RoutesResourceTest.class.getClassLoader().getResourceAsStream("allroutes.json"), new TypeReference<List<AlbumEntry>>() {});
    }

    @Test
    void testAllroutes() {
        OldAlbumService backendService = mock(OldAlbumService.class);
        when(backendService.fetchAllRoutes()).thenReturn(allroutes);
        RoutesResource resource = new RoutesResource();
        resource.oldAlbumService = backendService;
        List<AlbumEntry> routes = resource.allroutes();
        assertEquals(21, routes.size());
    }

}
