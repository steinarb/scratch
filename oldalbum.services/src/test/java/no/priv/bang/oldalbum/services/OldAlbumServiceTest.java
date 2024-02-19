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
package no.priv.bang.oldalbum.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import org.junit.jupiter.api.Test;

import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.oldalbum.services.bean.BatchAddPicturesRequest;

class OldAlbumServiceTest {

    @Test
    void testOldAlbumService() {
        var service = mock(OldAlbumService.class);
        var routes = service.fetchAllRoutes(null, false);
        assertEquals(0, routes.size());
        var paths = service.getPaths(false);
        assertEquals(0, paths.size());
        var albumEntryId = 42;
        var albumentry = service.getAlbumEntry(albumEntryId);
        assertTrue(albumentry.isEmpty());
        var path = "/moto/grava";
        var entry = service.getAlbumEntryFromPath(path);
        assertNull(entry);
        var parent = 2;
        var children = service.getChildren(parent);
        assertEquals(0, children.size());
        var modifiedEntry = AlbumEntry.with().build();
        var updatedRoutesOnModifiedEntry = service.updateEntry(modifiedEntry);
        assertEquals(0, updatedRoutesOnModifiedEntry.size());
        var addedEntry = AlbumEntry.with().build();
        var updatedRoutesOnAddedEntry = service.addEntry(addedEntry);
        assertEquals(0, updatedRoutesOnAddedEntry.size());
        var deletedEntry = AlbumEntry.with().build();
        var updatedRoutesOnDelete = service.deleteEntry(deletedEntry);
        assertEquals(0, updatedRoutesOnDelete.size());
        var selection = Arrays.asList(7);
        var updatedRoutesOnSelectionDelete = service.deleteSelectedEntries(selection);
        assertEquals(0, updatedRoutesOnSelectionDelete.size());
        var movedEntry = AlbumEntry.with().build();
        var updatedRoutesOnEntryMovedUp = service.moveEntryUp(movedEntry);
        assertEquals(0, updatedRoutesOnEntryMovedUp.size());
        var updatedRoutesOnEntryMovedDown = service.moveEntryDown(movedEntry);
        assertEquals(0, updatedRoutesOnEntryMovedDown.size());
        var dumpedSql = service.dumpDatabaseSql(null, false);
        assertNull(dumpedSql);
        var streamingOutput = service.downloadAlbumEntry(albumEntryId);
        assertNull(streamingOutput);
        var selectedentryIds = Collections.singletonList(albumEntryId);
        var selectionStreamingOutput = service.downloadAlbumEntrySelection(selectedentryIds);
        assertNull(selectionStreamingOutput);
        var imageUrl = "https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg";
        var metadata = service.readMetadata(imageUrl);
        assertNull(metadata);

        var batchAddPicturesRequest = BatchAddPicturesRequest.with().build();
        var updatedRoutesAfterBatchAdd = service.batchAddPictures(batchAddPicturesRequest);
        assertThat(updatedRoutesAfterBatchAdd).isEmpty();

        var updatedRoutesAfterSort = service.sortByDate(1);
        assertThat(updatedRoutesAfterSort).isEmpty();

        var defaultLocale = service.defaultLocale();
        assertNull(defaultLocale);
        var availableLocales = service.availableLocales();
        assertThat(availableLocales).isEmpty();
        var locale = Locale.UK;
        var texts = service.displayTexts(locale);
        assertThat(texts).isEmpty();
        var key = "loggedout";
        var text = service.displayText(key, locale.toString());
        assertNull(text);
    }

}
