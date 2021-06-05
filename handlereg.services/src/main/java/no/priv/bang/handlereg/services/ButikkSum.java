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

public class ButikkSum {

    private Butikk butikk;
    private double sum;

    private ButikkSum() {}

    public Butikk getButikk() {
        return butikk;
    }

    public double getSum() {
        return sum;
    }

    @Override
    public String toString() {
        return "ButikkSum [butikk=" + butikk + ", sum=" + sum + "]";
    }

    public static ButikkSumBuilder with() {
        return new ButikkSumBuilder();
    }

    public static class ButikkSumBuilder {
        private Butikk butikk;
        private double sum;

        private ButikkSumBuilder() {}

        public ButikkSum build() {
            ButikkSum butikkSum = new ButikkSum();
            butikkSum.butikk = this.butikk;
            butikkSum.sum = this.sum;
            return butikkSum;
        }

        public ButikkSumBuilder butikk(Butikk butikk) {
            this.butikk = butikk;
            return this;
        }

        public ButikkSumBuilder sum(double sum) {
            this.sum = sum;
            return this;
        }

    }
}
