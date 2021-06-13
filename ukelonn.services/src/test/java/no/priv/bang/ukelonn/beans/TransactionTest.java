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
package no.priv.bang.ukelonn.beans;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import org.junit.jupiter.api.Test;

class TransactionTest {

    @Test
    void testNoArgConstructor() {
        Transaction bean = Transaction.with().build();
        assertEquals(-1, bean.getId());
        assertNull(bean.getTransactionType());
        assertNull(bean.getTransactionTime());
        assertEquals(0.0, bean.getTransactionAmount(), 0.0);
        assertFalse(bean.isPaidOut());
    }

    @Test
    void testConstructorWithArgs() {
        int id = 5;
        TransactionType transactionType = TransactionType.with().build();
        Date transactionTime = new Date();
        double transactionAmount = 100.0;
        boolean paidOut = true;
        Transaction bean = Transaction.with()
            .id(id)
            .transactionType(transactionType)
            .transactionTime(transactionTime)
            .transactionAmount(transactionAmount)
            .paidOut(paidOut)
            .build();
        assertEquals(id, bean.getId());
        assertEquals(transactionType, bean.getTransactionType());
        assertEquals(transactionTime, bean.getTransactionTime());
        assertEquals(transactionAmount, bean.getTransactionAmount(), 0.0);
        assertTrue(bean.isPaidOut());
    }

}
