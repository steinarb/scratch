/*
 * Copyright 2019-2024 Steinar Bang
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

import static org.junit.jupiter.api.Assertions.*;

import java.time.Month;
import java.time.Year;

import org.junit.jupiter.api.Test;

class SumYearMonthTest {

    @Test
    void test() {
        var sum = 2345;
        var year = Year.of(2017);
        var month = Month.JULY;
        var bean = SumYearMonth.with().sum(sum).year(year).month(month).build();
        assertEquals(sum, bean.sum());
        assertEquals(year, bean.year());
        assertEquals(month, bean.month());
    }

    @Test
    void testNoargsConstructor() {
        var bean = SumYearMonth.with().build();
        assertEquals(0, bean.sum());
        assertNull(bean.year());
        assertNull(bean.month());
    }

}
