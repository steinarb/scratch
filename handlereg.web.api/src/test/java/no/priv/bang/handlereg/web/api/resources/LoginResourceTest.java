/*
 * Copyright 2019-2021 Steinar Bang
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
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import no.priv.bang.handlereg.services.Credentials;
import no.priv.bang.handlereg.services.Loginresultat;
import no.priv.bang.handlereg.web.api.ShiroTestBase;
import no.priv.bang.handlereg.web.api.resources.LoginResource;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

class LoginResourceTest extends ShiroTestBase {

    @Test
    void testLogin() {
        LoginResource resource = new LoginResource();
        String username = "jd";
        String password = "johnnyBoi";
        createSubjectAndBindItToThread();
        Credentials credentials = Credentials.with().username(username).password(password).build();
        Loginresultat resultat = resource.login(credentials);
        assertTrue(resultat.getSuksess());
    }

    @Test
    void testLoginFeilPassord() {
        MockLogService logservice = new MockLogService();
        LoginResource resource = new LoginResource();
        resource.setLogservice(logservice);
        String username = "jd";
        String password = "feil";
        createSubjectAndBindItToThread();
        Credentials credentials = Credentials.with().username(username).password(password).build();
        Loginresultat resultat = resource.login(credentials);
        assertFalse(resultat.getSuksess());
        assertThat(resultat.getFeilmelding()).startsWith("Feil passord");
    }

    @Test
    void testLoginUkjentBrukernavn() {
        MockLogService logservice = new MockLogService();
        LoginResource resource = new LoginResource();
        resource.setLogservice(logservice);
        String username = "jdd";
        String password = "feil";
        createSubjectAndBindItToThread();
        Credentials credentials = Credentials.with().username(username).password(password).build();
        Loginresultat resultat = resource.login(credentials);
        assertThat(resultat.getFeilmelding()).startsWith("Ukjent konto");
    }

}
