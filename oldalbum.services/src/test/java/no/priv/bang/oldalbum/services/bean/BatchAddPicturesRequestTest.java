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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BatchAddPicturesRequestTest {

    @Test
    void testBuildBean() {
        int parent = 4;
        String batchAddUrl = "http://lorenzo.hjemme.lan/bilder/202349_001396/Export%20JPG%2016Base/";
        var bean = BatchAddPicturesRequest.with()
            .parent(parent)
            .batchAddUrl(batchAddUrl)
            .build();
        assertEquals(parent, bean.getParent());
        assertEquals(batchAddUrl, bean.getBatchAddUrl());
    }

    @Test
    void testBuildBeanWithDefaults() {
        var bean = BatchAddPicturesRequest.with().build();
        assertEquals(0, bean.getParent());
        assertNull(bean.getBatchAddUrl());
    }

}
