/*
 * Copyright 2021 Steinar Bang
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
package no.priv.bang.handlelapp.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ArticleTest {

    @Test
    void test() {
        int articleid = 123;
        String name = "Soap";
        int categoryid = 321;
        Article bean = Article.with()
            .articleid(articleid)
            .name(name)
            .categoryid(categoryid)
            .build();
        assertNotNull(bean);
        assertEquals(articleid, bean.getArticleid());
        assertEquals(name, bean.getName());
        assertEquals(categoryid, bean.getCategoryid());
    }

}
