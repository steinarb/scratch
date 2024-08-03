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
package no.priv.bang.ratatoskr.services.beans;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ActorTest {
    static ObjectMapper mapper = new ObjectMapper();

    @Test
    void testCreateActor() {
        var id = "http://localhost:8181/ratatoskr/ap/person/kenzoishii";
        var inbox = "http://localhost:8181/ratatoskr/ap/inbox/kenzoishii";
        var outbox = "http://localhost:8181/ratatoskr/ap/outbox/kenzoishii";
        var following = "http://localhost:8181/ratatoskr/ap/following/kenzoishii";
        var followers = "http://localhost:8181/ratatoskr/ap/followers/kenzoishii";
        var liked = "http://localhost:8181/ratatoskr/ap/liked/kenzoishii";
        var streams = "http://localhost:8181/ratatoskr/ap/streams/kenzoishii";
        var preferredUsername = "kenzoishii";
        var endpoints = EndPoints.with().sharedInbox("http://localhost:8181/ratatoskr/ap/sharedinbox").build();
        var actor = Actor.with()
            .id(id)
            .type(ActorType.Person)
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
            .hasFieldOrPropertyWithValue("type", ActorType.Person)
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
        var actor = Actor.with()
            .id(id)
            .endpoints(endpoints)
            .build();
        var json = mapper.writeValueAsString(actor);
        assertThat(json)
            .contains("@context")
            .contains(id)
            .contains(sharedInbox)
            .doesNotContain("null");
        var deserializedActor = mapper.readValue(json, Actor.class);
        assertThat(deserializedActor)
            .isEqualTo(actor)
            .hasFieldOrPropertyWithValue("context", "https://www.w3.org/ns/activitystreams");
    }

}
