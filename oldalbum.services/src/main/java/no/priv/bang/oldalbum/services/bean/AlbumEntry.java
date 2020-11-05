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

import java.util.Date;

public class AlbumEntry {

    private int id;
    private int parent;
    private String path;
    private boolean album;
    private String title;
    private String description;
    private String imageUrl;
    private String thumbnailUrl;
    private int sort;
    private Date lastModified;
    private String contentType;
    private int contentLength;

    private int childcount;

    public AlbumEntry(int id, int parent, String path, boolean album, String title, String description, String imageUrl, String thumbnailUrl, int sort, Date lastmodified, String contenttype, int contentlength, int childcount) { // NOSONAR
        this.id = id;
        this.parent = parent;
        this.path = path;
        this.album = album;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.sort  = sort;
        this.lastModified = lastmodified;
        this.contentType = contenttype;
        this.contentLength = contentlength;
        this.childcount = childcount;
    }

    public AlbumEntry() {
        // jackson requires a no-args constructor
        id = -1;
    }

    public int getId() {
        return id;
    }

    public int getParent() {
        return parent;
    }

    public String getPath() {
        return path;
    }

    public boolean isAlbum() {
        return album;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public int getSort() {
        return sort;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public String getContentType() {
        return contentType;
    }

    public int getContentLength() {
        return contentLength;
    }

    public int getChildcount() {
        return childcount;
    }

}
