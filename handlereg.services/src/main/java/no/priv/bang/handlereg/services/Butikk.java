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

import no.priv.bang.beans.immutable.Immutable;

public class Butikk extends Immutable {

    private int storeId;
    private String butikknavn;
    private int gruppe;
    private int rekkefolge;

    private Butikk() {}

    public int getStoreId() {
        return storeId;
    }

    public String getButikknavn() {
        return butikknavn;
    }

    public int getGruppe() {
        return gruppe;
    }

    public int getRekkefolge() {
        return rekkefolge;
    }

    public static ButikkBuilder with() {
        return new ButikkBuilder();
    }

    public static ButikkBuilder with(Butikk butikk) {
        ButikkBuilder builder = new ButikkBuilder();
        builder.storeId = butikk.storeId;
        builder.butikknavn = butikk.butikknavn;
        builder.gruppe = butikk.gruppe;
        builder.rekkefolge = butikk.rekkefolge;
        return builder;
    }

    public static class ButikkBuilder {

        private int storeId = -1;
        private String butikknavn;
        private int gruppe;
        private int rekkefolge;

        public Butikk build() {
            Butikk butikk = new Butikk();
            butikk.storeId = this.storeId;
            butikk.butikknavn = this.butikknavn;
            butikk.gruppe = this.gruppe;
            butikk.rekkefolge = this.rekkefolge;
            return butikk;
        }

        public ButikkBuilder storeId(int storeId) {
            this.storeId = storeId;
            return this;
        }

        public ButikkBuilder butikknavn(String butikknavn) {
            this.butikknavn = butikknavn;
            return this;
        }

        public ButikkBuilder gruppe(int gruppe) {
            this.gruppe = gruppe;
            return this;
        }

        public ButikkBuilder rekkefolge(int rekkefolge) {
            this.rekkefolge = rekkefolge;
            return this;
        }

    }

}
