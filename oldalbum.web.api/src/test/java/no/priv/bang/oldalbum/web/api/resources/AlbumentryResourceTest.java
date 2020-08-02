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
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;

class AlbumentryResourceTest {

    @Test
    void testModifyalbum() {
        AlbumEntry modifiedAlbum = new AlbumEntry(2, 1, "/moto/", true, "Album has been updated", "This is an updated description", null, null);
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
    void testModifypicture() {
        AlbumEntry modifiedPicture = new AlbumEntry(2, 1, "/moto/vfr96/acirc1", true, "Picture has been updated", "This is an updated picture description", "https://www.bang.priv.no/sb/pics/moto/vfr96/acirc1.jpg", "https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc1.gif");
        AlbumentryResource resource = new AlbumentryResource();
        OldAlbumService oldalbum = mock(OldAlbumService.class);
        when(oldalbum.updateEntry(any())).thenReturn(Arrays.asList(modifiedPicture));
        resource.oldalbum = oldalbum;
        List<AlbumEntry> allroutes = resource.modifypicture(modifiedPicture);
        AlbumEntry updatedAlbum = allroutes.stream().filter(r -> r.getId() == 2).findFirst().get();
        assertEquals(modifiedPicture.getTitle(), updatedAlbum.getTitle());
        assertEquals(modifiedPicture.getDescription(), updatedAlbum.getDescription());
    }

}
