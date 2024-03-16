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
package no.priv.bang.oldalbum.web.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.LinkedHashMap;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.Ini;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.junit.jupiter.api.Test;

import com.mockrunner.mock.web.MockFilterChain;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;

import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class OldAlbumShiroFilterTest {

    @Test
    void testAuthenticate() throws Exception {
        var filter = new OldAlbumShiroFilter();
        var realm = getRealmFromIniFile();
        filter.setRealm(realm);
        var session = new MemorySessionDAO();
        filter.setSession(session);
        var oldalbum = mock(OldAlbumService.class);
        filter.setOldAlbumService(oldalbum);
        var logservice = new MockLogService();
        filter.setLogService(logservice);
        filter.activate();

        var securitymanager = filter.getSecurityManager();
        var token = new UsernamePasswordToken("jad", "1ad".toCharArray());
        var info = securitymanager.authenticate(token);
        assertEquals(1, info.getPrincipals().asList().size());
    }

    @Test
    void testWebAuthorize() throws Exception {
        var filter = new OldAlbumShiroFilter();
        var realm = getRealmFromIniFile();
        filter.setRealm(realm);
        var session = new MemorySessionDAO();
        filter.setSession(session);
        var oldalbum = mock(OldAlbumService.class);
        var emptyProtectedUrls = new LinkedHashMap<String, String>();
        var protectedUrls = new LinkedHashMap<String, String>();
        protectedUrls.put("/slides/berglia", "anon");
        protectedUrls.put("/slides/", "authc");
        when(oldalbum.findShiroProtectedUrls()).thenReturn(emptyProtectedUrls).thenReturn(protectedUrls);
        filter.setOldAlbumService(oldalbum);
        var logservice = new MockLogService();
        filter.setLogService(logservice);
        filter.activate();

        // Resources to test
        var slidesRequest = buildGetRequest("/slides/");

        var bergliaRequest = buildGetRequest("/slides/berglia");

        // First verify that access is allowed for both resources
        var filterChain = new MockFilterChain();
        var slidesResponse1 = new MockHttpServletResponse();
        filter.doFilter(slidesRequest, slidesResponse1, filterChain);
        assertEquals(200, slidesResponse1.getStatusCode(), "Expect redirect to /login");
        var bergliaResponse1 = new MockHttpServletResponse();
        filter.doFilter(bergliaRequest, bergliaResponse1, filterChain);
        assertEquals(200, bergliaResponse1.getStatusCode(), "Expect access allowed");

        // Call the update function to reload the filter
        var status = filter.reloadConfiguration();
        assertTrue(status);

        // Verify that the album is password protected (redirects to login) and picture is unprotected
        var slidesResponse2 = new MockHttpServletResponse();
        filter.doFilter(slidesRequest, slidesResponse2, filterChain);
        assertEquals(302, slidesResponse2.getStatusCode(), "Expect redirect to /login");
        var bergliaResponse2 = new MockHttpServletResponse();
        filter.doFilter(bergliaRequest, bergliaResponse2, filterChain);
        assertEquals(200, bergliaResponse2.getStatusCode(), "Expect access allowed");
    }

    MockHttpServletRequest buildGetRequest(String resource) {
        var session = new MockHttpSession();
        var contextPath = "/oldalbum";
        var requestUri = contextPath + resource;
        return new MockHttpServletRequest()
            .setProtocol("HTTP/1.1")
            .setRequestURL("http://localhost:8181" + requestUri)
            .setRequestURI(requestUri)
            .setPathInfo(resource)
            .setContextPath(contextPath)
            .setServletPath("")
            .setSession(session);
    }

    private static Realm getRealmFromIniFile() {
        var environment = new IniWebEnvironment();
        environment.setIni(Ini.fromResourcePath("classpath:security.test.shiro.ini"));
        environment.init();
        var securitymanager = RealmSecurityManager.class.cast(environment.getWebSecurityManager());
        var realms = securitymanager.getRealms();
        return (SimpleAccountRealm) realms.iterator().next();
    }

}
