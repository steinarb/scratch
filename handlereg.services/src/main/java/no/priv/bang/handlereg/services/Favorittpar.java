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

public class Favorittpar extends Immutable {

    private Favoritt forste;
    private Favoritt andre;

    private Favorittpar() {}

    public static Builder with() {
        return new Builder();
    }

    public Favoritt getForste() {
        return forste;
    }

    public Favoritt getAndre() {
        return andre;
    }

    @Override
    public String toString() {
        return "Favorittpar [forste=" + forste + ", andre=" + andre + "]";
    }

    public static class Builder {

        private Favoritt forste;
        private Favoritt andre;

        private Builder() {}

        public Favorittpar build() {
            var favorittpar = new Favorittpar();
            favorittpar.forste = this.forste;
            favorittpar.andre = this.andre;
            return favorittpar;
        }

        public Builder forste(Favoritt forste) {
            this.forste = forste;
            return this;
        }

        public Builder andre(Favoritt andre) {
            this.andre = andre;
            return this;
        }

    }

}
