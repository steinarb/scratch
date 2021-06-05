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

import java.util.Date;

public class Transaction {
    private int transactionId;
    private Date handletidspunkt;
    private String butikk;
    private int storeId;
    private double belop;

    private Transaction() {}

    public int getTransactionId() {
        return transactionId;
    }

    public Date getHandletidspunkt() {
        return handletidspunkt;
    }

    public String getButikk() {
        return butikk;
    }

    public int getStoreId() {
        return storeId;
    }

    public double getBelop() {
        return belop;
    }

    @Override
    public String toString() {
        return "Transaction [transactionId=" + transactionId + ", handletidspunkt=" + handletidspunkt + ", butikk="
                + butikk + ", storeId=" + storeId + ", belop=" + belop + "]";
    }

    public static TransactionBuilder with() {
        return new TransactionBuilder();
    }

    public static class TransactionBuilder {
        private int transactionId = -1;
        private Date handletidspunkt;
        private String butikk;
        private int storeId = -1;
        private double belop;

        private TransactionBuilder() {}

        public Transaction build() {
            Transaction transaction = new Transaction();
            transaction.transactionId = this.transactionId;
            transaction.handletidspunkt = this.handletidspunkt;
            transaction.butikk = this.butikk;
            transaction.storeId = this.storeId;
            transaction.belop = this.belop;
            return transaction;
        }

        public TransactionBuilder transactionId(int transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public TransactionBuilder handletidspunkt(Date handletidspunkt) {
            this.handletidspunkt = handletidspunkt;
            return this;
        }

        public TransactionBuilder butikk(String butikk) {
            this.butikk = butikk;
            return this;
        }

        public TransactionBuilder storeId(int storeId) {
            this.storeId = storeId;
            return this;
        }

        public TransactionBuilder belop(double belop) {
            this.belop = belop;
            return this;
        }
    }
}
