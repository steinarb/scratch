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

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.junit.jupiter.api.Test;
import no.priv.bang.oldalbum.services.OldAlbumException;
import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;
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
        int albumEntryId = 9;
        var imageUrl = "https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg";
        var lastModifiedDate = new Date();
        var entry = AlbumEntry.with().id(albumEntryId).album(false).path("/moto/places/grava1").imageUrl(imageUrl).lastModified(lastModifiedDate).build();
        var streamingOutput = mock(StreamingOutput.class);
        var backend = mock(OldAlbumService.class);
        when(backend.getAlbumEntry(anyInt())).thenReturn(Optional.of(entry));
        when(backend.downloadAlbumEntry(anyInt())).thenReturn(streamingOutput);
        ImageResource resource = new ImageResource();
        resource.oldalbum = backend;

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

    @Test
    void testFindFilenameFromAlbumEntryPathWhenEntryIsAlbum() {
        var entry = AlbumEntry.with().album(true).path("/moto/places").build();
        var resource = new ImageResource();
        var filename = resource.findFilenameFromAlbumEntryPath(entry);
        assertEquals("places.zip", filename);
    }

    @Test
    void testFindFilenameFromAlbumEntryPathWhenEntryIsImage() {
        var entry = AlbumEntry.with().album(false).path("/moto/places/grava1").imageUrl("https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg").build();
        var resource = new ImageResource();
        var filename = resource.findFilenameFromAlbumEntryPath(entry);
        assertEquals("grava1.jpg", filename);
    }

}
