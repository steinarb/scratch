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
import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
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
    static String dumpedroutes = loadClasspathResourceIntoString("dumproutes.sql");

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

    @Test
    void testDumpSql() {
        OldAlbumService backendService = mock(OldAlbumService.class);
        when(backendService.dumpDatabaseSql()).thenReturn(dumpedroutes);
        RoutesResource resource = new RoutesResource();
        resource.oldAlbumService = backendService;
        String sql = resource.dumpSql();
        assertThat(sql).contains("--liquibase formatted sql");
    }

    private static String loadClasspathResourceIntoString(String resource) {
        InputStream resourceStream = RoutesResourceTest.class.getClassLoader().getResourceAsStream(resource);
        StringBuilder builder = new StringBuilder();
        try(Reader reader = new BufferedReader(new InputStreamReader(resourceStream, StandardCharsets.UTF_8))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                builder.append((char) c);
            }
        } catch (IOException e) {
            return "";
        }
        return builder.toString();
    }

}
