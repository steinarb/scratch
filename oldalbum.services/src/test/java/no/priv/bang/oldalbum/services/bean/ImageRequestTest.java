/*
 * Copyright 2020 Steinar Bang
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
package no.priv.bang.oldalbum.services.bean;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ImageRequestTest {

    @Test
    void test() {
        String url = "https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg";
        ImageRequest bean = new ImageRequest(url);
        assertEquals(url, bean.getUrl());
    }

    @Test
    void testNoargsConstructor() {
        ImageRequest bean = new ImageRequest();
        assertNull(bean.getUrl());
    }

}
