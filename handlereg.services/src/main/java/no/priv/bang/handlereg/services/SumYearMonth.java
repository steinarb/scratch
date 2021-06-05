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

import java.time.Month;
import java.time.Year;

public class SumYearMonth extends SumYear {

    private Month month;

    private SumYearMonth() {}

    public Month getMonth() {
        return month;
    }

    @Override
    public String toString() {
        return "SumYearMonth [month=" + month + "]";
    }

    public static SumYearMonthBuilder with() {
        return new SumYearMonthBuilder();
    }

    public static class SumYearMonthBuilder extends SumYearBuilder {
        private Month month;

        @Override
        public SumYearMonth build() {
            SumYearMonth sumYearMonth = new SumYearMonth();
            copyValues(sumYearMonth);
            sumYearMonth.month = this.month;
            return sumYearMonth;
        }

        @Override
        public SumYearMonthBuilder sum(double sum) {
            super.sum(sum);
            return this;
        }

        @Override
        public SumYearMonthBuilder year(Year year) {
            super.year(year);
            return this;
        }

        public SumYearMonthBuilder month(Month month) {
            this.month = month;
            return this;
        }
    }
}
