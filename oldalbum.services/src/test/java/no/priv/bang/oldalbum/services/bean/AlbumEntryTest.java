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
package no.priv.bang.oldalbum.services.bean;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AlbumEntryTest {

    @Test
    void testAlbumEntry() {
        int id = 1;
        int parent = 2;
        String path = "/album/bilde01";
        boolean album = true;
        String title = "Album";
        String description = "This is an album";
        String imageUrl = "https://www.bang.priv.no/sb/pics/moto/vfr96/acirc1.jpg";
        String thumbnailUrl = "https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc1.gif";
        int sort = 1;
        int childcount = 4;
        AlbumEntry bean = new AlbumEntry(id, parent, path, album, title, description, imageUrl, thumbnailUrl, sort, childcount);
        assertEquals(id, bean.getId());
        assertEquals(parent, bean.getParent());
        assertEquals(path, bean.getPath());
        assertEquals(album, bean.isAlbum());
        assertEquals(title, bean.getTitle());
        assertEquals(description, bean.getDescription());
        assertEquals(imageUrl, bean.getImageUrl());
        assertEquals(thumbnailUrl, bean.getThumbnailUrl());
        assertEquals(sort, bean.getSort());
        assertEquals(childcount, bean.getChildcount());
    }

    @Test
    void testAlbumEntryNoArgsConstructor() {
        AlbumEntry bean = new AlbumEntry();
        assertEquals(-1, bean.getId());
        assertNull(bean.getPath());
        assertFalse(bean.isAlbum());
        assertNull(bean.getTitle());
        assertNull(bean.getDescription());
        assertNull(bean.getImageUrl());
        assertNull(bean.getThumbnailUrl());
        assertEquals(0, bean.getSort());
        assertEquals(0, bean.getChildcount());
    }

}
