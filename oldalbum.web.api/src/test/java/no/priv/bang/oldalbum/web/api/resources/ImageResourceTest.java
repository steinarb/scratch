/*
 * Copyright 2020-2023 Steinar Bang
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

import java.nio.file.Path;
import java.time.Instant;
import java.util.Date;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;
import no.priv.bang.oldalbum.services.OldAlbumException;
import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.ImageMetadata;
import no.priv.bang.oldalbum.services.bean.ImageRequest;
import no.priv.bang.osgi.service.mocks.logservice.MockLogService;

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

    @Test
    void testDownloadAlbumEntry() {
        var imageUrl = "https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg";
        var tempDir = Path.of(System.getProperty("java.io.tmpdir"));
        var fileName = findFileNamePartOfUrl(imageUrl);
        var tempfile = tempDir.resolve(fileName).toFile();
        var backend = mock(OldAlbumService.class);
        when(backend.downloadAlbumEntry(anyInt())).thenReturn(tempfile);
        ImageResource resource = new ImageResource();
        resource.oldalbum = backend;

        int albumEntryId = 9;
        Response response = resource.downloadAlbumEntry(albumEntryId);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testDownloadAlbumEntryWhenFileNotFound() {
        var backend = mock(OldAlbumService.class);
        when(backend.downloadAlbumEntry(anyInt())).thenThrow(OldAlbumException.class);
        var logservice = new MockLogService();
        ImageResource resource = new ImageResource();
        resource.oldalbum = backend;
        resource.setLogservice(logservice);

        int albumEntryId = 9;
        assertThat(logservice.getLogmessages()).isEmpty();
        Response response = resource.downloadAlbumEntry(albumEntryId);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

    private String findFileNamePartOfUrl(String imageUrl) {
        var urlComponents = imageUrl.split("/");
        return urlComponents[urlComponents.length - 1];
    }

}
