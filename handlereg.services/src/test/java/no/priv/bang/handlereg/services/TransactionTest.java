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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;

class TransactionTest {

    @Test
    void testCreate() {
        Date now = new Date();
        Transaction bean = Transaction.with()
            .transactionId(1)
            .handletidspunkt(now)
            .butikk("Spar fjellheimen")
            .storeId(101)
            .belop(107.0)
            .build();
        assertEquals(1, bean.getTransactionId());
        assertEquals(now, bean.getHandletidspunkt());
        assertEquals("Spar fjellheimen", bean.getButikk());
        assertEquals(101, bean.getStoreId());
        assertEquals(107.0, bean.getBelop(), 1.0);
    }

    @Test
    void testCreateWithNoargsConstructor() {
        Transaction bean = Transaction.with().build();
        assertEquals(-1, bean.getTransactionId());
        assertNull(bean.getHandletidspunkt());
        assertNull(bean.getButikk());
        assertEquals(-1, bean.getStoreId());
        assertEquals(0.0, bean.getBelop(), 1.0);
    }

    @Test
    void testToString() {
        Transaction bean = Transaction.with().build();
        assertThat(bean.toString()).startsWith("Transaction [");
    }

}
