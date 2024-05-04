/*
 * Copyright 2021-2024 Steinar Bang
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

import no.priv.bang.beans.immutable.Immutable;

public class NyFavoritt extends Immutable {

    private String brukernavn;
    private Butikk butikk;

    private NyFavoritt() {}

    public String getBrukernavn() {
        return brukernavn;
    }

    public Butikk getButikk() {
        return butikk;
    }

    @Override
    public String toString() {
        return "NyFavoritt [brukernavn=" + brukernavn + ", butikk=" + butikk + "]";
    }

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {

        private String brukernavn;
        private Butikk butikk;

        private Builder() {}

        public NyFavoritt build() {
            var favorittOgBrukernavn = new NyFavoritt();
            favorittOgBrukernavn.brukernavn = this.brukernavn;
            favorittOgBrukernavn.butikk = this.butikk;
            return favorittOgBrukernavn;
        }

        public Builder brukernavn(String brukernavn) {
            this.brukernavn = brukernavn;
            return this;
        }

        public Builder butikk(Butikk favoritt) {
            this.butikk = favoritt;
            return this;
        }

    }

}
