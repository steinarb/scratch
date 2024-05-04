/*
 * Copyright 2018-2024 Steinar Bang
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

public record Oversikt(
    int accountid,
    String brukernavn,
    String email,
    String fornavn,
    String etternavn,
    double balanse,
    double sumPreviousMonth,
    double sumThisMonth,
    double lastTransactionAmount,
    int lastTransactionStore)
{

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {
        private int accountid = -1;
        private String brukernavn;
        private String email;
        private String fornavn;
        private String etternavn;
        private double balanse;
        private double sumPreviousMonth;
        private double sumThisMonth;
        private double lastTransactionAmount;
        private int lastTransactionStore;

        private Builder() {}

        public Oversikt build() {
            return new Oversikt(
                accountid,
                brukernavn,
                email,
                fornavn,
                etternavn,
                balanse,
                sumPreviousMonth,
                sumThisMonth,
                lastTransactionAmount,
                lastTransactionStore);
        }

        public Builder accountid(int accountid) {
            this.accountid = accountid;
            return this;
        }

        public Builder brukernavn(String brukernavn) {
            this.brukernavn = brukernavn;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder fornavn(String fornavn) {
            this.fornavn = fornavn;
            return this;
        }

        public Builder etternavn(String etternavn) {
            this.etternavn = etternavn;
            return this;
        }

        public Builder balanse(double balanse) {
            this.balanse = balanse;
            return this;
        }

        public Builder sumPreviousMonth(double sumPreviousMonth) {
            this.sumPreviousMonth = sumPreviousMonth;
            return this;
        }

        public Builder sumThisMonth(double sumThisMonth) {
            this.sumThisMonth = sumThisMonth;
            return this;
        }

        public Builder lastTransactionAmount(double lastTransactionAmount) {
            this.lastTransactionAmount = lastTransactionAmount;
            return this;
        }

        public Builder lastTransactionStore(int lastTransactionStore) {
            this.lastTransactionStore = lastTransactionStore;
            return this;
        }
    }

}
