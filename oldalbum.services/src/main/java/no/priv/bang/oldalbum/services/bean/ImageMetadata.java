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

public class ImageMetadata {

    private int status;
    private Date lastModified;
    private String contentType;
    private int contentLength;
    private String description;

    public ImageMetadata(int status, Date lastModified, String contentType, int contentLength, String description) {
        this.status = status;
        this.lastModified = lastModified;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.description = description;
    }

    public ImageMetadata() {
        // NoArgs constructor required by jackson
    }

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

    public String getDescription() {
        return description;
    }

}
