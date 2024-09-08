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

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record Person(
    @JsonGetter("@context") Object context,
    ActivityStreamObjectType type,
    String id,
    String name,
    Map<String, String> nameMap,
    String summary,
    Map<String, String> summaryMap,
    String content,
    Map<String, String> contentMap,
    String mediaType,
    List<Link> url,
    LinkOrObject attributedTo,
    String duration,
    ZonedDateTime startTime,
    ZonedDateTime endTime,
    ZonedDateTime published,
    ZonedDateTime updated,
    LinkOrObject attachment,
    LinkOrObject audience,
    LinkOrObject to,
    LinkOrObject bcc,
    LinkOrObject bto,
    LinkOrObject cc,
    LinkOrObject generator,
    LinkOrObject icon,
    LinkOrObject image,
    LinkOrObject inReplyTo,
    LinkOrObject location,
    LinkOrObject preview,
    Collection replies,
    LinkOrObject tag,
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
        private ActivityStreamObjectType type = ActivityStreamObjectType.Person;
        private String id;
        private String name;
        private Map<String, String> nameMap;
        private String summary;
        private Map<String, String> summaryMap;
        private String content;
        private Map<String, String> contentMap;
        private String mediaType;
        private Link url;
        private LinkOrObject attributedTo;
        private String duration;
        private ZonedDateTime startTime;
        private ZonedDateTime endTime;
        private ZonedDateTime published;
        private ZonedDateTime updated;
        private LinkOrObject attachment;
        private LinkOrObject audience;
        private LinkOrObject to;
        private LinkOrObject bcc;
        private LinkOrObject bto;
        private LinkOrObject cc;
        private LinkOrObject generator;
        private LinkOrObject icon;
        private LinkOrObject image;
        private LinkOrObject inReplyTo;
        private LinkOrObject location;
        private LinkOrObject preview;
        private Collection replies;
        private LinkOrObject tag;
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
                nameMap,
                summary,
                summaryMap,
                content,
                contentMap,
                mediaType,
                Collections.singletonList(url),
                attributedTo,
                duration,
                startTime,
                endTime,
                published,
                updated,
                attachment,
                audience,
                to,
                bcc,
                bto,
                cc,
                generator,
                icon,
                image,
                inReplyTo,
                location,
                preview,
                replies,
                tag,
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
