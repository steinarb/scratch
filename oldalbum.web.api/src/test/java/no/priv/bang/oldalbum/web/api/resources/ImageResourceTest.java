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
package no.priv.bang.oldalbum.web.api.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

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
        var backendService = mock(OldAlbumService.class);
        var mockMetadata = ImageMetadata.with()
            .status(200)
            .lastModified(new Date())
            .contentType("image/jpeg")
            .contentLength(128000)
            .build();
        when(backendService.readMetadata(anyString())).thenReturn(mockMetadata);
        var resource = new ImageResource();
        resource.oldalbum = backendService;
        var url = "https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg";
        var metadata = resource.getMetadata(ImageRequest.with().url(url).build());
        assertEquals(200, metadata.status());
        assertThat(metadata.lastModified()).isAfter(Date.from(Instant.EPOCH));
        assertEquals("image/jpeg", metadata.contentType());
        assertThat(metadata.contentLength()).isPositive();
    }

    @Test
    void testDownloadAlbumEntry() {
        var albumEntryId = 9;
        var imageUrl = "https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg";
        var lastModifiedDate = new Date();
        var entry = AlbumEntry.with().id(albumEntryId).album(false).path("/moto/places/grava1").imageUrl(imageUrl).lastModified(lastModifiedDate).build();
        var streamingOutput = mock(StreamingOutput.class);
        var backend = mock(OldAlbumService.class);
        when(backend.getAlbumEntry(anyInt())).thenReturn(Optional.of(entry));
        when(backend.downloadAlbumEntry(anyInt())).thenReturn(streamingOutput);
        var resource = new ImageResource();
        resource.oldalbum = backend;

        var response = resource.downloadAlbumEntry(albumEntryId);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testDownloadAlbumEntryWhenFileNotFound() {
        var backend = mock(OldAlbumService.class);
        when(backend.downloadAlbumEntry(anyInt())).thenThrow(OldAlbumException.class);
        var logservice = new MockLogService();
        var resource = new ImageResource();
        resource.oldalbum = backend;
        resource.setLogservice(logservice);

        var albumEntryId = 9;
        assertThat(logservice.getLogmessages()).isEmpty();
        var response = resource.downloadAlbumEntry(albumEntryId);
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

    @Test
    void testDownloadAlbumEntrySelection() {
        var albumId = 4;
        var album = AlbumEntry.with().id(albumId).parent(2).album(true).path("/moto/vfr96/").title("My VFR750F in 1996").description("In may 1996, I bought a 1995 VFR750F, registered in october 1995, with 3400km on the clock when I bought it. This picture archive, contains pictures from my first (but hopefully not last) season, on a VFR.").build();
        var albumEntryId = 9;
        var imageUrl = "https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg";
        var lastModifiedDate = new Date();
        var entry = AlbumEntry.with().id(albumEntryId).album(false).path("/moto/places/grava1").imageUrl(imageUrl).lastModified(lastModifiedDate).build();
        var selectedentryIds = Collections.singletonList(albumEntryId);
        var streamingOutput = mock(StreamingOutput.class);
        var backend = mock(OldAlbumService.class);
        when(backend.getAlbumEntry(albumId)).thenReturn(Optional.of(album));
        when(backend.getAlbumEntry(albumEntryId)).thenReturn(Optional.of(entry));
        when(backend.downloadAlbumEntry(anyInt())).thenReturn(streamingOutput);
        var resource = new ImageResource();
        resource.oldalbum = backend;

        var response = resource.downloadAlbumEntrySelection(albumId, selectedentryIds);
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testDownloadAlbumEntrySelectionWhenParentAlbumFound() {
        var backend = mock(OldAlbumService.class);
        when(backend.getAlbumEntry(anyInt())).thenReturn(Optional.empty());
        var logservice = new MockLogService();
        var resource = new ImageResource();
        resource.oldalbum = backend;
        resource.setLogservice(logservice);

        var albumId = 4;
        var albumEntryId = 9;
        var selectedentryIds = Collections.singletonList(albumEntryId);
        assertThat(logservice.getLogmessages()).isEmpty();
        var response = resource.downloadAlbumEntrySelection(albumId, selectedentryIds);
        assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertThat(logservice.getLogmessages()).isNotEmpty();
    }

}
