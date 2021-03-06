/*
 * Copyright 2020-2021 Steinar Bang
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
package no.priv.bang.oldalbum.web.api.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.Test;

import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.ImageMetadata;
import no.priv.bang.oldalbum.services.bean.ImageRequest;

class ImageResourceTest {

    @Test
    void testGetMetadata() {
        OldAlbumService backendService = mock(OldAlbumService.class);
        ImageMetadata mockMetadata = ImageMetadata.with()
            .status(200)
            .lastModified(new Date())
            .contentType("image/jpeg")
            .contentLength(128000)
            .build();
        when(backendService.readMetadata(anyString())).thenReturn(mockMetadata);
        ImageResource resource = new ImageResource();
        resource.oldalbum = backendService;
        String url = "https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg";
        ImageMetadata metadata = resource.getMetadata(ImageRequest.with().url(url).build());
        assertEquals(200, metadata.getStatus());
        assertThat(metadata.getLastModified()).isAfter(Date.from(Instant.EPOCH));
        assertEquals("image/jpeg", metadata.getContentType());
        assertThat(metadata.getContentLength()).isPositive();
    }

}
