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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record EndPoints(
    String proxyUrl,
    String oauthAuthorizationEndpoint,
    String oauthTokenEndpoint,
    String provideClientKey,
    String signClientKey,
    String sharedInbox)
{
    public static Builder with() {
        return new Builder();
    }

    public static class Builder {
        private String proxyUrl;
        private String oauthAuthorizationEndpoint;
        private String oauthTokenEndpoint;
        private String provideClientKey;
        private String signClientKey;
        private String sharedInbox;

        public EndPoints build() {
            return new EndPoints(
                proxyUrl,
                oauthAuthorizationEndpoint,
                oauthTokenEndpoint,
                provideClientKey,
                signClientKey,
                sharedInbox);
        }

        public Builder proxyUrl(String proxyUrl) {
            this.proxyUrl = proxyUrl;
            return this;
        }

        public Builder oauthAuthorizationEndpoint(String oauthAuthorizationEndpoint) {
            this.oauthAuthorizationEndpoint = oauthAuthorizationEndpoint;
            return this;
        }

        public Builder oauthTokenEndpoint(String oauthTokenEndpoint) {
            this.oauthTokenEndpoint = oauthTokenEndpoint;
            return this;
        }

        public Builder provideClientKey(String provideClientKey) {
            this.provideClientKey = provideClientKey;
            return this;
        }

        public Builder signClientKey(String signClientKey) {
            this.signClientKey = signClientKey;
            return this;
        }

        public Builder sharedInbox(String sharedInbox) {
            this.sharedInbox = sharedInbox;
            return this;
        }

    }
}
