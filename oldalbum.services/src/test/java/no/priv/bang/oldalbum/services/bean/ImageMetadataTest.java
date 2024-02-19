/*
 * Copyright 2020-2024 Steinar Bang
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;

class ImageMetadataTest {

    @Test
    void testCreate() {
        var status = 200;
        var lastModified = new Date();
        var contentType = "image/jpeg";
        var contentLength = 128186;
        var title = "A fine picture";
        var description = "This is a description";
        var bean = ImageMetadata.with()
            .status(status)
            .lastModified(lastModified)
            .contentType(contentType)
            .contentLength(contentLength)
            .title(title)
            .description(description)
            .build();
        assertEquals(status, bean.getStatus());
        assertEquals(lastModified, bean.getLastModified());
        assertEquals(contentType, bean.getContentType());
        assertEquals(contentLength, bean.getContentLength());
        assertThat(bean.getTitle()).isEqualTo(title);
        assertThat(bean.getDescription()).isEqualTo(description);
    }

    @Test
    void testCreateNoArgsConstructor() {
        var bean = ImageMetadata.with().build();
        assertEquals(0, bean.getStatus());
        assertNull(bean.getLastModified());
        assertNull(bean.getContentType());
        assertEquals(0, bean.getContentLength());
        assertNull(bean.getTitle());
        assertNull(bean.getDescription());
    }

    @Test
    void testBuilderDescriptionIsNullOrEmpty() {
        var emptyBuilder = ImageMetadata.with();
        assertTrue(emptyBuilder.descriptionIsNullOrEmpty());
        var builderWithEmptyDescription = ImageMetadata.with().description("");
        assertTrue(builderWithEmptyDescription.descriptionIsNullOrEmpty());
        var builderWithDescription = ImageMetadata.with().description("Some description");
        assertFalse(builderWithDescription.descriptionIsNullOrEmpty());
    }

}
