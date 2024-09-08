/*
 * Copyright 2024 Steinar Bang
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
package no.priv.bang.ratatoskr.asvocabulary;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class EndPointsTest {

    @Test
    void testCreateEndPoints() {
        var proxyUrl = "http://actorproxy.com";
        var oauthAuthorizationEndpoint = "http://oauth.com";
        var oauthTokenEndpoint = "http://token.com";
        var provideClientKey = "http://keymaster.com";
        var signClientKey = "http://signmaster.com";
        var sharedInbox = "http://localhost:8181/ratatoskr/ap/sharedinbox";
        var endPoints = EndPoints.with()
            .proxyUrl(proxyUrl)
            .oauthAuthorizationEndpoint(oauthAuthorizationEndpoint)
            .oauthTokenEndpoint(oauthTokenEndpoint)
            .provideClientKey(provideClientKey)
            .signClientKey(signClientKey)
            .sharedInbox(sharedInbox)
            .build();

        assertThat(endPoints)
            .isNotNull()
            .hasFieldOrPropertyWithValue("proxyUrl",proxyUrl)
            .hasFieldOrPropertyWithValue("oauthAuthorizationEndpoint",oauthAuthorizationEndpoint)
            .hasFieldOrPropertyWithValue("oauthTokenEndpoint",oauthTokenEndpoint)
            .hasFieldOrPropertyWithValue("provideClientKey",provideClientKey)
            .hasFieldOrPropertyWithValue("signClientKey",signClientKey)
            .hasFieldOrPropertyWithValue("sharedInbox",sharedInbox);
    }

}
