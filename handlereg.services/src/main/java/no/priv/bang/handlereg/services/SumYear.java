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

import java.time.Year;

public class SumYear {

    private double sum;
    private Year year;

    protected SumYear() {}

    public double getSum() {
        return sum;
    }

    public Year getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "SumYear [sum=" + sum + ", year=" + year + "]";
    }

    public static SumYearBuilder with() {
        return new SumYearBuilder();
    }

    public static class SumYearBuilder {
        private double sum;
        private Year year;

        protected SumYearBuilder() {}

        public SumYear build() {
            SumYear sumYear = new SumYear();
            copyValues(sumYear);
            return sumYear;
        }

        protected void copyValues(SumYear sumYear) {
            sumYear.sum = this.sum;
            sumYear.year = this.year;
        }

        public SumYearBuilder sum(double sum) {
            this.sum = sum;
            return this;
        }

        public SumYearBuilder year(Year year) {
            this.year = year;
            return this;
        }
    }

}
