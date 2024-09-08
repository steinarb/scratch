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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PersonTest {
    static ObjectMapper mapper = new ObjectMapper();

    @Test
    void testCreatePerson() {
        var id = "http://localhost:8181/ratatoskr/ap/person/kenzoishii";
        var inbox = "http://localhost:8181/ratatoskr/ap/inbox/kenzoishii";
        var outbox = "http://localhost:8181/ratatoskr/ap/outbox/kenzoishii";
        var following = "http://localhost:8181/ratatoskr/ap/following/kenzoishii";
        var followers = "http://localhost:8181/ratatoskr/ap/followers/kenzoishii";
        var liked = "http://localhost:8181/ratatoskr/ap/liked/kenzoishii";
        var streams = "http://localhost:8181/ratatoskr/ap/streams/kenzoishii";
        var preferredUsername = "kenzoishii";
        var endpoints = EndPoints.with().sharedInbox("http://localhost:8181/ratatoskr/ap/sharedinbox").build();
        var actor = Person.with()
            .id(id)
            .type(ActivityStreamObjectType.Person)
            .inbox(inbox)
            .outbox(outbox)
            .following(following)
            .followers(followers)
            .liked(liked)
            .streams(streams)
            .preferredUsername(preferredUsername)
            .endpoints(endpoints)
            .build();

        assertThat(actor)
            .isNotNull()
            .hasFieldOrPropertyWithValue("id", id)
            .hasFieldOrPropertyWithValue("type", ActivityStreamObjectType.Person)
            .hasFieldOrPropertyWithValue("inbox", inbox)
            .hasFieldOrPropertyWithValue("outbox", outbox)
            .hasFieldOrPropertyWithValue("following", following)
            .hasFieldOrPropertyWithValue("followers", followers)
            .hasFieldOrPropertyWithValue("liked", liked)
            .hasFieldOrPropertyWithValue("streams", streams)
            .hasFieldOrPropertyWithValue("endpoints", endpoints);
    }

    @Test
    void testSerialize() throws Exception {
        var id = "http://localhost:8181/ratatoskr/ap/person/kenzoishii";
        var sharedInbox = "http://localhost:8181/ratatoskr/ap/sharedinbox";
        var endpoints = EndPoints.with().sharedInbox(sharedInbox).build();
        Actor actor = Person.with()
            .id(id)
            .endpoints(endpoints)
            .build();
        var json = mapper.writeValueAsString(actor);
        assertThat(json)
            .contains("@context")
            .contains(id)
            .contains(sharedInbox)
            .doesNotContain("null");
        var deserializedActor = mapper.readValue(json, Person.class);
        assertThat(deserializedActor)
            .isEqualTo(actor)
            .hasFieldOrPropertyWithValue("context", "https://www.w3.org/ns/activitystreams");
    }

    @Test
    void testSerializeAndDeserializeToBaseType() throws Exception {
        var id = "http://localhost:8181/ratatoskr/ap/person/kenzoishii";
        var sharedInbox = "http://localhost:8181/ratatoskr/ap/sharedinbox";
        var endpoints = EndPoints.with().sharedInbox(sharedInbox).build();
        Actor actor = Person.with()
            .id(id)
            .endpoints(endpoints)
            .build();
        var json = mapper.writeValueAsString(actor);
        assertThat(json)
            .contains("@context")
            .contains(id)
            .contains(sharedInbox)
            .doesNotContain("null");
        var deserializedActor = mapper.readValue(json, LinkOrObject.class);
        assertThat(deserializedActor)
            .isEqualTo(actor)
            .hasFieldOrPropertyWithValue("context", "https://www.w3.org/ns/activitystreams");
    }

    @Test
    void testSerializeAndDeserializeLinkToBaseType() throws Exception {
        var sharedInbox = "http://localhost:8181/ratatoskr/ap/sharedinbox";
        LinkOrObject link = Link.with()
            .href(sharedInbox)
            .build();
        var json = mapper.writeValueAsString(link);
        var deserializedLink = mapper.readValue(json, LinkOrObject.class);
        assertThat(deserializedLink)
            .isEqualTo(link)
            .hasFieldOrPropertyWithValue("context", "https://www.w3.org/ns/activitystreams");
    }

    @Test
    void testActorOnPersonRecord() {
        var id = "http://localhost:8181/ratatoskr/ap/person/kenzoishii";
        var inbox = "http://localhost:8181/ratatoskr/ap/inbox/kenzoishii";
        var outbox = "http://localhost:8181/ratatoskr/ap/outbox/kenzoishii";
        var following = "http://localhost:8181/ratatoskr/ap/following/kenzoishii";
        var followers = "http://localhost:8181/ratatoskr/ap/followers/kenzoishii";
        var liked = "http://localhost:8181/ratatoskr/ap/liked/kenzoishii";
        var streams = "http://localhost:8181/ratatoskr/ap/streams/kenzoishii";
        var preferredUsername = "kenzoishii";
        var endpoints = EndPoints.with().sharedInbox("http://localhost:8181/ratatoskr/ap/sharedinbox").build();
        Actor object = Person.with()
            .id(id)
            .type(ActivityStreamObjectType.Person)
            .inbox(inbox)
            .outbox(outbox)
            .following(following)
            .followers(followers)
            .liked(liked)
            .streams(streams)
            .preferredUsername(preferredUsername)
            .endpoints(endpoints)
            .build();

        assertThat(object).isNotNull();
        assertThat(object.id()).isEqualTo(id);
        assertThat(object.type()).isEqualTo(ActivityStreamObjectType.Person);
        assertThat(object.inbox()).isEqualTo(inbox);
        assertThat(object.outbox()).isEqualTo(outbox);
        assertThat(object.following()).isEqualTo(following);
        assertThat(object.followers()).isEqualTo(followers);
        assertThat(object.liked()).isEqualTo(liked);
        assertThat(object.streams()).isEqualTo(streams);
    }

}
