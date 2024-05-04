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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Year;

import org.junit.jupiter.api.Test;

class SumYearTest {

    @Test
    void test() {
        var sum = 2345;
        var year = Year.of(2017);
        var bean = SumYear.with().sum(sum).year(year).build();
        assertEquals(sum, bean.sum());
        assertEquals(year, bean.year());
    }

    @Test
    void testNoargsConstructor() {
        var bean = SumYear.with().build();
        assertEquals(0, bean.sum());
        assertNull(bean.year());
    }

    @Test
    void testToString() {
        var bean = SumYear.with().build();
        assertThat(bean.toString()).startsWith("SumYear[");
    }

}
