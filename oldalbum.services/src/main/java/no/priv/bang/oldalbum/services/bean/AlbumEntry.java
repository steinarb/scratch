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

import java.util.Date;

import no.priv.bang.beans.immutable.Immutable;

public class AlbumEntry extends Immutable { // NOSONAR Immutable handles added fields

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
    private boolean requireLogin;
    private Boolean groupByYear;

    private AlbumEntry() {}

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

    public boolean isRequireLogin() {
        return requireLogin;
    }

    public Boolean getGroupByYear() {
        return groupByYear;
    }

    @Override
    public String toString() {
        return "AlbumEntry [id=" + id + ", parent=" + parent + ", path=" + path + ", album=" + album + ", title="
            + title + ", description=" + description + ", imageUrl=" + imageUrl + ", thumbnailUrl=" + thumbnailUrl
            + ", sort=" + sort + ", lastModified=" + lastModified + ", contentType=" + contentType
            + ", contentLength=" + contentLength + ", childcount=" + childcount + ", requireLogin=" + requireLogin
            + ", groupByYear=" + groupByYear + "]";
    }

    public static Builder with() {
        return new Builder();
    }

    public static Builder with(AlbumEntry albumEntry) {
        var builder = new Builder();
        builder.id = albumEntry.id;
        builder.parent = albumEntry.parent;
        builder.path = albumEntry.path;
        builder.album = albumEntry.album;
        builder.title = albumEntry.title;
        builder.description = albumEntry.description;
        builder.imageUrl = albumEntry.imageUrl;
        builder.thumbnailUrl = albumEntry.thumbnailUrl;
        builder.sort  = albumEntry.sort;
        builder.lastModified = albumEntry.lastModified;
        builder.contentType = albumEntry.contentType;
        builder.contentLength = albumEntry.contentLength;
        builder.requireLogin = albumEntry.requireLogin;
        builder.groupByYear = albumEntry.groupByYear;
        builder.childcount = albumEntry.childcount;
        return builder;
    }

    public static class Builder {
        private int id = -1;
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
        private boolean requireLogin;
        private Boolean groupByYear;

        private Builder() {}

        public AlbumEntry build() {
            var albumEntry = new AlbumEntry();
            albumEntry.id = this.id;
            albumEntry.parent = this.parent;
            albumEntry.path = this.path;
            albumEntry.album = this.album;
            albumEntry.title = this.title;
            albumEntry.description = this.description;
            albumEntry.imageUrl = this.imageUrl;
            albumEntry.thumbnailUrl = this.thumbnailUrl;
            albumEntry.sort  = this.sort;
            albumEntry.lastModified = this.lastModified;
            albumEntry.contentType = this.contentType;
            albumEntry.contentLength = this.contentLength;
            albumEntry.requireLogin = this.requireLogin;
            albumEntry.groupByYear = this.groupByYear;
            albumEntry.childcount = this.childcount;
            return albumEntry;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder parent(int parent) {
            this.parent = parent;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder album(boolean album) {
            this.album = album;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder thumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
            return this;
        }

        public Builder sort(int sort) {
            this.sort = sort;
            return this;
        }

        public Builder lastModified(Date lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder contentLength(int contentLength) {
            this.contentLength = contentLength;
            return this;
        }

        public Builder childcount(int childcount) {
            this.childcount = childcount;
            return this;
        }

        public Builder requireLogin(boolean requirelogin) {
            this.requireLogin = requirelogin;
            return this;
        }

        public Builder groupByYear(Boolean groupByYear) {
            this.groupByYear = groupByYear;
            return this;
        }
    }

}
