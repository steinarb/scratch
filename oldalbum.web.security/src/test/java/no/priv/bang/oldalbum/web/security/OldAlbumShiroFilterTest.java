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

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.Ini;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.junit.jupiter.api.Test;

import no.priv.bang.oldalbum.services.OldAlbumService;

class OldAlbumShiroFilterTest {

    @Test
    void testAuthenticate() {
        var filter = new OldAlbumShiroFilter();
        var realm = getRealmFromIniFile();
        filter.setRealm(realm);
        var session = new MemorySessionDAO();
        filter.setSession(session);
        var oldalbum = mock(OldAlbumService.class);
        filter.setOldAlbumService(oldalbum);
        filter.activate();
        var securitymanager = filter.getSecurityManager();
        var token = new UsernamePasswordToken("jad", "1ad".toCharArray());
        var info = securitymanager.authenticate(token);
        assertEquals(1, info.getPrincipals().asList().size());
    }

    private static Realm getRealmFromIniFile() {
        var environment = new IniWebEnvironment();
        environment.setIni(Ini.fromResourcePath("classpath:test.shiro.ini"));
        environment.init();
        var securitymanager = RealmSecurityManager.class.cast(environment.getWebSecurityManager());
        var realms = securitymanager.getRealms();
        return (SimpleAccountRealm) realms.iterator().next();
    }

}
