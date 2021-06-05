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
package no.priv.bang.handlereg.services;

public class Loginresultat {

    private boolean suksess;
    private String feilmelding;
    private boolean authorized;

    private Loginresultat() {}

    public boolean getSuksess() {
        return suksess;
    }

    public String getFeilmelding() {
        return feilmelding;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    @Override
    public String toString() {
        return "Loginresultat [suksess=" + suksess + ", feilmelding=" + feilmelding + ", authorized=" + authorized + "]";
    }

    public static LoginresultatBuilder with() {
        return new LoginresultatBuilder();
    }

    public static class LoginresultatBuilder {
        private boolean suksess;
        private String feilmelding;
        private boolean authorized;

        private LoginresultatBuilder() {}

        public Loginresultat build() {
            Loginresultat loginresultat = new Loginresultat();
            loginresultat.suksess = this.suksess;
            loginresultat.feilmelding = this.feilmelding;
            loginresultat.authorized = authorized;
            return loginresultat;
        }

        public LoginresultatBuilder suksess(boolean suksess) {
            this.suksess = suksess;
            return this;
        }

        public LoginresultatBuilder feilmelding(String feilmelding) {
            this.feilmelding = feilmelding;
            return this;
        }

        public LoginresultatBuilder authorized(boolean authorized) {
            this.authorized = authorized;
            return this;
        }
    }

}
