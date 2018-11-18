/*
 * Copyright 2018 Steinar Bang
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
package no.bang.priv.handlereg.services;

import java.util.Date;

public class Transaction {
    int transactionId = -1;
    Date handletidspunkt;
    String butikk;
    double belop;

    public Transaction() {
        // No-args constructor required by jackson
    }

    public Transaction(int transactionId, Date handletidspunkt, String butikk, double belop) {
        super();
        this.transactionId = transactionId;
        this.handletidspunkt = handletidspunkt;
        this.butikk = butikk;
        this.belop = belop;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public Date getHandletidspunkt() {
        return handletidspunkt;
    }

    public String getButikk() {
        return butikk;
    }

    public double getBelop() {
        return belop;
    }
}
