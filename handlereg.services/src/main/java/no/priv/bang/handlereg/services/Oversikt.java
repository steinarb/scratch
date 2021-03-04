/*
 * Copyright 2018-2021 Steinar Bang
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

public class Oversikt {
    private int accountid;
    private String brukernavn;
    private String email;
    private String fornavn;
    private String etternavn;
    private double balanse;

    private Oversikt() {}

    public int getAccountid() {
        return accountid;
    }

    public String getBrukernavn() {
        return brukernavn;
    }

    public String getEmail() {
        return email;
    }

    public String getFornavn() {
        return fornavn;
    }

    public String getEtternavn() {
        return etternavn;
    }

    public double getBalanse() {
        return balanse;
    }

    @Override
    public String toString() {
        return "Oversikt [user_id=" + accountid + ", brukernavn=" + brukernavn + ", fornavn=" + fornavn + ", etternavn=" + etternavn + ", balanse=" + balanse + "]";
    }

    public static OversiktBuilder with() {
        OversiktBuilder oversiktBuilder = new OversiktBuilder();
        return oversiktBuilder;
    }

    public static class OversiktBuilder {
        private int accountid = -1;
        private String brukernavn;
        private String email;
        private String fornavn;
        private String etternavn;
        private double balanse;

        private OversiktBuilder() {}

        public Oversikt build() {
            Oversikt oversikt = new Oversikt();
            oversikt.accountid = this.accountid;
            oversikt.brukernavn = this.brukernavn;
            oversikt.email = this.email;
            oversikt.fornavn = this.fornavn;
            oversikt.etternavn = this.etternavn;
            oversikt.balanse = this.balanse;
            return oversikt;
        }

        public OversiktBuilder accountid(int accountid) {
            this.accountid = accountid;
            return this;
        }

        public OversiktBuilder brukernavn(String brukernavn) {
            this.brukernavn = brukernavn;
            return this;
        }

        public OversiktBuilder email(String email) {
            this.email = email;
            return this;
        }

        public OversiktBuilder fornavn(String fornavn) {
            this.fornavn = fornavn;
            return this;
        }

        public OversiktBuilder etternavn(String etternavn) {
            this.etternavn = etternavn;
            return this;
        }

        public OversiktBuilder balanse(double balanse) {
            this.balanse = balanse;
            return this;
        }
    }

}
