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
package no.priv.bang.oldalbum.services.bean;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;

class AlbumEntryTest {

    @Test
    void testAlbumEntry() {
        var id = 1;
        var parent = 2;
        var path = "/album/bilde01";
        var album = true;
        var title = "Album";
        var description = "This is an album";
        var imageUrl = "https://www.bang.priv.no/sb/pics/moto/vfr96/acirc1.jpg";
        var thumbnailUrl = "https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc1.gif";
        var sort = 1;
        var childcount = 4;
        var lastmodified = new Date(800275785000L);
        var contenttype = "image/jpeg";
        var contentlength = 128186;
        var requirelogin = true;
        var groupByYear = true;
        var bean = AlbumEntry.with()
            .id(id)
            .parent(parent)
            .path(path)
            .album(album)
            .title(title)
            .description(description)
            .imageUrl(imageUrl)
            .thumbnailUrl(thumbnailUrl)
            .sort(sort)
            .lastModified(lastmodified)
            .contentType(contenttype)
            .contentLength(contentlength)
            .requireLogin(requirelogin)
            .groupByYear(groupByYear)
            .childcount(childcount)
            .build();
        assertEquals(id, bean.id());
        assertEquals(parent, bean.parent());
        assertEquals(path, bean.path());
        assertEquals(album, bean.album());
        assertEquals(title, bean.title());
        assertEquals(description, bean.description());
        assertEquals(imageUrl, bean.imageUrl());
        assertEquals(thumbnailUrl, bean.thumbnailUrl());
        assertEquals(sort, bean.sort());
        assertEquals(childcount, bean.childcount());
        assertEquals(lastmodified, bean.lastModified());
        assertEquals(contenttype, bean.contentType());
        assertEquals(contentlength, bean.contentLength());
        assertEquals(requirelogin, bean.requireLogin());
        assertEquals(groupByYear, bean.groupByYear());
    }

    @Test
    void testAlbumEntryNoArgsConstructor() {
        var bean = AlbumEntry.with().build();
        assertEquals(-1, bean.id());
        assertNull(bean.path());
        assertFalse(bean.album());
        assertNull(bean.title());
        assertNull(bean.description());
        assertNull(bean.imageUrl());
        assertNull(bean.thumbnailUrl());
        assertEquals(0, bean.sort());
        assertEquals(0, bean.childcount());
        assertNull(bean.lastModified());
        assertNull(bean.contentType());
        assertEquals(0, bean.contentLength());
        assertFalse(bean.requireLogin());
        assertNull(bean.groupByYear());
    }

    @Test
    void testAlbumEntryCopyBuilder() {
        var originalBean = AlbumEntry.with()
            .id(1)
            .parent(2)
            .path("/album/bilde01")
            .album(true)
            .title("Album")
            .description("This is an album")
            .imageUrl("https://www.bang.priv.no/sb/pics/moto/vfr96/acirc1.jpg")
            .thumbnailUrl("https://www.bang.priv.no/sb/pics/moto/vfr96/icons/acirc1.gif")
            .sort(1)
            .lastModified(new Date(800275785000L))
            .contentType("image/jpeg")
            .contentLength(128186)
            .requireLogin(true)
            .groupByYear(true)
            .childcount(4)
            .build();
        var bean = AlbumEntry.with(originalBean).build();
        assertEquals(originalBean.id(), bean.id());
        assertEquals(originalBean.parent(), bean.parent());
        assertEquals(originalBean.path(), bean.path());
        assertEquals(originalBean.album(), bean.album());
        assertEquals(originalBean.title(), bean.title());
        assertEquals(originalBean.description(), bean.description());
        assertEquals(originalBean.imageUrl(), bean.imageUrl());
        assertEquals(originalBean.thumbnailUrl(), bean.thumbnailUrl());
        assertEquals(originalBean.sort(), bean.sort());
        assertEquals(originalBean.childcount(), bean.childcount());
        assertEquals(originalBean.lastModified(), bean.lastModified());
        assertEquals(originalBean.contentType(), bean.contentType());
        assertEquals(originalBean.contentLength(), bean.contentLength());
        assertEquals(originalBean.requireLogin(), bean.requireLogin());
        assertEquals(originalBean.groupByYear(), bean.groupByYear());
    }

}
