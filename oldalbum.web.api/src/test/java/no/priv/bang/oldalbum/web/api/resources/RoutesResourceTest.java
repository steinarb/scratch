/*
 * Copyright 2020-2024 Steinar Bang
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

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.ReloadableShiroFilter;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.oldalbum.testutilities.ShiroTestBase;

class RoutesResourceTest extends ShiroTestBase {
    final static ObjectMapper mapper = new ObjectMapper();
    private OldAlbumService backendService;
    private static List<AlbumEntry> allroutes;
    private static List<AlbumEntry> allPublicRoutes;
    static String dumpedroutes = loadClasspathResourceIntoString("dumproutes.sql");

    @BeforeAll
    static void beforeAllTests() throws Exception {
        allroutes = mapper.readValue(RoutesResourceTest.class.getClassLoader().getResourceAsStream("allroutes.json"), new TypeReference<List<AlbumEntry>>() {});
        allPublicRoutes = new ArrayList<>(allroutes);
        allPublicRoutes.remove(allPublicRoutes.size() - 1);
    }

    @BeforeEach
    void setup() {
        backendService = mock(OldAlbumService.class);
        when(backendService.fetchAllRoutes(null, false)).thenReturn(allPublicRoutes);
        when(backendService.fetchAllRoutes(anyString(), eq(true))).thenReturn(allroutes);
    }

    @Test
    void testAllroutesWhenNotLoggedIn() {
        var resource = new RoutesResource();
        resource.oldAlbumService = backendService;
        createSubjectAndBindItToThread();
        var routes = resource.allroutes();
        assertThat(routes).hasSameSizeAs(allPublicRoutes);
    }

    @Test
    void testAllroutesWhenLoggedIn() {
        var resource = new RoutesResource();
        resource.oldAlbumService = backendService;
        createSubjectAndBindItToThread();
        loginUser("jad", "1ad");
        var routes = resource.allroutes();
        assertThat(routes).hasSameSizeAs(allroutes);
    }

    @Test
    void testDumpSqlWhenNotLoggedIn() {
        var mockedBackendService = mock(OldAlbumService.class);
        when(mockedBackendService.dumpDatabaseSql(null, false)).thenReturn(dumpedroutes);
        var resource = new RoutesResource();
        resource.oldAlbumService = mockedBackendService;
        createSubjectAndBindItToThread();
        var sql = resource.dumpSql();
        assertThat(sql).contains("--liquibase formatted sql");
    }

    @Test
    void testDumpSqlWhenLoggedIn() {
        var mockedBackendService = mock(OldAlbumService.class);
        when(mockedBackendService.dumpDatabaseSql("jad", true)).thenReturn(dumpedroutes);
        var resource = new RoutesResource();
        resource.oldAlbumService = mockedBackendService;
        createSubjectAndBindItToThread();
        loginUser("jad", "1ad");
        var sql = resource.dumpSql();
        assertThat(sql).contains("--liquibase formatted sql");
    }

    @Test
    void testReloadShiroConfig() {
        var shirofilter = mock(ReloadableShiroFilter.class);
        when(shirofilter.reloadConfiguration()).thenReturn(true);
        var resource = new RoutesResource();
        resource.shiroFilter = shirofilter;
        var response = resource.reloadShiroConfig();
        assertEquals(200, response.getStatus());
    }

    @Test
    void testReloadShiroConfigWithFailedReload() {
        var shirofilter = mock(ReloadableShiroFilter.class);
        when(shirofilter.reloadConfiguration()).thenReturn(false);
        var resource = new RoutesResource();
        resource.shiroFilter = shirofilter;
        var response = resource.reloadShiroConfig();
        assertEquals(500, response.getStatus());
    }

    private static String loadClasspathResourceIntoString(String resource) {
        var resourceStream = RoutesResourceTest.class.getClassLoader().getResourceAsStream(resource);
        var builder = new StringBuilder();
        try(var reader = new BufferedReader(new InputStreamReader(resourceStream, StandardCharsets.UTF_8))) {
            var c = 0;
            while ((c = reader.read()) != -1) {
                builder.append((char) c);
            }
        } catch (IOException e) {
            return "";
        }
        return builder.toString();
    }

}
