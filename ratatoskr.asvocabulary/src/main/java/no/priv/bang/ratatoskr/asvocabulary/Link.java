/*
 * Copyright 2024 Steinar Bang
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
package no.priv.bang.ratatoskr.asvocabulary;

import com.fasterxml.jackson.annotation.JsonGetter;

public record Link (
    @JsonGetter("@context") Object context,
    ActivityStreamObjectType type,
    String href,
    String rel,
    String mediaType,
    String name,
    String hreflang,
    long height,
    long width) implements LinkOrObject
{

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {
        private Object context = "https://www.w3.org/ns/activitystreams";
        private ActivityStreamObjectType type = ActivityStreamObjectType.Link;
        private String href;
        private String rel;
        private String mediaType;
        private String name;
        private String hreflang;
        private long height;
        private long width;

        public Link build() {
            return new Link(
                context,
                type,
                href,
                rel,
                mediaType,
                name,
                hreflang,
                height,
                width);
        }

        public Builder href(String href) {
            this.href = href;
            return this;
        }

        public Builder rel(String rel) {
            this.rel = rel;
            return this;
        }

        public Builder mediaType(String mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder hreflang(String hreflang) {
            this.hreflang = hreflang;
            return this;
        }

        public Builder height(long height) {
            this.height = height;
            return this;
        }

        public Builder width(long width) {
            this.width = width;
            return this;
        }

    }

}
