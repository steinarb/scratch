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

import org.junit.jupiter.api.Test;

class ButikkSumTest {

    @Test
    void test() {
        var butikk = Butikk.with().butikknavn("Spar Fjellheimen").build();
        var sum = 2345;
        var bean = ButikkSum.with().butikk(butikk).sum(sum).build();
        assertEquals(butikk, bean.butikk());
        assertEquals(sum, bean.sum());
    }

    @Test
    void testNoargsConstructor() {
        var bean = ButikkSum.with().build();
        assertNull(bean.butikk());
        assertEquals(0, bean.sum());
    }

    @Test
    void testToString() {
        var bean = ButikkSum.with().build();
        assertThat(bean.toString()).startsWith("ButikkSum[");
    }

}
