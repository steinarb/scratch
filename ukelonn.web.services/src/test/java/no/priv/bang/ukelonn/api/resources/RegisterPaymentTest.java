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
package no.priv.bang.ukelonn.api.resources;

import static no.priv.bang.ukelonn.testutils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.priv.bang.ukelonn.UkelonnService;
import no.priv.bang.ukelonn.beans.Account;
import no.priv.bang.ukelonn.beans.PerformedTransaction;
import no.priv.bang.ukelonn.beans.TransactionType;

class RegisterPaymentTest {

    @Test
    void testRegisterPayment() throws Exception {
        // Create the request
        Account account = getJadAccount();
        double originalBalance = account.getBalance();
        account.setBalance(0.0);
        List<TransactionType> paymenttypes = getPaymenttypes();
        PerformedTransaction payment = PerformedTransaction.with()
            .account(account)
            .transactionTypeId(paymenttypes.get(0).getId())
            .transactionAmount(account.getBalance())
            .transactionDate(new Date())
            .build();

        // Create the object to be tested
        RegisterPayment resource = new RegisterPayment();

        // Inject fake OSGi service UkelonnService
        UkelonnService ukelonn = mock(UkelonnService.class);
        when(ukelonn.registerPayment(any())).thenReturn(account);
        resource.ukelonn = ukelonn;

        // Run the method under test
        Account result = resource.doRegisterPayment(payment);

        // Check the response
        assertEquals("jad", result.getUsername());
        assertThat(result.getBalance()).isLessThan(originalBalance);
    }
}
