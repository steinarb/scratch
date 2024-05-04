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
package no.priv.bang.handlereg.services;

public record Loginresultat(
    boolean suksess,
    String feilmelding,
    boolean authorized,
    String originalRequestUrl,
    String brukernavn)
{

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {
        private boolean suksess;
        private String feilmelding;
        private boolean authorized;
        private String originalRequestUrl;
        private String brukernavn;

        private Builder() {}

        public Loginresultat build() {
            return new Loginresultat(suksess, feilmelding, authorized, originalRequestUrl, brukernavn);
        }

        public Builder suksess(boolean suksess) {
            this.suksess = suksess;
            return this;
        }

        public Builder feilmelding(String feilmelding) {
            this.feilmelding = feilmelding;
            return this;
        }

        public Builder authorized(boolean authorized) {
            this.authorized = authorized;
            return this;
        }

        public Builder originalRequestUrl(String originalRequestUrl) {
            this.originalRequestUrl = originalRequestUrl;
            return this;
        }

        public Builder brukernavn(String brukernavn) {
            this.brukernavn = brukernavn;
            return this;
        }
    }

}
