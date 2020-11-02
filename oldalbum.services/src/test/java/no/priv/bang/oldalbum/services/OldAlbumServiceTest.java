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
package no.priv.bang.oldalbum.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.oldalbum.services.bean.ImageMetadata;

class OldAlbumServiceTest {

    @Test
    void testOldAlbumService() {
        OldAlbumService service = mock(OldAlbumService.class);
        List<AlbumEntry> routes = service.fetchAllRoutes();
        assertEquals(0, routes.size());
        List<String> paths = service.getPaths();
        assertEquals(0, paths.size());
        String path = "/moto/grava";
        AlbumEntry entry = service.getAlbumEntryFromPath(path);
        assertNull(entry);
        int parent = 2;
        List<AlbumEntry> children = service.getChildren(parent);
        assertEquals(0, children.size());
        AlbumEntry modifiedEntry = new AlbumEntry();
        List<AlbumEntry> updatedRoutesOnModifiedEntry = service.updateEntry(modifiedEntry);
        assertEquals(0, updatedRoutesOnModifiedEntry.size());
        AlbumEntry addedEntry = new AlbumEntry();
        List<AlbumEntry> updatedRoutesOnAddedEntry = service.addEntry(addedEntry);
        assertEquals(0, updatedRoutesOnAddedEntry.size());
        AlbumEntry deletedEntry = new AlbumEntry();
        List<AlbumEntry> updatedRoutesOnDelete = service.deleteEntry(deletedEntry);
        assertEquals(0, updatedRoutesOnDelete.size());
        AlbumEntry movedEntry = new AlbumEntry();
        List<AlbumEntry> updatedRoutesOnEntryMovedUp = service.moveEntryUp(movedEntry);
        assertEquals(0, updatedRoutesOnEntryMovedUp.size());
        List<AlbumEntry> updatedRoutesOnEntryMovedDown = service.moveEntryDown(movedEntry);
        assertEquals(0, updatedRoutesOnEntryMovedDown.size());
        String dumpedSql = service.dumpDatabaseSql();
        assertNull(dumpedSql);
        String imageUrl = "https://www.bang.priv.no/sb/pics/moto/places/grava1.jpg";
        ImageMetadata metadata = service.readMetadata(imageUrl);
        assertNull(metadata);
    }

}
