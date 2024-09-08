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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record Person(
    @JsonGetter("@context") Object context,
    ActivityStreamObjectType type,
    String id,
    String name,
    String summary,
    String inbox,
    String outbox,
    String following,
    String followers,
    String liked,
    String streams,
    String preferredUsername,
    EndPoints endpoints) implements Actor
{

    public static Builder with() {
        return new Builder();
    }

    public static class Builder {
        private Object context = "https://www.w3.org/ns/activitystreams";
        private String id;
        private String name;
        private String summary;
        private ActivityStreamObjectType type;
        private String inbox;
        private String outbox;
        private String following;
        private String followers;
        private String liked;
        private String streams;
        private String preferredUsername;
        private EndPoints endpoints;

        public Person build() {
            return new Person(
                context,
                type,
                id,
                name,
                summary,
                inbox,
                outbox,
                following,
                followers,
                liked,
                streams,
                preferredUsername,
                endpoints);
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder type(ActivityStreamObjectType type) {
            this.type = type;
            return this;
        }

        public Builder inbox(String inbox) {
            this.inbox = inbox;
            return this;
        }

        public Builder outbox(String outbox) {
            this.outbox = outbox;
            return this;
        }

        public Builder following(String following) {
            this.following = following;
            return this;
        }

        public Builder followers(String followers) {
            this.followers = followers;
            return this;
        }

        public Builder liked(String liked) {
            this.liked = liked;
            return this;
        }

        public Builder streams(String streams) {
            this.streams = streams;
            return this;
        }

        public Builder preferredUsername(String preferredUsername) {
            this.preferredUsername = preferredUsername;
            return this;
        }

        public Builder endpoints(EndPoints endpoints) {
            this.endpoints = endpoints;
            return this;
        }

    }

}
