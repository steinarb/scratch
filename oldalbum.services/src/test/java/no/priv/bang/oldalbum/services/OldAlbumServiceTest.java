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
package no.priv.bang.oldalbum.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.oldalbum.services.bean.BatchAddPicturesRequest;
import no.priv.bang.oldalbum.services.bean.ImageMetadata;
import no.priv.bang.oldalbum.services.bean.LocaleBean;

class OldAlbumServiceTest {

    @Test
    void testOldAlbumService() {
        OldAlbumService service = mock(OldAlbumService.class);
        List<AlbumEntry> routes = service.fetchAllRoutes(null, false);
        assertEquals(0, routes.size());
        List<String> paths = service.getPaths(false);
        assertEquals(0, paths.size());
        String path = "/moto/grava";
        AlbumEntry entry = service.getAlbumEntryFromPath(path);
        assertNull(entry);
        int parent = 2;
        List<AlbumEntry> children = service.getChildren(parent);
        assertEquals(0, children.size());
        AlbumEntry modifiedEntry = AlbumEntry.with().build();
        List<AlbumEntry> updatedRoutesOnModifiedEntry = service.updateEntry(modifiedEntry);
        assertEquals(0, updatedRoutesOnModifiedEntry.size());
        AlbumEntry addedEntry = AlbumEntry.with().build();
        List<AlbumEntry> updatedRoutesOnAddedEntry = service.addEntry(addedEntry);
        assertEquals(0, updatedRoutesOnAddedEntry.size());
        AlbumEntry deletedEntry = AlbumEntry.with().build();
        List<AlbumEntry> updatedRoutesOnDelete = service.deleteEntry(deletedEntry);
        assertEquals(0, updatedRoutesOnDelete.size());
        AlbumEntry movedEntry = AlbumEntry.with().build();
        List<AlbumEntry> updatedRoutesOnEntryMovedUp = service.moveEntryUp(movedEntry);
        assertEquals(0, updatedRoutesOnEntryMovedUp.size());
        List<AlbumEntry> updatedRoutesOnEntryMovedDown = service.moveEntryDown(movedEntry);
        assertEquals(0, updatedRoutesOnEntryMovedDown.size());
        String dumpedSql = service.dumpDatabaseSql(null, false);
        assertNull(dumpedSql);
        String imageUrl = "https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg";
        ImageMetadata metadata = service.readMetadata(imageUrl);
        assertNull(metadata);

        var batchAddPicturesRequest = BatchAddPicturesRequest.with().build();
        List<AlbumEntry> updatedRoutesAfterBatchAdd = service.batchAddPictures(batchAddPicturesRequest);
        assertThat(updatedRoutesAfterBatchAdd).isEmpty();

        List<AlbumEntry> updatedRoutesAfterSort = service.sortByDate(1);
        assertThat(updatedRoutesAfterSort).isEmpty();

        Locale defaultLocale = service.defaultLocale();
        assertNull(defaultLocale);
        List<LocaleBean> availableLocales = service.availableLocales();
        assertThat(availableLocales).isEmpty();
        Locale locale = Locale.UK;
        Map<String, String> texts = service.displayTexts(locale);
        assertThat(texts).isEmpty();
        String key = "loggedout";
        String text = service.displayText(key, locale.toString());
        assertNull(text);
    }

}
