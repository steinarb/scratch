/*
 * Copyright 2020-2021 Steinar Bang
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

public class ImageMetadata {

    private int status;
    private Date lastModified;
    private String contentType;
    private int contentLength;

    private ImageMetadata() {}

    public int getStatus() {
        return status;
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

    public static ImageMetadataBuilder with() {
        return new ImageMetadataBuilder();
    }

    public static class ImageMetadataBuilder {
        private int status;
        private Date lastModified;
        private String contentType;
        private int contentLength;

        private ImageMetadataBuilder() {}

        public ImageMetadata build() {
            ImageMetadata imageMetadata = new ImageMetadata();
            imageMetadata.status = this.status;
            imageMetadata.lastModified = this.lastModified;
            imageMetadata.contentType = this.contentType;
            imageMetadata.contentLength = this.contentLength;
            return imageMetadata;
        }

        public ImageMetadataBuilder status(int status) {
            this.status = status;
            return this;
        }

        public ImageMetadataBuilder lastModified(Date lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public ImageMetadataBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public ImageMetadataBuilder contentLength(int contentLength) {
            this.contentLength = contentLength;
            return this;
        }
    }

}
