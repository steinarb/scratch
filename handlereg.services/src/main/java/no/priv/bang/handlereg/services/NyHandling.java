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

import java.util.Date;

public class NyHandling {

    private String username;
    private int accountid;
    private int storeId;
    private double belop;
    private Date handletidspunkt;

    private NyHandling() {}

    public String getUsername() {
        return username;
    }

    public int getAccountid() {
        return accountid;
    }

    public int getStoreId() {
        return storeId;
    }

    public double getBelop() {
        return belop;
    }

    public Date getHandletidspunkt() {
        return handletidspunkt;
    }

    public static NyHandlingBuilder with() {
        NyHandlingBuilder nyHandlingBuilder = new NyHandlingBuilder();
        return nyHandlingBuilder;
    }

    public static class NyHandlingBuilder {
        private String username;
        private int accountid = -1;
        private int storeId = -1;
        private double belop;
        private Date handletidspunkt;

        private NyHandlingBuilder() {}

        public NyHandling build() {
            NyHandling nyHandling = new NyHandling();
            nyHandling.username = this.username;
            nyHandling.accountid = this.accountid;
            nyHandling.storeId = this.storeId;
            nyHandling.belop = this.belop;
            nyHandling.handletidspunkt = this.handletidspunkt;
            return nyHandling;
        }

        public NyHandlingBuilder username(String username) {
            this.username = username;
            return this;
        }

        public NyHandlingBuilder accountid(int accountid) {
            this.accountid = accountid;
            return this;
        }

        public NyHandlingBuilder storeId(int storeId) {
            this.storeId = storeId;
            return this;
        }

        public NyHandlingBuilder belop(double belop) {
            this.belop = belop;
            return this;
        }

        public NyHandlingBuilder handletidspunkt(Date handletidspunkt) {
            this.handletidspunkt = handletidspunkt;
            return this;
        }
    }

}
