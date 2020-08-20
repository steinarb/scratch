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
package no.priv.bang.oldalbum.web.api.resources;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;

class AlbumentryResourceTest {

    @Test
    void testModifyalbum() {
        AlbumEntry modifiedAlbum = new AlbumEntry(2, 1, "/moto/", true, "Album has been updated", "This is an updated description", null, null, 1, null, null, 0, 2);
        AlbumentryResource resource = new AlbumentryResource();
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.updateEntry(any())).thenReturn(Arrays.asList(modifiedAlbum));
        resource.oldalbum = oldalbum;
        List<AlbumEntry> allroutes = resource.modifyalbum(modifiedAlbum);
        AlbumEntry updatedAlbum = allroutes.stream().filter(r -> r.getId() == 2).findFirst().get();
        assertEquals(modifiedAlbum.getTitle(), updatedAlbum.getTitle());
        assertEquals(modifiedAlbum.getDescription(), updatedAlbum.getDescription());
    }

    @Test
    void testAddalbum() {
        AlbumEntry albumToAdd = new AlbumEntry(0, 1, "/newalbum/", true, "A new album", "A new album for new pictures", null, null, 2, null, null, 0, 0);
        AlbumentryResource resource = new AlbumentryResource();
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.addEntry(any())).thenReturn(Arrays.asList(albumToAdd));
        resource.oldalbum = oldalbum;
        List<AlbumEntry> allroutes = resource.addalbum(albumToAdd);
        AlbumEntry addedAlbum = allroutes.stream().filter(r -> "/newalbum/".equals(r.getPath())).findFirst().get();
        assertEquals(albumToAdd.getTitle(), addedAlbum.getTitle());
        assertEquals(albumToAdd.getDescription(), addedAlbum.getDescription());
    }

    @Test
    void testModifypicture() {
        AlbumEntry modifiedPicture = new AlbumEntry(2, 1, "/moto/vfr96/acirc1", false, "Picture has been updated", "This is an updated picture description", "https://www.bang.priv.no/sb/pics/moto/vfr96/acirc1.jpg", "https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc1.gif", 1, new Date(), "image/jpeg", 71072, 0);
        AlbumentryResource resource = new AlbumentryResource();
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.updateEntry(any())).thenReturn(Arrays.asList(modifiedPicture));
        resource.oldalbum = oldalbum;
        List<AlbumEntry> allroutes = resource.modifypicture(modifiedPicture);
        AlbumEntry updatedPicture = allroutes.stream().filter(r -> r.getId() == 2).findFirst().get();
        assertEquals(modifiedPicture.getTitle(), updatedPicture.getTitle());
        assertEquals(modifiedPicture.getDescription(), updatedPicture.getDescription());
    }

    @Test
    void testAddpicture() {
        AlbumEntry pictureToAdd = new AlbumEntry(2, 1, "/moto/vfr96/acirc1", false, "Picture has been updated", "This is an updated picture description", "https://www.bang.priv.no/sb/pics/moto/vfr96/acirc1.jpg", "https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc1.gif", 1, new Date(), "image/jpeg", 71072, 0);
        AlbumentryResource resource = new AlbumentryResource();
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.addEntry(any())).thenReturn(Arrays.asList(pictureToAdd));
        resource.oldalbum = oldalbum;
        List<AlbumEntry> allroutes = resource.addpicture(pictureToAdd);
        AlbumEntry updatedPicture = allroutes.stream().filter(r -> "/moto/vfr96/acirc1".equals(r.getPath())).findFirst().get();
        assertEquals(pictureToAdd.getTitle(), updatedPicture.getTitle());
        assertEquals(pictureToAdd.getDescription(), updatedPicture.getDescription());
    }

    @Test
    void testDeleteEntry() {
        AlbumEntry pictureToDelete = new AlbumEntry(7, 3, "/oldalbum/moto/places/grava3", false, "", "Tyrigrava, view from the north. Lotsa bikes here too", "https://www.bang.priv.no/sb/pics/moto/places/grava3.jpg", "https://www.bang.priv.no/sb/pics/moto/places/icons/grava3.gif", 1, new Date(), "image/jpeg", 71072, 0);
        AlbumentryResource resource = new AlbumentryResource();
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        resource.oldalbum = oldalbum;
        List<AlbumEntry> allroutes = resource.deleteEntry(pictureToDelete);
        assertEquals(0, allroutes.size());
    }

    @Test
    void testMoveEntryUp() {
        AlbumEntry albumToMove = new AlbumEntry(2, 1, "/moto/", true, "Album has been updated", "This is an updated description", null, null, 2, null, null, 0, 2);
        AlbumEntry movedAlbum = new AlbumEntry(2, 1, "/moto/", true, "Album has been updated", "This is an updated description", null, null, 1, null, null, 0, 2);
        AlbumentryResource resource = new AlbumentryResource();
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.moveEntryUp(any())).thenReturn(Arrays.asList(movedAlbum));
        resource.oldalbum = oldalbum;
        List<AlbumEntry> allroutes = resource.moveEntryUp(albumToMove);
        AlbumEntry updatedAlbum = allroutes.stream().filter(r -> r.getId() == 2).findFirst().get();
        assertThat(albumToMove.getSort()).isGreaterThan(updatedAlbum.getSort());
    }

    @Test
    void testMoveEntryDown() {
        AlbumEntry albumToMove = new AlbumEntry(2, 1, "/moto/", true, "Album has been updated", "This is an updated description", null, null, 1, null, null, 0, 2);
        AlbumEntry movedAlbum = new AlbumEntry(2, 1, "/moto/", true, "Album has been updated", "This is an updated description", null, null, 2, null, null, 0, 2);
        AlbumentryResource resource = new AlbumentryResource();
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.moveEntryDown(any())).thenReturn(Arrays.asList(movedAlbum));
        resource.oldalbum = oldalbum;
        List<AlbumEntry> allroutes = resource.moveEntryDown(albumToMove);
        AlbumEntry updatedAlbum = allroutes.stream().filter(r -> r.getId() == 2).findFirst().get();
        assertThat(albumToMove.getSort()).isLessThan(updatedAlbum.getSort());
    }

}
