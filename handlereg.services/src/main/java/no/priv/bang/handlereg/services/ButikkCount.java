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

public class ButikkCount {

    private Butikk butikk;
    private long count;

    private ButikkCount() {}

    public Butikk getButikk() {
        return butikk;
    }

    public long getCount() {
        return count;
    }

    public static ButikkCountBuilder with() {
        return new ButikkCountBuilder();
    }

    public static class ButikkCountBuilder {
        private Butikk butikk;
        private long count;

        private ButikkCountBuilder() {}

        public ButikkCount build() {
            ButikkCount butikkCount = new ButikkCount();
            butikkCount.butikk = this.butikk;
            butikkCount.count = this.count;
            return butikkCount;
        }

        public ButikkCountBuilder butikk(Butikk butikk) {
            this.butikk = butikk;
            return this;
        }

        public ButikkCountBuilder count(long count) {
            this.count = count;
            return this;
        }
    }

}
