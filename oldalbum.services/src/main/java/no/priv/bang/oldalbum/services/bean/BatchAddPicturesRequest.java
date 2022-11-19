package no.priv.bang.oldalbum.services.bean;
/*
 * Copyright 2022 Steinar Bang
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

public class BatchAddPicturesRequest {

    private int parent;
    private String batchAddUrl;

    public int getParent() {
        return parent;
    }

    public String getBatchAddUrl() {
        return batchAddUrl;
    }

    private BatchAddPicturesRequest() { }

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {

        private Builder() { }

        private int parent;
        private String batchAddUrl;

        public BatchAddPicturesRequest build() {
            BatchAddPicturesRequest bean = new BatchAddPicturesRequest();
            bean.parent = this.parent;
            bean.batchAddUrl = this.batchAddUrl;
            return bean;
        }

        public Builder parent(int parent) {
            this.parent = parent;
            return this;
        }

        public Builder batchAddUrl(String batchAddUrl) {
            this.batchAddUrl = batchAddUrl;
            return this;
        }

    }

}
