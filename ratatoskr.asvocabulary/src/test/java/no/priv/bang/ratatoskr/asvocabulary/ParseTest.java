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
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ParseTest {
    static ObjectMapper mapper = JsonMapper.builder()
        .addModule(new JavaTimeModule())
        .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
        .build();

    @Test
    void testParseExample001() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_001.json"), LinkOrObject.class);
        switch(object) {
            case ActivityStreamObject asobject -> {
                assertThat(asobject.id()).isEqualTo("http://www.test.example/object/1");
                assertThat(asobject.name()).isEqualTo("A Simple, non-specific object");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample002() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_002.json"), LinkOrObject.class);
        switch(object) {
            case Link link -> {
                assertThat(link.href()).isEqualTo("http://example.org/abc");
                assertThat(link.hreflang()).isEqualTo("en");
                assertThat(link.mediaType()).isEqualTo("text/html");
                assertThat(link.name()).isEqualTo("An example link");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample003() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_003.json"), LinkOrObject.class);
        switch(object) {
            case Activity activity -> {
                assertThat(activity.summary()).isEqualTo("Sally did something to a note");
                switch (activity.actor()) {
                    case Person person -> assertThat(person.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for activity.actor");
                }
                switch (activity.object()) {
                    case Note note -> assertThat(note.name()).isEqualTo("A Note");
                    default -> fail("Did not get the expected type for activity.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample004() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_004.json"), LinkOrObject.class);
        switch(object) {
            case Travel travel -> {
                assertThat(travel.summary()).isEqualTo("Sally went to work");
                switch (travel.actor()) {
                    case Person person -> assertThat(person.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for travel.actor");
                }
                switch (travel.target()) {
                    case Place place -> assertThat(place.name()).isEqualTo("Work");
                    default -> fail("Did not get the expected type for travel.target");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample005() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_005.json"), LinkOrObject.class);
        switch(object) {
            case Collection collection -> {
                assertThat(collection.summary()).isEqualTo("Sally's notes");
                assertThat(collection.totalItems()).isEqualTo(2);
                switch (collection.items().get(0)) {
                    case Note note -> assertThat(note.name()).isEqualTo("A Simple Note");
                    default -> fail("Did not get the expected type for collection.orderedItems[0]");
                }
                switch (collection.items().get(1)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Another Simple Note");
                    default -> fail("Did not get the expected type for collection.orderedItems[1]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample006() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_006.json"), LinkOrObject.class);
        switch(object) {
            case OrderedCollection collection -> {
                assertThat(collection.summary()).isEqualTo("Sally's notes");
                assertThat(collection.totalItems()).isEqualTo(2);
                switch (collection.orderedItems().get(0)) {
                    case Note note -> assertThat(note.name()).isEqualTo("A Simple Note");
                    default -> fail("Did not get the expected type for orderedCollection.orderedItems[0]");
                }
                switch (collection.orderedItems().get(1)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Another Simple Note");
                    default -> fail("Did not get the expected type for orderedCollection.orderedItems[1]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample007() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_007.json"), LinkOrObject.class);
        switch(object) {
            case CollectionPage collectionPage -> {
                assertThat(collectionPage.summary()).isEqualTo("Page 1 of Sally's notes");
                assertThat(collectionPage.id()).isEqualTo("http://example.org/foo?page=1");
                assertThat(collectionPage.partOf()).isEqualTo("http://example.org/foo");
                switch (collectionPage.items().get(0)) {
                    case Note note -> assertThat(note.name()).isEqualTo("A Simple Note");
                    default -> fail("Did not get the expected type for collectionPage.orderedItems[0]");
                }
                switch (collectionPage.items().get(1)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Another Simple Note");
                    default -> fail("Did not get the expected type for collectionPage.orderedItems[1]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample008() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_008.json"), LinkOrObject.class);
        switch(object) {
            case OrderedCollectionPage orderedCollectionPage -> {
                assertThat(orderedCollectionPage.summary()).isEqualTo("Page 1 of Sally's notes");
                assertThat(orderedCollectionPage.id()).isEqualTo("http://example.org/foo?page=1");
                assertThat(orderedCollectionPage.partOf()).isEqualTo("http://example.org/foo");
                switch (orderedCollectionPage.orderedItems().get(0)) {
                    case Note note -> assertThat(note.name()).isEqualTo("A Simple Note");
                    default -> fail("Did not get the expected type for orderedCollectionPage.orderedItems[0]");
                }
                switch (orderedCollectionPage.orderedItems().get(1)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Another Simple Note");
                    default -> fail("Did not get the expected type for orderedCollectionPage.orderedItems[1]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample009() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_009.json"), LinkOrObject.class);
        switch(object) {
            case Accept accept -> {
                assertThat(accept.summary()).isEqualTo("Sally accepted an invitation to a party");
                switch (accept.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for accept.actor");
                }
                switch (accept.object()) {
                    case Invite invite -> {
                        switch (invite.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://john.example.org");
                            default -> fail("Did not get the expected type for invite.actor");
                        }
                        switch (invite.object()) {
                            case Event event -> assertThat(event.name()).isEqualTo("Going-Away Party for Jim");
                            default -> fail("Did not get the expected type for invite.object");
                        }
                    }
                    default -> fail("Did not get the expected type for accept.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample010() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_010.json"), LinkOrObject.class);
        switch(object) {
            case Accept accept -> {
                assertThat(accept.summary()).isEqualTo("Sally accepted Joe into the club");
                switch (accept.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for accept.actor");
                }
                switch (accept.object()) {
                    case Person person -> assertThat(person.name()).isEqualTo("Joe");
                    default -> fail("Did not get the expected type for accept.object");
                }
                switch (accept.target()) {
                    case Group group -> assertThat(group.name()).isEqualTo("The Club");
                    default -> fail("Did not get the expected type for accept.target");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample011() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_011.json"), LinkOrObject.class);
        switch(object) {
            case TentativeAccept tentativeAccept -> {
                assertThat(tentativeAccept.summary()).isEqualTo("Sally tentatively accepted an invitation to a party");
                switch (tentativeAccept.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for tentativeAccept.actor");
                }
                switch (tentativeAccept.object()) {
                    case Invite invite -> {
                        switch (invite.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://john.example.org");
                            default -> fail("Did not get the expected type for invite.actor");
                        }
                        switch (invite.object()) {
                            case Event event -> assertThat(event.name()).isEqualTo("Going-Away Party for Jim");
                            default -> fail("Did not get the expected type for invite.object");
                        }
                    }
                    default -> fail("Did not get the expected type for tentativeAccept.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample012() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_012.json"), LinkOrObject.class);
        switch(object) {
            case Add add -> {
                assertThat(add.summary()).isEqualTo("Sally added an object");
                switch (add.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for add.actor");
                }
                switch (add.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/abc");
                    default -> fail("Did not get the expected type for add.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample013() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_013.json"), LinkOrObject.class);
        switch(object) {
            case Add add -> {
                assertThat(add.summary()).isEqualTo("Sally added a picture of her cat to her cat picture collection");
                switch (add.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for add.actor");
                }
                switch (add.object()) {
                    case Image image -> {
                        assertThat(image.name()).isEqualTo("A picture of my cat");
                        assertThat(image.url().get(0).href()).isEqualTo("http://example.org/img/cat.png");
                    }
                    default -> fail("Did not get the expected type for add.object");
                }
                switch (add.origin()) {
                    case Collection collection -> assertThat(collection.name()).isEqualTo("Camera Roll");
                    default -> fail("Did not get the expected type for add.origin");
                }
                switch (add.target()) {
                    case Collection collection -> assertThat(collection.name()).isEqualTo("My Cat Pictures");
                    default -> fail("Did not get the expected type for add.target");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample014() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_014.json"), LinkOrObject.class);
        switch(object) {
            case Arrive arrive -> {
                assertThat(arrive.summary()).isEqualTo("Sally arrived at work");
                switch (arrive.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for arrive.actor");
                }
                switch (arrive.location()) {
                    case Place place -> assertThat(place.name()).isEqualTo("Work");
                    default -> fail("Did not get the expected type for arrive.location");
                }
                switch (arrive.origin()) {
                    case Place place -> assertThat(place.name()).isEqualTo("Home");
                    default -> fail("Did not get the expected type for arrive.origin");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample015() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_015.json"), LinkOrObject.class);
        switch(object) {
            case Create create -> {
                assertThat(create.summary()).isEqualTo("Sally created a note");
                switch (create.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for create.actor");
                }
                switch (create.object()) {
                    case Note note -> assertThat(note.name()).isEqualTo("A Simple Note");
                    default -> fail("Did not get the expected type for create.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample016() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_016.json"), LinkOrObject.class);
        switch(object) {
            case Delete delete -> {
                assertThat(delete.summary()).isEqualTo("Sally deleted a note");
                switch (delete.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for delete.actor");
                }
                switch (delete.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/notes/1");
                    default -> fail("Did not get the expected type for delete.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample017() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_017.json"), LinkOrObject.class);
        switch(object) {
            case Follow follow -> {
                assertThat(follow.summary()).isEqualTo("Sally followed John");
                switch (follow.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for follow.actor");
                }
                switch (follow.object()) {
                    case Person person -> assertThat(person.name()).isEqualTo("John");
                    default -> fail("Did not get the expected type for follow.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample018() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_018.json"), LinkOrObject.class);
        switch(object) {
            case Ignore ignore -> {
                assertThat(ignore.summary()).isEqualTo("Sally ignored a note");
                switch (ignore.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for ignore.actor");
                }
                switch (ignore.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/notes/1");
                    default -> fail("Did not get the expected type for ignore.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample019() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_019.json"), LinkOrObject.class);
        switch(object) {
            case Join join -> {
                assertThat(join.summary()).isEqualTo("Sally joined a group");
                switch (join.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for join.actor");
                }
                switch (join.object()) {
                    case Group group -> assertThat(group.name()).isEqualTo("A Simple Group");
                    default -> fail("Did not get the expected type for join.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample020() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_020.json"), LinkOrObject.class);
        switch(object) {
            case Leave leave -> {
                assertThat(leave.summary()).isEqualTo("Sally left work");
                switch (leave.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for leave.actor");
                }
                switch (leave.object()) {
                    case Place place -> assertThat(place.name()).isEqualTo("Work");
                    default -> fail("Did not get the expected type for leave.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample021() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_021.json"), LinkOrObject.class);
        switch(object) {
            case Leave leave -> {
                assertThat(leave.summary()).isEqualTo("Sally left a group");
                switch (leave.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for leave.actor");
                }
                switch (leave.object()) {
                    case Group group -> assertThat(group.name()).isEqualTo("A Simple Group");
                    default -> fail("Did not get the expected type for leave.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample022() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_022.json"), LinkOrObject.class);
        switch(object) {
            case Like like -> {
                assertThat(like.summary()).isEqualTo("Sally liked a note");
                switch (like.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for like.actor");
                }
                switch (like.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/notes/1");
                    default -> fail("Did not get the expected type for like.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample023() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_023.json"), LinkOrObject.class);
        switch(object) {
            case Offer offer -> {
                assertThat(offer.summary()).isEqualTo("Sally offered 50% off to Lewis");
                switch (offer.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for offer.actor");
                }
                switch (offer.object()) {
                    case UntypedObject untyped -> assertThat(untyped.name()).isEqualTo("50% Off!");
                    default -> fail("Did not get the expected type for offer.object");
                }
                switch (offer.target()) {
                    case Person person -> assertThat(person.name()).isEqualTo("Lewis");
                    default -> fail("Did not get the expected type for offer.target");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample024() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_024.json"), LinkOrObject.class);
        switch(object) {
            case Offer offer -> {
                assertThat(offer.summary()).isEqualTo("Sally invited John and Lisa to a party");
                switch (offer.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for offer.actor");
                }
                switch (offer.object()) {
                    case Event event -> assertThat(event.name()).isEqualTo("A Party");
                    default -> fail("Did not get the expected type for offer.object");
                }
                switch (offer.target()) {
                    case LinkOrObjectList list -> {
                        assertThat(list.size()).isEqualTo(2);
                        switch (list.get(0)) {
                            case Person person -> assertThat(person.name()).isEqualTo("John");
                            default -> fail("Did not get the expected type for list[0]");
                        }
                        switch (list.get(1)) {
                            case Person person -> assertThat(person.name()).isEqualTo("Lisa");
                            default -> fail("Did not get the expected type for list[1]");
                        }
                    }
                    default -> fail("Did not get the expected type for offer.target");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample025() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_025.json"), LinkOrObject.class);
        switch(object) {
            case Reject reject -> {
                assertThat(reject.summary()).isEqualTo("Sally rejected an invitation to a party");
                switch (reject.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for reject.actor");
                }
                switch (reject.object()) {
                    case Invite invite -> {
                        switch(invite.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://john.example.org");
                            default -> fail("Did not get expected type for invite.actor");
                        }
                        switch(invite.object()) {
                            case Event event -> assertThat(event.name()).isEqualTo("Going-Away Party for Jim");
                            default -> fail("Did not get expected type for invite.object");
                        }
                    }
                    default -> fail("Did not get the expected type for reject.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample026() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_026.json"), LinkOrObject.class);
        switch(object) {
            case Reject reject -> {
                assertThat(reject.summary()).isEqualTo("Sally tentatively rejected an invitation to a party");
                switch (reject.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for reject.actor");
                }
                switch (reject.object()) {
                    case Invite invite -> {
                        switch(invite.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://john.example.org");
                            default -> fail("Did not get expected type for invite.actor");
                        }
                        switch(invite.object()) {
                            case Event event -> assertThat(event.name()).isEqualTo("Going-Away Party for Jim");
                            default -> fail("Did not get expected type for invite.object");
                        }
                    }
                    default -> fail("Did not get the expected type for reject.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample027() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_027.json"), LinkOrObject.class);
        switch(object) {
            case Remove remove -> {
                assertThat(remove.summary()).isEqualTo("Sally removed a note from her notes folder");
                switch (remove.actor()) {
                    case Actor actor -> assertThat(actor.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for remove.actor");
                }
                switch (remove.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/notes/1");
                    default -> fail("Did not get the expected type for remove.object");
                }
                switch (remove.target()) {
                    case Collection collection -> assertThat(collection.name()).isEqualTo("Notes Folder");
                    default -> fail("Did not get the expected type for remove.target");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample028() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_028.json"), LinkOrObject.class);
        switch(object) {
            case Remove remove -> {
                assertThat(remove.summary()).isEqualTo("The moderator removed Sally from a group");
                switch (remove.actor()) {
                    case UntypedObject untyped -> assertThat(untyped.name()).isEqualTo("The Moderator");
                    default -> fail("Did not get the expected type for remove.actor");
                }
                switch (remove.object()) {
                    case Person person -> assertThat(person.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for remove.object");
                }
                switch (remove.origin()) {
                    case Group group -> assertThat(group.name()).isEqualTo("A Simple Group");
                    default -> fail("Did not get the expected type for remove.origin");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample029() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_029.json"), LinkOrObject.class);
        switch(object) {
            case Undo undo -> {
                assertThat(undo.summary()).isEqualTo("Sally retracted her offer to John");
                switch (undo.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("Did not get the expected type for undo.actor");
                }
                switch (undo.object()) {
                    case Offer offer -> {
                        switch (offer.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                            default -> fail("Did not get the expected type for offer.actor");
                        }
                        switch (offer.object()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                            default -> fail("Did not get the expected type for offer.object");
                        }
                        switch (offer.target()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://john.example.org");
                            default -> fail("Did not get the expected type for offer.target");
                        }
                    }
                    default -> fail("Did not get the expected type for undo.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample030() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_030.json"), LinkOrObject.class);
        switch(object) {
            case Update update -> {
                assertThat(update.summary()).isEqualTo("Sally updated her note");
                switch (update.actor()) {
                    case Person person -> assertThat(person.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for update.actor");
                }
                switch (update.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/notes/1");
                    default -> fail("Did not get the expected type for update.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample031() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_031.json"), LinkOrObject.class);
        switch(object) {
            case View view -> {
                assertThat(view.summary()).isEqualTo("Sally read an article");
                switch (view.actor()) {
                    case Person person -> assertThat(person.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for view.actor");
                }
                switch (view.object()) {
                    case Article article -> assertThat(article.name()).isEqualTo("What You Should Know About Activity Streams");
                    default -> fail("Did not get the expected type for view.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample032() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_032.json"), LinkOrObject.class);
        switch(object) {
            case Listen listen -> {
                assertThat(listen.summary()).isEqualTo("Sally listened to a piece of music");
                switch (listen.actor()) {
                    case Person person -> assertThat(person.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for listen.actor");
                }
                switch (listen.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/music.mp3");
                    default -> fail("Did not get the expected type for listen.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample033() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_033.json"), LinkOrObject.class);
        switch(object) {
            case Read read -> {
                assertThat(read.summary()).isEqualTo("Sally read a blog post");
                switch (read.actor()) {
                    case Person person -> assertThat(person.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for read.actor");
                }
                switch (read.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                    default -> fail("Did not get the expected type for read.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample034() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_034.json"), LinkOrObject.class);
        switch(object) {
            case Move move -> {
                assertThat(move.summary()).isEqualTo("Sally moved a post from List A to List B");
                switch (move.actor()) {
                    case Person person -> assertThat(person.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for move.actor");
                }
                switch (move.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                    default -> fail("Did not get the expected type for move.object");
                }
                switch (move.target()) {
                    case Collection collection -> assertThat(collection.name()).isEqualTo("List B");
                    default -> fail("Did not get the expected type for move.target");
                }
                switch (move.origin()) {
                    case Collection collection -> assertThat(collection.name()).isEqualTo("List A");
                    default -> fail("Did not get the expected type for move.origin");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample035() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_035.json"), LinkOrObject.class);
        switch(object) {
            case Travel travel -> {
                assertThat(travel.summary()).isEqualTo("Sally went home from work");
                switch (travel.actor()) {
                    case Person person -> assertThat(person.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for travel.actor");
                }
                switch (travel.target()) {
                    case Place place -> assertThat(place.name()).isEqualTo("Home");
                    default -> fail("Did not get the expected type for travel.target");
                }
                switch (travel.origin()) {
                    case Place place -> assertThat(place.name()).isEqualTo("Work");
                    default -> fail("Did not get the expected type for travel.origin");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample036() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_036.json"), LinkOrObject.class);
        switch(object) {
            case Announce announce -> {
                assertThat(announce.summary()).isEqualTo("Sally announced that she had arrived at work");
                switch (announce.actor()) {
                    case Person person -> {
                        assertThat(person.name()).isEqualTo("Sally");
                        assertThat(person.id()).isEqualTo("http://sally.example.org");
                    }
                    default -> fail("Did not get the expected type for announce.actor");
                }
                switch (announce.object()) {
                    case Arrive arrive -> {
                        switch (arrive.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                            default -> fail("Did not get the expected type for arrive.actor");
                        }
                        switch (arrive.location()) {
                            case Place place -> assertThat(place.name()).isEqualTo("Work");
                            default -> fail("Did not get the expected type for arrive.actor");
                        }
                    }
                    default -> fail("Did not get the expected type for announce.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample037() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_037.json"), LinkOrObject.class);
        switch(object) {
            case Block block -> {
                assertThat(block.summary()).isEqualTo("Sally blocked Joe");
                switch (block.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("Did not get the expected type for block.actor");
                }
                switch (block.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://joe.example.org");
                    default -> fail("Did not get the expected type for block.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample038() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_038.json"), LinkOrObject.class);
        switch(object) {
            case Flag flag -> {
                assertThat(flag.summary()).isEqualTo("Sally flagged an inappropriate note");
                switch (flag.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("Did not get the expected type for flag.actor");
                }
                switch (flag.object()) {
                    case Note note -> assertThat(note.content()).isEqualTo("An inappropriate note");
                    default -> fail("Did not get the expected type for flag.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample039() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_039.json"), LinkOrObject.class);
        switch(object) {
            case Dislike dislike -> {
                assertThat(dislike.summary()).isEqualTo("Sally disliked a post");
                switch (dislike.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("Did not get the expected type for dislike.actor");
                }
                switch (dislike.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                    default -> fail("Did not get the expected type for dislike.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample040() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_040.json"), LinkOrObject.class);
        switch(object) {
            case Question question -> {
                assertThat(question.name()).isEqualTo("What is the answer?");
                switch (question.oneOf().get(0)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Option A");
                    default -> fail("Did not get the expected type for question.oneOf[0]");
                }
                switch (question.oneOf().get(1)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Option B");
                    default -> fail("Did not get the expected type for question.oneOf[1]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample041() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_041.json"), LinkOrObject.class);
        switch(object) {
            case Question question -> {
                assertThat(question.name()).isEqualTo("What is the answer?");
                assertThat(question.closed()).isBefore(ZonedDateTime.now());
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample042() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_042.json"), LinkOrObject.class);
        switch(object) {
            case Application application -> assertThat(application.name()).isEqualTo("Exampletron 3000");
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample043() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_043.json"), LinkOrObject.class);
        switch(object) {
            case Group group -> assertThat(group.name()).isEqualTo("Big Beards of Austin");
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample044() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_044.json"), LinkOrObject.class);
        switch(object) {
            case Organization organization -> assertThat(organization.name()).isEqualTo("Example Co.");
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample045() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_045.json"), LinkOrObject.class);
        switch(object) {
            case Person person -> assertThat(person.name()).isEqualTo("Sally Smith");
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample046() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_046.json"), LinkOrObject.class);
        switch(object) {
            case Service service -> assertThat(service.name()).isEqualTo("Acme Web Service");
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample047() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_047.json"), LinkOrObject.class);
        switch(object) {
            case Relationship relationship -> {
                assertThat(relationship.summary()).isEqualTo("Sally is an acquaintance of John");
                switch (relationship.subject()) {
                    case Person person -> assertThat(person.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for relationship.subject");
                }
                switch (relationship.relationship()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://purl.org/vocab/relationship/acquaintanceOf");
                    default -> fail("Did not get the expected type for relationship.relationship");
                }
                switch (relationship.object()) {
                    case Person person -> assertThat(person.name()).isEqualTo("John");
                    default -> fail("Did not get the expected type for relationship.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample048() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_048.json"), LinkOrObject.class);
        switch(object) {
            case Article article -> {
                assertThat(article.name()).isEqualTo("What a Crazy Day I Had");
                assertThat(article.content()).isEqualTo("<div>... you will never believe ...</div>");
                switch (article.attributedTo()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("did not get the expected type for article.attributedTo");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample049() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_049.json"), LinkOrObject.class);
        switch(object) {
            case Document document -> {
                assertThat(document.name()).isEqualTo("4Q Sales Forecast");
                assertThat(document.url().get(0).href()).isEqualTo("http://example.org/4q-sales-forecast.pdf");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample050() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_050.json"), LinkOrObject.class);
        switch(object) {
            case Audio audio -> {
                assertThat(audio.name()).isEqualTo("Interview With A Famous Technologist");
                assertThat(audio.url().get(0).href()).isEqualTo("http://example.org/podcast.mp3");
                assertThat(audio.url().get(0).mediaType()).isEqualTo("audio/mp3");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample051() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_051.json"), LinkOrObject.class);
        switch(object) {
            case Image image -> {
                assertThat(image.name()).isEqualTo("Cat Jumping on Wagon");
                assertThat(image.url().get(0).href()).isEqualTo("http://example.org/image.jpeg");
                assertThat(image.url().get(0).mediaType()).isEqualTo("image/jpeg");
                assertThat(image.url().get(1).href()).isEqualTo("http://example.org/image.png");
                assertThat(image.url().get(1).mediaType()).isEqualTo("image/png");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample052() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_052.json"), LinkOrObject.class);
        switch(object) {
            case Video video -> {
                assertThat(video.name()).isEqualTo("Puppy Plays With Ball");
                assertThat(video.url().get(0).href()).isEqualTo("http://example.org/video.mkv");
                assertThat(video.duration()).isEqualTo("PT2H");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample053() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_053.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.name()).isEqualTo("A Word of Warning");
                assertThat(note.content()).isEqualTo("Looks like it is going to rain today. Bring an umbrella!");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample054() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_054.json"), LinkOrObject.class);
        switch(object) {
            case Page page -> {
                assertThat(page.name()).isEqualTo("Omaha Weather Report");
                assertThat(page.url().get(0).href()).isEqualTo("http://example.org/weather-in-omaha.html");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample055() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_055.json"), LinkOrObject.class);
        switch(object) {
            case Event event -> {
                assertThat(event.name()).isEqualTo("Going-Away Party for Jim");
                assertThat(event.startTime()).isEqualTo(ZonedDateTime.parse("2014-12-31T23:00:00-08:00"));
                assertThat(event.endTime()).isEqualTo(ZonedDateTime.parse("2015-01-01T06:00:00-08:00"));
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample056() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_056.json"), LinkOrObject.class);
        switch(object) {
            case Place place -> assertThat(place.name()).isEqualTo("Work");
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample057() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_057.json"), LinkOrObject.class);
        switch(object) {
            case Place place -> {
                assertThat(place.name()).isEqualTo("Fresno Area");
                assertThat(place.latitude()).isEqualTo(36.75f);
                assertThat(place.longitude()).isEqualTo(119.7667f);
                assertThat(place.radius()).isEqualTo(15);
                assertThat(place.units()).isEqualTo("miles");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample058() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_058.json"), LinkOrObject.class);
        switch(object) {
            case Mention mention -> {
                assertThat(mention.name()).isEqualTo("Joe");
                assertThat(mention.summary()).isEqualTo("Mention of Joe by Carrie in her note");
                assertThat(mention.href()).isEqualTo("http://example.org/joe");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample059() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_059.json"), LinkOrObject.class);
        switch(object) {
            case Profile profile -> {
                assertThat(profile.summary()).isEqualTo("Sally's Profile");
                switch (profile.describes()) {
                    case Person person -> assertThat(person.name()).isEqualTo("Sally Smith");
                    default -> fail("did not get the expected type for summary.describes");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample060() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_060.json"), LinkOrObject.class);
        switch(object) {
            case OrderedCollection collection -> {
                assertThat(collection.name()).isEqualTo("Vacation photos 2016");
                switch (collection.orderedItems().get(0)) {
                    case Image image -> assertThat(image.id()).isEqualTo("http://image.example/1");
                    default -> fail("did not get the expected type for collection.orderedItems()[0]");
                }
                switch (collection.orderedItems().get(1)) {
                    case Tombstone tombstone -> {
                        assertThat(tombstone.formerType()).isEqualTo("Image");
                        assertThat(tombstone.id()).isEqualTo("http://image.example/2");
                        assertThat(tombstone.deleted()).isEqualTo(ZonedDateTime.parse("2016-03-17T00:00:00Z"));
                    }
                    default -> fail("did not get the expected type for collection.orderedItems()[1]");
                }
                switch (collection.orderedItems().get(2)) {
                    case Image image -> assertThat(image.id()).isEqualTo("http://image.example/3");
                    default -> fail("did not get the expected type for collection.orderedItems()[2]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample061() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_061.json"), LinkOrObject.class);
        switch(object) {
            case UntypedObject untypedObject -> {
                assertThat(untypedObject.name()).isEqualTo("Foo");
                assertThat(untypedObject.id()).isEqualTo("http://example.org/foo");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample062() throws Exception {
        // Non-standard type property
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_062.json"), LinkOrObject.class);
        switch(object) {
            case UntypedObject untypedObject -> {
                assertThat(untypedObject.summary()).isEqualTo("A foo");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample063() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_063.json"), LinkOrObject.class);
        switch(object) {
            case Offer offer -> {
                assertThat(offer.summary()).isEqualTo("Sally offered the Foo object");
                switch(offer.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("did not get the expected type for offer.actor");
                }
                switch(offer.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/foo");
                    default -> fail("did not get the expected type for offer.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample064() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_064.json"), LinkOrObject.class);
        switch(object) {
            case Offer offer -> {
                assertThat(offer.summary()).isEqualTo("Sally offered the Foo object");
                switch(offer.actor()) {
                    case Actor actor -> {
                        assertThat(actor.id()).isEqualTo("http://sally.example.org");
                        assertThat(actor.summary()).isEqualTo("Sally");
                    }
                    default -> fail("did not get the expected type for offer.actor");
                }
                switch(offer.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/foo");
                    default -> fail("did not get the expected type for offer.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample065() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_065.json"), LinkOrObject.class);
        switch(object) {
            case Offer offer -> {
                assertThat(offer.summary()).isEqualTo("Sally and Joe offered the Foo object");
                switch(offer.actor()) {
                    case LinkOrObjectList list -> {
                        switch(list.get(0)) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://joe.example.org");
                            default -> fail("did not get the expected type for list[0]");
                        }
                        switch(list.get(1)) {
                            case Person person -> {
                                assertThat(person.id()).isEqualTo("http://sally.example.org");
                                assertThat(person.name()).isEqualTo("Sally");
                            }
                            default -> fail("did not get the expected type for list[1]");
                        }
                    }
                    default -> fail("did not get the expected type for offer.actor");
                }
                switch(offer.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/foo");
                    default -> fail("did not get the expected type for offer.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample067() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_067.json"), LinkOrObject.class);
        switch(object) {
            case Image image -> {
                assertThat(image.name()).isEqualTo("My cat taking a nap");
                assertThat(image.url().get(0).href()).isEqualTo("http://example.org/cat.jpeg");
                switch(image.attributedTo()) {
                    case LinkOrObjectList list -> {
                        switch(list.get(0)) {
                            case Person person -> assertThat(person.name()).isEqualTo("Sally");
                            default -> fail("did not get the expected type for list[0]");
                        }
                    }
                    default -> fail("did not get the expected type for image.attributedTo");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample068() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_068.json"), LinkOrObject.class);
        switch(object) {
            case Image image -> {
                assertThat(image.name()).isEqualTo("My cat taking a nap");
                assertThat(image.url().get(0).href()).isEqualTo("http://example.org/cat.jpeg");
                switch(image.attributedTo()) {
                    case LinkOrObjectList list -> {
                        switch(list.get(0)) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://joe.example.org");
                            default -> fail("did not get the expected type for list[0]");
                        }
                        switch(list.get(1)) {
                            case Person person -> assertThat(person.name()).isEqualTo("Sally");
                            default -> fail("did not get the expected type for list[1]");
                        }
                    }
                    default -> fail("did not get the expected type for image.attributedTo");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample069() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_069.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.name()).isEqualTo("Holiday announcement");
                assertThat(note.content()).isEqualTo("Thursday will be a company-wide holiday. Enjoy your day off!");
                switch(note.audience()) {
                    case UntypedObject audience -> assertThat(audience.name()).isEqualTo("ExampleCo LLC");
                    default -> fail("did not get the expected type for note.audience");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample070() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_070.json"), LinkOrObject.class);
        switch(object) {
            case Offer offer -> {
                assertThat(offer.summary()).isEqualTo("Sally offered a post to John");
                switch(offer.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("did not get the expected type for offer.actor");
                }
                switch(offer.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                    default -> fail("did not get the expected type for offer.object");
                }
                switch(offer.target()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://john.example.org");
                    default -> fail("did not get the expected type for offer.target");
                }
                switch(offer.bcc()) {
                    case LinkOrObjectList list -> {
                        switch(list.get(0)) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://joe.example.org");
                            default -> fail("did not get the expected type for offer.bcc[0]");
                        }
                    }
                    default -> fail("did not get the expected type for offer.bcc");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample071() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_071.json"), LinkOrObject.class);
        switch(object) {
            case Offer offer -> {
                assertThat(offer.summary()).isEqualTo("Sally offered a post to John");
                switch(offer.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("did not get the expected type for offer.actor");
                }
                switch(offer.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                    default -> fail("did not get the expected type for offer.object");
                }
                switch(offer.target()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://john.example.org");
                    default -> fail("did not get the expected type for offer.target");
                }
                switch(offer.bto()) {
                    case LinkOrObjectList list -> {
                        switch(list.get(0)) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://joe.example.org");
                            default -> fail("did not get the expected type for offer.bto[0]");
                        }
                    }
                    default -> fail("did not get the expected type for offer.bto");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample072() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_072.json"), LinkOrObject.class);
        switch(object) {
            case Offer offer -> {
                assertThat(offer.summary()).isEqualTo("Sally offered a post to John");
                switch(offer.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("did not get the expected type for offer.actor");
                }
                switch(offer.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                    default -> fail("did not get the expected type for offer.object");
                }
                switch(offer.target()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://john.example.org");
                    default -> fail("did not get the expected type for offer.target");
                }
                switch(offer.cc()) {
                    case LinkOrObjectList list -> {
                        switch(list.get(0)) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://joe.example.org");
                            default -> fail("did not get the expected type for offer.cc[0]");
                        }
                    }
                    default -> fail("did not get the expected type for offer.cc");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample073() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_073.json"), LinkOrObject.class);
        switch(object) {
            case Collection collection -> {
                assertThat(collection.summary()).isEqualTo("Activities in context 1");
                switch(collection.items().get(0)) {
                    case Offer offer -> {
                        switch (offer.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                            default -> fail("did not get the expected type for offer.actor");
                        }
                        switch (offer.object()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                            default -> fail("did not get the expected type for offer.object");
                        }
                        switch (offer.target()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://john.example.org");
                            default -> fail("did not get the expected type for offer.target");
                        }
                        switch (offer.context()) {
                            case String string -> assertThat(string).isEqualTo("http://example.org/contexts/1");
                            default -> fail("did not get the expected type for offer.context");
                        }
                    }
                    default -> fail("did not get the expected type for collection.items[0]");
                }
                switch(collection.items().get(1)) {
                    case Like like -> {
                        switch (like.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://joe.example.org");
                            default -> fail("did not get the expected type for like.actor");
                        }
                        switch (like.object()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/2");
                            default -> fail("did not get the expected type for like.object");
                        }
                        switch (like.context()) {
                            case String string -> assertThat(string).isEqualTo("http://example.org/contexts/1");
                            default -> fail("did not get the expected type for like.context");
                        }
                    }
                    default -> fail("did not get the expected type for collection.items[1]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample074() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_074.json"), LinkOrObject.class);
        switch(object) {
            case Collection collection -> {
                assertThat(collection.summary()).isEqualTo("Sally's blog posts");
                assertThat(collection.totalItems()).isEqualTo(3);
                switch(collection.current()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/collection");
                    default -> fail("did not get the expected type for collection.current");
                }
                switch(collection.items().get(0)) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                    default -> fail("did not get the expected type for collection.items[0]");
                }
                switch(collection.items().get(1)) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/2");
                    default -> fail("did not get the expected type for collection.items[1]");
                }
                switch(collection.items().get(2)) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/3");
                    default -> fail("did not get the expected type for collection.items[2]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample075() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_075.json"), LinkOrObject.class);
        switch(object) {
            case Collection collection -> {
                assertThat(collection.summary()).isEqualTo("Sally's blog posts");
                assertThat(collection.totalItems()).isEqualTo(3);
                switch(collection.current()) {
                    case Link link -> {
                        assertThat(link.summary()).isEqualTo("Most Recent Items");
                        assertThat(link.href()).isEqualTo("http://example.org/collection");
                    }
                    default -> fail("did not get the expected type for collection.current");
                }
                switch(collection.items().get(0)) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                    default -> fail("did not get the expected type for collection.items[0]");
                }
                switch(collection.items().get(1)) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/2");
                    default -> fail("did not get the expected type for collection.items[1]");
                }
                switch(collection.items().get(2)) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/3");
                    default -> fail("did not get the expected type for collection.items[2]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample076() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_076.json"), LinkOrObject.class);
        switch(object) {
            case Collection collection -> {
                assertThat(collection.summary()).isEqualTo("Sally's blog posts");
                assertThat(collection.totalItems()).isEqualTo(3);
                switch(collection.first()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/collection?page=0");
                    default -> fail("Did not get the expected type for collection.first");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample077() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_077.json"), LinkOrObject.class);
        switch(object) {
            case Collection collection -> {
                assertThat(collection.summary()).isEqualTo("Sally's blog posts");
                assertThat(collection.totalItems()).isEqualTo(3);
                switch(collection.first()) {
                    case Link link -> {
                        assertThat(link.summary()).isEqualTo("First Page");
                        assertThat(link.href()).isEqualTo("http://example.org/collection?page=0");
                    }
                    default -> fail("Did not get the expected type for collection.first");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample078() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_078.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.summary()).isEqualTo("A simple note");
                assertThat(note.content()).isEqualTo("This is all there is.");
                switch(note.generator()) {
                    case Application application -> assertThat(application.name()).isEqualTo("Exampletron 3000");
                    default -> fail("Did not get the expected type for note.generator");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample079() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_079.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.summary()).isEqualTo("A simple note");
                assertThat(note.content()).isEqualTo("This is all there is.");
                switch(note.icon()) {
                    case Image image -> {
                        assertThat(image.name()).isEqualTo("Note icon");
                        assertThat(image.url().get(0).href()).isEqualTo("http://example.org/note.png");
                        assertThat(image.width()).isEqualTo(16);
                        assertThat(image.height()).isEqualTo(16);
                    }
                    default -> fail("Did not get the expected type for note.icon");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample080() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_080.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.summary()).isEqualTo("A simple note");
                assertThat(note.content()).isEqualTo("A simple note");
                switch(note.icon()) {
                    case LinkOrObjectList list -> {
                        switch(list.get(0)) {
                            case Image image -> {
                                assertThat(image.summary()).isEqualTo("Note (16x16)");
                                assertThat(image.url().get(0).href()).isEqualTo("http://example.org/note1.png");
                                assertThat(image.width()).isEqualTo(16);
                                assertThat(image.height()).isEqualTo(16);
                            }
                            default -> fail("Did not get the expected type for list[0]");
                        }
                        switch(list.get(1)) {
                            case Image image -> {
                                assertThat(image.summary()).isEqualTo("Note (32x32)");
                                assertThat(image.url().get(0).href()).isEqualTo("http://example.org/note2.png");
                                assertThat(image.width()).isEqualTo(32);
                                assertThat(image.height()).isEqualTo(32);
                            }
                            default -> fail("Did not get the expected type for list[1]");
                        }
                    }
                    default -> fail("Did not get the expected type for note.icon");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample081() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_081.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.name()).isEqualTo("A simple note");
                assertThat(note.content()).isEqualTo("This is all there is.");
                switch(note.image()) {
                    case Image image -> {
                        assertThat(image.name()).isEqualTo("A Cat");
                        assertThat(image.url().get(0).href()).isEqualTo("http://example.org/cat.png");
                    }
                    default -> fail("Did not get the expected type for note.image");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample082() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_082.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.name()).isEqualTo("A simple note");
                assertThat(note.content()).isEqualTo("This is all there is.");
                switch(note.image()) {
                    case LinkOrObjectList list -> {
                        switch(list.get(0)) {
                            case Image image -> {
                                assertThat(image.name()).isEqualTo("Cat 1");
                                assertThat(image.url().get(0).href()).isEqualTo("http://example.org/cat1.png");
                            }
                            default -> fail("Did not get the expected type for list[0]");
                        }
                        switch(list.get(1)) {
                            case Image image -> {
                                assertThat(image.name()).isEqualTo("Cat 2");
                                assertThat(image.url().get(0).href()).isEqualTo("http://example.org/cat2.png");
                            }
                            default -> fail("Did not get the expected type for list[1]");
                        }
                    }
                    default -> fail("Did not get the expected type for note.image");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample083() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_083.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.summary()).isEqualTo("A simple note");
                assertThat(note.content()).isEqualTo("This is all there is.");
                switch(note.inReplyTo()) {
                    case Note note1 -> {
                        assertThat(note1.summary()).isEqualTo("Previous note");
                        assertThat(note1.content()).isEqualTo("What else is there?");
                    }
                    default -> fail("Did not get the expected type for note.inReplyTo");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample084() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_084.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.summary()).isEqualTo("A simple note");
                assertThat(note.content()).isEqualTo("This is all there is.");
                switch(note.inReplyTo()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                    default -> fail("Did not get the expected type for note.inReplyTo");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample085() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_085.json"), LinkOrObject.class);
        switch(object) {
            case Listen listen -> {
                assertThat(listen.summary()).isEqualTo("Sally listened to a piece of music on the Acme Music Service");
                switch(listen.actor()) {
                    case Person person -> assertThat(person.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for listen.actor");
                }
                switch(listen.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/foo.mp3");
                    default -> fail("Did not get the expected type for listen.object");
                }
                switch(listen.instrument()) {
                    case Service service -> assertThat(service.name()).isEqualTo("Acme Music Service");
                    default -> fail("Did not get the expected type for listen.instrument");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample086() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_086.json"), LinkOrObject.class);
        switch(object) {
            case Collection collection -> {
                assertThat(collection.summary()).isEqualTo("A collection");
                assertThat(collection.totalItems()).isEqualTo(3);
                switch(collection.last()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/collection?page=1");
                    default -> fail("Did not get the expected type for collection.last");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample087() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_087.json"), LinkOrObject.class);
        switch(object) {
            case Collection collection -> {
                assertThat(collection.summary()).isEqualTo("A collection");
                assertThat(collection.totalItems()).isEqualTo(5);
                switch(collection.last()) {
                    case Link link -> {
                        assertThat(link.summary()).isEqualTo("Last Page");
                        assertThat(link.href()).isEqualTo("http://example.org/collection?page=1");
                    }
                    default -> fail("Did not get the expected type for collection.last");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample088() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_088.json"), LinkOrObject.class);
        switch(object) {
            case Person person -> {
                assertThat(person.name()).isEqualTo("Sally");
                switch(person.location()) {
                    case Place place -> {
                        assertThat(place.name()).isEqualTo("Over the Arabian Sea, east of Socotra Island Nature Sanctuary");
                        assertThat(place.longitude()).isEqualTo(12.34f);
                        assertThat(place.latitude()).isEqualTo(56.78f);
                        assertThat(place.altitude()).isEqualTo(90);
                        assertThat(place.units()).isEqualTo("m");
                    }
                    default -> fail("Did not get the expected type for person.location");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample089() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_089.json"), LinkOrObject.class);
        switch(object) {
            case Collection collection -> {
                assertThat(collection.summary()).isEqualTo("Sally's notes");
                assertThat(collection.totalItems()).isEqualTo(2);
                switch(collection.items().get(0)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Reminder for Going-Away Party");
                    default -> fail("Did not get the expected type for collection.items[0]");
                }
                switch(collection.items().get(1)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Meeting 2016-11-17");
                    default -> fail("Did not get the expected type for collection.items[1]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample090() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_090.json"), LinkOrObject.class);
        switch(object) {
            case OrderedCollection collection -> {
                assertThat(collection.summary()).isEqualTo("Sally's notes");
                assertThat(collection.totalItems()).isEqualTo(2);
                switch(collection.orderedItems().get(0)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Meeting 2016-11-17");
                    default -> fail("Did not get the expected type for collection.orderedItems[0]");
                }
                switch(collection.orderedItems().get(1)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Reminder for Going-Away Party");
                    default -> fail("Did not get the expected type for collection.orderedItems[1]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample091() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_091.json"), LinkOrObject.class);
        switch(object) {
            case Question question -> {
                assertThat(question.name()).isEqualTo("What is the answer?");
                switch(question.oneOf()) {
                    case LinkOrObjectList list -> {
                        switch(list.get(0)) {
                            case Note note -> assertThat(note.name()).isEqualTo("Option A");
                            default -> fail("Did not get the expected type for list.orderedItems[0]");
                        }
                        switch(list.get(1)) {
                            case Note note -> assertThat(note.name()).isEqualTo("Option B");
                            default -> fail("Did not get the expected type for list.orderedItems[1]");
                        }
                    }
                    default -> fail("Did not get the expected type for question.oneOf");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample092() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_092.json"), LinkOrObject.class);
        switch(object) {
            case Question question -> {
                assertThat(question.name()).isEqualTo("What is the answer?");
                switch(question.anyOf()) {
                    case LinkOrObjectList list -> {
                        switch(list.get(0)) {
                            case Note note -> assertThat(note.name()).isEqualTo("Option A");
                            default -> fail("Did not get the expected type for list.orderedItems[0]");
                        }
                        switch(list.get(1)) {
                            case Note note -> assertThat(note.name()).isEqualTo("Option B");
                            default -> fail("Did not get the expected type for list.orderedItems[1]");
                        }
                    }
                    default -> fail("Did not get the expected type for question.anyOf");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample093() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_093.json"), LinkOrObject.class);
        switch(object) {
            case Question question -> {
                assertThat(question.name()).isEqualTo("What is the answer?");
                assertThat(question.closed()).isEqualTo(ZonedDateTime.parse("2016-05-10T00:00:00Z"));
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample094() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_094.json"), LinkOrObject.class);
        switch(object) {
            case Move move -> {
                assertThat(move.summary()).isEqualTo("Sally moved a post from List A to List B");
                switch(move.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("Did not get the expected type for move.actor");
                }
                switch(move.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                    default -> fail("Did not get the expected type for move.object");
                }
                switch(move.target()) {
                    case Collection collection -> assertThat(collection.name()).isEqualTo("List B");
                    default -> fail("Did not get the expected type for move.target");
                }
                switch(move.origin()) {
                    case Collection collection -> assertThat(collection.name()).isEqualTo("List A");
                    default -> fail("Did not get the expected type for move.origin");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample095() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_095.json"), LinkOrObject.class);
        switch(object) {
            case CollectionPage collectionPage -> {
                assertThat(collectionPage.summary()).isEqualTo("Page 2 of Sally's blog posts");
                switch(collectionPage.next()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/collection?page=2");
                    default -> fail("Did not get the expected type for collectionPage.next");
                }
                switch(collectionPage.items().get(0)) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                    default -> fail("Did not get the expected type for collectionPage.items[0]");
                }
                switch(collectionPage.items().get(1)) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/2");
                    default -> fail("Did not get the expected type for collectionPage.items[1]");
                }
                switch(collectionPage.items().get(2)) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/3");
                    default -> fail("Did not get the expected type for collectionPage.items[2]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample096() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_096.json"), LinkOrObject.class);
        switch(object) {
            case CollectionPage collectionPage -> {
                assertThat(collectionPage.summary()).isEqualTo("Page 2 of Sally's blog posts");
                switch(collectionPage.next()) {
                    case Link link -> {
                        assertThat(link.name()).isEqualTo("Next Page");
                        assertThat(link.href()).isEqualTo("http://example.org/collection?page=2");
                    }
                    default -> fail("Did not get the expected type for collectionPage.next");
                }
                switch(collectionPage.items().get(0)) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                    default -> fail("Did not get the expected type for collectionPage.items[0]");
                }
                switch(collectionPage.items().get(1)) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/2");
                    default -> fail("Did not get the expected type for collectionPage.items[1]");
                }
                switch(collectionPage.items().get(2)) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/3");
                    default -> fail("Did not get the expected type for collectionPage.items[2]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample097() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_097.json"), LinkOrObject.class);
        switch(object) {
            case Like like -> {
                assertThat(like.summary()).isEqualTo("Sally liked a post");
                switch(like.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("Did not get the expected type for like.actor");
                }
                switch(like.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                    default -> fail("Did not get the expected type for like.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample098() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_098.json"), LinkOrObject.class);
        switch(object) {
            case Like like -> {
                switch(like.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("Did not get the expected type for like.actor");
                }
                switch(like.object()) {
                    case Note note -> assertThat(note.content()).isEqualTo("A simple note");
                    default -> fail("Did not get the expected type for like.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample099() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_099.json"), LinkOrObject.class);
        switch(object) {
            case Like like -> {
                assertThat(like.summary()).isEqualTo("Sally liked a note");
                switch(like.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("Did not get the expected type for like.actor");
                }
                switch(like.object()) {
                    case LinkOrObjectList list -> {
                        switch(list.get(0)) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                            default -> fail("Did not get the expected type for like.object[0]");
                        }
                        switch(list.get(1)) {
                            case Note note -> {
                                assertThat(note.summary()).isEqualTo("A simple note");
                                assertThat(note.content()).isEqualTo("That is a tree.");
                            }
                            default -> fail("Did not get the expected type for like.object[1]");
                        }
                    }
                    default -> fail("Did not get the expected type for like.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample100() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_100.json"), LinkOrObject.class);
        switch(object) {
            case CollectionPage collectionPage -> {
                assertThat(collectionPage.summary()).isEqualTo("Page 1 of Sally's blog posts");
                switch(collectionPage.prev()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/collection?page=1");
                    default -> fail("Did not get the expected type for collectionPage.prev");
                }
                switch(collectionPage.items().get(0)) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                    default -> fail("Did not get the expected type for collectionPage.items[0]");
                }
                switch(collectionPage.items().get(1)) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/2");
                    default -> fail("Did not get the expected type for collectionPage.items[1]");
                }
                switch(collectionPage.items().get(2)) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/3");
                    default -> fail("Did not get the expected type for collectionPage.items[2]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample101() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_101.json"), LinkOrObject.class);
        switch(object) {
            case CollectionPage collectionPage -> {
                assertThat(collectionPage.summary()).isEqualTo("Page 1 of Sally's blog posts");
                switch(collectionPage.prev()) {
                    case Link link -> {
                        assertThat(link.name()).isEqualTo("Previous Page");
                        assertThat(link.href()).isEqualTo("http://example.org/collection?page=1");
                    }
                    default -> fail("Did not get the expected type for collectionPage.prev");
                }
                switch(collectionPage.items().get(0)) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                    default -> fail("Did not get the expected type for collectionPage.items[0]");
                }
                switch(collectionPage.items().get(1)) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/2");
                    default -> fail("Did not get the expected type for collectionPage.items[1]");
                }
                switch(collectionPage.items().get(2)) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/3");
                    default -> fail("Did not get the expected type for collectionPage.items[2]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample102() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_102.json"), LinkOrObject.class);
        switch(object) {
            case Video video -> {
                assertThat(video.name()).isEqualTo("Cool New Movie");
                assertThat(video.duration()).isEqualTo("PT2H30M");
                switch(video.preview()) {
                    case Video preview -> {
                        assertThat(preview.name()).isEqualTo("Trailer");
                        assertThat(preview.duration()).isEqualTo("PT1M");
                        assertThat(preview.url().get(0).href()).isEqualTo("http://example.org/trailer.mkv");
                        assertThat(preview.url().get(0).mediaType()).isEqualTo("video/mkv");
                    }
                    default -> fail("Did not get the expected type for video.preview");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample103() throws Exception {
        // Non-standard type property, see example_103_a for what this example was intende to test, ie. property "Activity.resource"
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_103.json"), LinkOrObject.class);
        switch(object) {
            case UntypedObject untyped -> {
                assertThat(untyped.summary()).isEqualTo("Sally checked that her flight was on time");
                switch(untyped.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("Did not get the expected type for untyped.actor");
                }
                switch(untyped.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/flights/1");
                    default -> fail("Did not get the expected type for untyped.object");
                }
                switch(untyped.result()) {
                    case UntypedObject untyped1 -> assertThat(untyped1.name()).isEqualTo("On Time");
                    default -> fail("Did not get the expected type for untyped.result");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample103a() throws Exception {
        // Modified example file where the "type" property has a value jackson SubType can parse
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_103_a.json"), LinkOrObject.class);
        switch(object) {
            case Activity activity -> {
                assertThat(activity.summary()).isEqualTo("Sally checked that her flight was on time");
                switch(activity.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("Did not get the expected type for activity.actor");
                }
                switch(activity.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/flights/1");
                    default -> fail("Did not get the expected type for activity.object");
                }
                switch(activity.result()) {
                    // This property has an object but also with a weird type attribute
                    case UntypedObject untyped -> assertThat(untyped.name()).isEqualTo("On Time");
                    default -> fail("Did not get the expected type for activity.result");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample104() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_104.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.summary()).isEqualTo("A simple note");
                assertThat(note.id()).isEqualTo("http://www.test.example/notes/1");
                assertThat(note.content()).isEqualTo("I am fine.");
                assertThat(note.replies().totalItems()).isEqualTo(1);
                switch(note.replies().items().get(0)) {
                    case Note note1 -> {
                        assertThat(note1.summary()).isEqualTo("A response to the note");
                        assertThat(note1.content()).isEqualTo("I am glad to hear it.");
                        switch (note1.inReplyTo()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://www.test.example/notes/1");
                            default -> fail("Did not get the expected type for note1.inReplyTo");
                        }
                    }
                    default -> fail("Did not get the expected type for note.replies[0]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample105() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_105.json"), LinkOrObject.class);
        switch(object) {
            case Image image -> {
                assertThat(image.summary()).isEqualTo("Picture of Sally");
                assertThat(image.url().get(0).href()).isEqualTo("http://example.org/sally.jpg");
                switch(image.tag()) {
                    case LinkOrObjectList list -> {
                        switch(list.get(0)) {
                            case Person person -> {
                                assertThat(person.name()).isEqualTo("Sally");
                                assertThat(person.id()).isEqualTo("http://sally.example.org");
                            }
                            default -> fail("Did not get the expected type for list[0]");
                        }
                    }
                    default -> fail("Did not get the expected type for image.tag");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample106() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_106.json"), LinkOrObject.class);
        switch(object) {
            case Offer offer -> {
                assertThat(offer.summary()).isEqualTo("Sally offered the post to John");
                switch(offer.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("Did not get the expected type for offer.actor");
                }
                switch(offer.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                    default -> fail("Did not get the expected type for offer.object");
                }
                switch(offer.target()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://john.example.org");
                    default -> fail("Did not get the expected type for offer.target");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample107() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_107.json"), LinkOrObject.class);
        switch(object) {
            case Offer offer -> {
                assertThat(offer.summary()).isEqualTo("Sally offered the post to John");
                switch(offer.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("Did not get the expected type for offer.actor");
                }
                switch(offer.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                    default -> fail("Did not get the expected type for offer.object");
                }
                switch(offer.target()) {
                    case Person person -> assertThat(person.name()).isEqualTo("John");
                    default -> fail("Did not get the expected type for offer.target");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample108() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_108.json"), LinkOrObject.class);
        switch(object) {
            case Offer offer -> {
                assertThat(offer.summary()).isEqualTo("Sally offered the post to John");
                switch(offer.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("Did not get the expected type for offer.actor");
                }
                switch(offer.object()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/posts/1");
                    default -> fail("Did not get the expected type for offer.object");
                }
                switch(offer.target()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://john.example.org");
                    default -> fail("Did not get the expected type for offer.target");
                }
                switch(offer.to()) {
                    case LinkOrObjectList list -> {
                        assertThat(list).hasSize(1);
                        switch(list.get(0)) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://joe.example.org");
                            default -> fail("Did not get the expected type for list[0]");
                        }
                    }
                    default -> fail("Did not get the expected type for offer.to");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample109() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_109.json"), LinkOrObject.class);
        switch(object) {
            case Document document -> {
                assertThat(document.name()).isEqualTo("4Q Sales Forecast");
                assertThat(document.url()).hasSize(1);
                assertThat(document.url().get(0).href()).isEqualTo("http://example.org/4q-sales-forecast.pdf");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample110() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_110.json"), LinkOrObject.class);
        switch(object) {
            case Document document -> {
                assertThat(document.name()).isEqualTo("4Q Sales Forecast");
                assertThat(document.url()).hasSize(1);
                assertThat(document.url().get(0).href()).isEqualTo("http://example.org/4q-sales-forecast.pdf");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample111() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_111.json"), LinkOrObject.class);
        switch(object) {
            case Document document -> {
                assertThat(document.name()).isEqualTo("4Q Sales Forecast");
                assertThat(document.url()).hasSize(2);
                assertThat(document.url().get(0).href()).isEqualTo("http://example.org/4q-sales-forecast.pdf");
                assertThat(document.url().get(0).mediaType()).isEqualTo("application/pdf");
                assertThat(document.url().get(1).href()).isEqualTo("http://example.org/4q-sales-forecast.html");
                assertThat(document.url().get(1).mediaType()).isEqualTo("text/html");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample112() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_112.json"), LinkOrObject.class);
        switch(object) {
            case Place place -> {
                assertThat(place.name()).isEqualTo("Liu Gu Lu Cun, Pingdu, Qingdao, Shandong, China");
                assertThat(place.latitude()).isEqualTo(36.75f);
                assertThat(place.longitude()).isEqualTo(119.7667f);
                assertThat(place.accuracy()).isEqualTo(94.5f);
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample113() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_113.json"), LinkOrObject.class);
        switch(object) {
            case Place place -> {
                assertThat(place.name()).isEqualTo("Fresno Area");
                assertThat(place.altitude()).isEqualTo(15.0f);
                assertThat(place.latitude()).isEqualTo(36.75f);
                assertThat(place.longitude()).isEqualTo(119.7667f);
                assertThat(place.units()).isEqualTo("miles");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample114() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_114.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.summary()).isEqualTo("A simple note");
                assertThat(note.content()).isEqualTo("A <em>simple</em> note");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample115() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_115.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.summary()).isEqualTo("A simple note");
                assertThat(note.contentMap())
                    .hasFieldOrPropertyWithValue("en", "A <em>simple</em> note")
                    .hasFieldOrPropertyWithValue("es", "Una nota <em>sencilla</em>")
                    .hasFieldOrPropertyWithValue("zh-Hans", "一段<em>简单的</em>笔记");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample116() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_116.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.summary()).isEqualTo("A simple note");
                assertThat(note.mediaType()).isEqualTo("text/markdown");
                assertThat(note.content()).isEqualTo("## A simple note\nA simple markdown `note`");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample117() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_117.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.name()).isEqualTo("A simple note");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample118() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_118.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.nameMap())
                    .hasFieldOrPropertyWithValue("en", "A simple note")
                    .hasFieldOrPropertyWithValue("es", "Una nota sencilla")
                    .hasFieldOrPropertyWithValue("zh-Hans", "一段简单的笔记");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample119() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_119.json"), LinkOrObject.class);
        switch(object) {
            case Video video -> {
                assertThat(video.name()).isEqualTo("Birds Flying");
                assertThat(video.url().get(0).href()).isEqualTo("http://example.org/video.mkv");
                assertThat(video.duration()).isEqualTo("PT2H");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample120() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_120.json"), LinkOrObject.class);
        switch(object) {
            case Link link -> {
                assertThat(link.href()).isEqualTo("http://example.org/image.png");
                assertThat(link.height()).isEqualTo(100);
                assertThat(link.width()).isEqualTo(100);
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample121() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_121.json"), LinkOrObject.class);
        switch(object) {
            case Link link -> {
                assertThat(link.href()).isEqualTo("http://example.org/abc");
                assertThat(link.mediaType()).isEqualTo("text/html");
                assertThat(link.name()).isEqualTo("Previous");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample122() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_122.json"), LinkOrObject.class);
        switch(object) {
            case Link link -> {
                assertThat(link.href()).isEqualTo("http://example.org/abc");
                assertThat(link.hreflang()).isEqualTo("en");
                assertThat(link.mediaType()).isEqualTo("text/html");
                assertThat(link.name()).isEqualTo("Previous");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample123() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_123.json"), LinkOrObject.class);
        switch(object) {
            case CollectionPage page -> {
                assertThat(page.summary()).isEqualTo("Page 1 of Sally's notes");
                assertThat(page.id()).isEqualTo("http://example.org/collection?page=1");
                assertThat(page.partOf()).isEqualTo("http://example.org/collection");
                assertThat(page.items()).hasSize(2);
                switch(page.items().get(0)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Pizza Toppings to Try");
                    default -> fail("Did not get expected type for page.items[0]");
                }
                switch(page.items().get(1)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Thought about California");
                    default -> fail("Did not get expected type for page.items[1]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample124() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_124.json"), LinkOrObject.class);
        switch(object) {
            case Place place -> {
                assertThat(place.name()).isEqualTo("Fresno Area");
                assertThat(place.latitude()).isEqualTo(36.75f);
                assertThat(place.longitude()).isEqualTo(119.7667f);
                assertThat(place.radius()).isEqualTo(15);
                assertThat(place.units()).isEqualTo("miles");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample125() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_125.json"), LinkOrObject.class);
        switch(object) {
            case Place place -> {
                assertThat(place.name()).isEqualTo("Fresno Area");
                assertThat(place.latitude()).isEqualTo(36.75f);
                assertThat(place.longitude()).isEqualTo(119.7667f);
                assertThat(place.radius()).isEqualTo(15);
                assertThat(place.units()).isEqualTo("miles");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample126() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_126.json"), LinkOrObject.class);
        switch(object) {
            case Link link -> {
                assertThat(link.href()).isEqualTo("http://example.org/abc");
                assertThat(link.hreflang()).isEqualTo("en");
                assertThat(link.mediaType()).isEqualTo("text/html");
                assertThat(link.name()).isEqualTo("Next");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample127() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_127.json"), LinkOrObject.class);
        switch(object) {
            case Event event -> {
                assertThat(event.name()).isEqualTo("Going-Away Party for Jim");
                assertThat(event.startTime()).isEqualTo(ZonedDateTime.parse("2014-12-31T23:00:00-08:00"));
                assertThat(event.endTime()).isEqualTo(ZonedDateTime.parse("2015-01-01T06:00:00-08:00"));
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample128() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_128.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.summary()).isEqualTo("A simple note");
                assertThat(note.content()).isEqualTo("Fish swim.");
                assertThat(note.published()).isEqualTo(ZonedDateTime.parse("2014-12-12T12:12:12Z"));
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample129() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_129.json"), LinkOrObject.class);
        switch(object) {
            case Event event -> {
                assertThat(event.name()).isEqualTo("Going-Away Party for Jim");
                assertThat(event.startTime()).isEqualTo(ZonedDateTime.parse("2014-12-31T23:00:00-08:00"));
                assertThat(event.endTime()).isEqualTo(ZonedDateTime.parse("2015-01-01T06:00:00-08:00"));
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample130() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_130.json"), LinkOrObject.class);
        switch(object) {
            case Place place -> {
                assertThat(place.name()).isEqualTo("Fresno Area");
                assertThat(place.latitude()).isEqualTo(36.75f);
                assertThat(place.longitude()).isEqualTo(119.7667f);
                assertThat(place.radius()).isEqualTo(15);
                assertThat(place.units()).isEqualTo("miles");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample131() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_131.json"), LinkOrObject.class);
        switch(object) {
            case Link link -> {
                assertThat(link.href()).isEqualTo("http://example.org/abc");
                assertThat(link.hreflang()).isEqualTo("en");
                assertThat(link.mediaType()).isEqualTo("text/html");
                assertThat(link.name()).isEqualTo("Preview");
                assertThat(link.rel()).hasSize(2).containsExactly("canonical", "preview");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample132() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_132.json"), LinkOrObject.class);
        switch(object) {
            case OrderedCollectionPage page -> {
                assertThat(page.summary()).isEqualTo("Page 1 of Sally's notes");
                assertThat(page.startIndex()).isEqualTo(0);
                assertThat(page.orderedItems()).hasSize(2);
                switch(page.orderedItems().get(0)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Density of Water");
                    default -> fail("Did not get expected type for page.items[0]");
                }
                switch(page.orderedItems().get(1)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Air Mattress Idea");
                    default -> fail("Did not get expected type for page.items[1]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample133() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_133.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.name()).isEqualTo("Cane Sugar Processing");
                assertThat(note.summary()).isEqualTo("A simple <em>note</em>");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample134() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_134.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.name()).isEqualTo("Cane Sugar Processing");
                assertThat(note.summaryMap())
                    .hasFieldOrPropertyWithValue("en", "A simple <em>note</em>")
                    .hasFieldOrPropertyWithValue("es", "Una <em>nota</em> sencilla")
                    .hasFieldOrPropertyWithValue("zh-Hans", "一段<em>简单的</em>笔记");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample135() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_135.json"), LinkOrObject.class);
        switch(object) {
            case Collection collection -> {
                assertThat(collection.summary()).isEqualTo("Sally's notes");
                assertThat(collection.totalItems()).isEqualTo(2);
                switch(collection.items().get(0)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Which Staircase Should I Use");
                    default -> fail("Did not get the expected type for collection.orderedItems[0]");
                }
                switch(collection.items().get(1)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Something to Remember");
                    default -> fail("Did not get the expected type for collection.orderedItems[1]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample136() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_136.json"), LinkOrObject.class);
        switch(object) {
            case Place place -> {
                assertThat(place.name()).isEqualTo("Fresno Area");
                assertThat(place.latitude()).isEqualTo(36.75f);
                assertThat(place.longitude()).isEqualTo(119.7667f);
                assertThat(place.radius()).isEqualTo(15);
                assertThat(place.units()).isEqualTo("miles");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample137() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_137.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.name()).isEqualTo("Cranberry Sauce Idea");
                assertThat(note.content()).isEqualTo("Mush it up so it does not have the same shape as the can.");
                assertThat(note.updated()).isEqualTo(ZonedDateTime.parse("2014-12-12T12:12:12Z"));
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample138() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_138.json"), LinkOrObject.class);
        switch(object) {
            case Link link -> {
                assertThat(link.href()).isEqualTo("http://example.org/image.png");
                assertThat(link.height()).isEqualTo(100);
                assertThat(link.width()).isEqualTo(100);
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample139() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_139.json"), LinkOrObject.class);
        switch(object) {
            case Relationship relationship -> {
                assertThat(relationship.summary()).isEqualTo("Sally is an acquaintance of John's");
                switch (relationship.subject()) {
                    case Person person -> assertThat(person.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for relationship.subject");
                }
                switch (relationship.relationship()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://purl.org/vocab/relationship/acquaintanceOf");
                    default -> fail("Did not get the expected type for relationship.relationship");
                }
                switch (relationship.object()) {
                    case Person person -> assertThat(person.name()).isEqualTo("John");
                    default -> fail("Did not get the expected type for relationship.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample140() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_140.json"), LinkOrObject.class);
        switch(object) {
            case Relationship relationship -> {
                assertThat(relationship.summary()).isEqualTo("Sally is an acquaintance of John's");
                switch (relationship.subject()) {
                    case Person person -> assertThat(person.name()).isEqualTo("Sally");
                    default -> fail("Did not get the expected type for relationship.subject");
                }
                switch (relationship.relationship()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://purl.org/vocab/relationship/acquaintanceOf");
                    default -> fail("Did not get the expected type for relationship.relationship");
                }
                switch (relationship.object()) {
                    case Person person -> assertThat(person.name()).isEqualTo("John");
                    default -> fail("Did not get the expected type for relationship.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample141() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_141.json"), LinkOrObject.class);
        switch(object) {
            case Profile profile -> {
                assertThat(profile.summary()).isEqualTo("Sally's profile");
                switch (profile.describes()) {
                    case Person person -> assertThat(person.name()).isEqualTo("Sally");
                    default -> fail("did not get the expected type for summary.describes");
                }
                assertThat(profile.url()).hasSize(1);
                assertThat(profile.url().get(0).href()).isEqualTo("http://sally.example.org");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample142() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_142.json"), LinkOrObject.class);
        switch(object) {
            case Tombstone tombstone -> {
                assertThat(tombstone.summary()).isEqualTo("This image has been deleted");
                assertThat(tombstone.formerType()).isEqualTo("Image");
                assertThat(tombstone.url().get(0).href()).isEqualTo("http://example.org/image/2");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample143() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_143.json"), LinkOrObject.class);
        switch(object) {
            case Tombstone tombstone -> {
                assertThat(tombstone.summary()).isEqualTo("This image has been deleted");
                assertThat(tombstone.deleted()).isEqualTo(ZonedDateTime.parse("2016-05-03T00:00:00Z"));
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample144() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_144.json"), LinkOrObject.class);
        switch(object) {
            case Collection collection -> {
                assertThat(collection.summary()).isEqualTo("Activities in Project XYZ");
                assertThat(collection.items()).hasSize(2);
                switch(collection.items().get(0)) {
                    case Create create -> {
                        assertThat(create.summary()).isEqualTo("Sally created a note");
                        assertThat(create.id()).isEqualTo("http://activities.example.com/1");
                        switch(create.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                            default -> fail("Did not get the expected type for create.actor");
                        }
                        switch(create.object()) {
                            case Note note -> {
                                assertThat(note.summary()).isEqualTo("A note");
                                assertThat(note.id()).isEqualTo("http://notes.example.com/1");
                                assertThat(note.content()).isEqualTo("A note");
                            }
                            default -> fail("Did not get the expected type for create.object");
                        }
                        switch(create.audience()) {
                            case Group group -> assertThat(group.name()).isEqualTo("Project XYZ Working Group");
                            default -> fail("Did not get the expected type for create.audience");
                        }
                        switch(create.to()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://john.example.org");
                            default -> fail("Did not get the expected type for create.to");
                        }
                    }
                    default -> fail("Did not get the expected type for collection.items[0]");
                }
                switch(collection.items().get(1)) {
                    case Like like -> {
                        assertThat(like.summary()).isEqualTo("John liked Sally's note");
                        assertThat(like.id()).isEqualTo("http://activities.example.com/1");
                        switch(like.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://john.example.org");
                            default -> fail("Did not get the expected type for like.actor");
                        }
                        switch(like.object()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://notes.example.com/1");
                            default -> fail("Did not get the expected type for like.object");
                        }
                        switch(like.audience()) {
                            case Group group -> assertThat(group.name()).isEqualTo("Project XYZ Working Group");
                            default -> fail("Did not get the expected type for like.audience");
                        }
                        switch(like.to()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                            default -> fail("Did not get the expected type for like.to");
                        }
                    }
                    default -> fail("Did not get the expected type for collection.items[1]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample145() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_145.json"), LinkOrObject.class);
        switch(object) {
            case Collection collection -> {
                assertThat(collection.summary()).isEqualTo("Sally's friends list");
                assertThat(collection.items()).hasSize(2);
                switch(collection.items().get(0)) {
                    case Relationship relationship -> {
                        assertThat(relationship.summary()).isEqualTo("Sally is influenced by Joe");
                        switch(relationship.subject()) {
                            case Person person -> assertThat(person.name()).isEqualTo("Sally");
                            default -> fail("Did not get the expected type for relationship.subject");
                        }
                        switch(relationship.relationship()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://purl.org/vocab/relationship/influencedBy");
                            default -> fail("Did not get the expected type for relationship.relationship");
                        }
                        switch(relationship.object()) {
                            case Person person -> {
                                assertThat(person.name()).isEqualTo("Joe");
                            }
                            default -> fail("Did not get the expected type for relationship.object");
                        }
                    }
                    default -> fail("Did not get the expected type for collection.items[0]");
                }
                switch(collection.items().get(1)) {
                    case Relationship relationship -> {
                        assertThat(relationship.summary()).isEqualTo("Sally is a friend of Jane");
                        switch(relationship.subject()) {
                            case Person person -> assertThat(person.name()).isEqualTo("Sally");
                            default -> fail("Did not get the expected type for relationship.subject");
                        }
                        switch(relationship.relationship()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://purl.org/vocab/relationship/friendOf");
                            default -> fail("Did not get the expected type for relationship.relationship");
                        }
                        switch(relationship.object()) {
                            case Person person -> assertThat(person.name()).isEqualTo("Jane");
                            default -> fail("Did not get the expected type for relationship.object");
                        }
                    }
                    default -> fail("Did not get the expected type for collection.items[1]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample146() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_146.json"), LinkOrObject.class);
        switch(object) {
            case Create create -> {
                assertThat(create.summary()).isEqualTo("Sally became a friend of Matt");
                switch (create.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("Did not get the expected type for create.actor");
                }
                switch (create.object()) {
                    case Relationship relationship -> {
                        switch (relationship.subject()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                            default -> fail("Did not get the expected type for relationship.subject");
                        }
                        switch (relationship.relationship()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://purl.org/vocab/relationship/friendOf");
                            default -> fail("Did not get the expected type for relationship.relationship");
                        }
                        switch (relationship.object()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://matt.example.org");
                            default -> fail("Did not get the expected type for relationship.object");
                        }
                        assertThat(relationship.startTime()).isEqualTo(LocalDateTime.parse("2015-04-21T12:34:56").atZone(ZoneId.of("UTC")));

                    }
                    default -> fail("Did not get the expected type for create.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample147() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_147.json"), LinkOrObject.class);
        switch(object) {
            case Offer offer -> {
                assertThat(offer.summary()).isEqualTo("Sally requested to be a friend of John");
                assertThat(offer.id()).isEqualTo("http://example.org/connection-requests/123");
                switch (offer.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("acct:sally@example.org");
                    default -> fail("Did not get the expected type for offer.actor");
                }
                switch (offer.object()) {
                    case Relationship relationship -> {
                        assertThat(relationship.summary()).isEqualTo("Sally and John's friendship");
                        assertThat(relationship.id()).isEqualTo("http://example.org/connections/123");
                        switch (relationship.subject()) {
                            case Link link -> assertThat(link.href()).isEqualTo("acct:sally@example.org");
                            default -> fail("Did not get the expected type for relationship.subject");
                        }
                        switch (relationship.relationship()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://purl.org/vocab/relationship/friendOf");
                            default -> fail("Did not get the expected type for relationship.relationship");
                        }
                        switch (relationship.object()) {
                            case Link link -> assertThat(link.href()).isEqualTo("acct:john@example.org");
                            default -> fail("Did not get the expected type for relationship.object");
                        }
                    }
                    default -> fail("Did not get the expected type for offer.object");
                }
                switch (offer.target()) {
                    case Link link -> assertThat(link.href()).isEqualTo("acct:john@example.org");
                    default -> fail("Did not get the expected type for offer.target");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample148() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_148.json"), LinkOrObject.class);
        switch(object) {
            case Collection collection -> {
                assertThat(collection.summary()).isEqualTo("Sally and John's relationship history");
                assertThat(collection.items()).hasSize(5);
                switch (collection.items().get(0)) {
                    case Accept accept -> {
                        assertThat(accept.summary()).isEqualTo("John accepted Sally's friend request");
                        assertThat(accept.id()).isEqualTo("http://example.org/activities/122");
                        switch (accept.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("acct:john@example.org");
                            default -> fail("Did not get the expected type for accept.actor");
                        }
                        switch (accept.object()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://example.org/connection-requests/123");
                            default -> fail("Did not get the expected type for accept.object");
                        }
                        switch (accept.result()) {
                            case LinkOrObjectList list -> {
                                assertThat(list).hasSize(4);
                                switch (list.get(0)) {
                                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/activities/123");
                                    default -> fail("Did not get the expected type for accept.object");
                                }
                                switch (list.get(1)) {
                                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/activities/124");
                                    default -> fail("Did not get the expected type for accept.object");
                                }
                                switch (list.get(2)) {
                                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/activities/125");
                                    default -> fail("Did not get the expected type for accept.object");
                                }
                                switch (list.get(3)) {
                                    case Link link -> assertThat(link.href()).isEqualTo("http://example.org/activities/126");
                                    default -> fail("Did not get the expected type for accept.object");
                                }
                            }
                            default -> fail("Did not get the expected type for accept.result");
                        }
                    }
                    default -> fail("Did not get the expected type for collection[0]");
                }
                switch (collection.items().get(1)) {
                    case Follow follow -> {
                        assertThat(follow.summary()).isEqualTo("John followed Sally");
                        assertThat(follow.id()).isEqualTo("http://example.org/activities/123");
                        switch (follow.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("acct:john@example.org");
                            default -> fail("Did not get the expected type for follow.actor");
                        }
                        switch (follow.object()) {
                            case Link link -> assertThat(link.href()).isEqualTo("acct:sally@example.org");
                            default -> fail("Did not get the expected type for follow.object");
                        }
                    }
                    default -> fail("Did not get the expected type for collection[1]");
                }
                switch (collection.items().get(2)) {
                    case Follow follow -> {
                        assertThat(follow.summary()).isEqualTo("Sally followed John");
                        assertThat(follow.id()).isEqualTo("http://example.org/activities/124");
                        switch (follow.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("acct:sally@example.org");
                            default -> fail("Did not get the expected type for follow.actor");
                        }
                        switch (follow.object()) {
                            case Link link -> assertThat(link.href()).isEqualTo("acct:john@example.org");
                            default -> fail("Did not get the expected type for follow.object");
                        }
                    }
                    default -> fail("Did not get the expected type for collection[2]");
                }
                switch (collection.items().get(3)) {
                    case Add add -> {
                        assertThat(add.summary()).isEqualTo("John added Sally to his friends list");
                        assertThat(add.id()).isEqualTo("http://example.org/activities/125");
                        switch (add.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("acct:john@example.org");
                            default -> fail("Did not get the expected type for add.actor");
                        }
                        switch (add.object()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://example.org/connections/123");
                            default -> fail("Did not get the expected type for add.object");
                        }
                        switch (add.target()) {
                            case Collection collection1 -> {
                                assertThat(collection1.summary()).isEqualTo("John's Connections");
                                assertThat(collection1.items()).isNull();
                            }
                            default -> fail("Did not get the expected type for add.target");
                        }
                    }
                    default -> fail("Did not get the expected type for collection[3]");
                }
                switch (collection.items().get(4)) {
                    case Add add -> {
                        assertThat(add.summary()).isEqualTo("Sally added John to her friends list");
                        assertThat(add.id()).isEqualTo("http://example.org/activities/126");
                        switch (add.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("acct:sally@example.org");
                            default -> fail("Did not get the expected type for add.actor");
                        }
                        switch (add.object()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://example.org/connections/123");
                            default -> fail("Did not get the expected type for add.object");
                        }
                        switch (add.target()) {
                            case Collection collection1 -> {
                                assertThat(collection1.summary()).isEqualTo("Sally's Connections");
                                assertThat(collection1.items()).isNull();
                            }
                            default -> fail("Did not get the expected type for add.target");
                        }
                    }
                    default -> fail("Did not get the expected type for collection[4]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample149() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_149.json"), LinkOrObject.class);
        switch(object) {
            case Place place -> assertThat(place.name()).isEqualTo("San Francisco, CA");
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample150() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_150.json"), LinkOrObject.class);
        switch(object) {
            case Place place -> {
                assertThat(place.name()).isEqualTo("San Francisco, CA");
                assertThat(place.longitude()).isEqualTo(122.4167f);
                assertThat(place.latitude()).isEqualTo(37.7833f);
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample151() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_151.json"), LinkOrObject.class);
        switch(object) {
            case Question question -> {
                assertThat(question.name()).isEqualTo("A question about robots");
                assertThat(question.id()).isEqualTo("http://help.example.org/question/1");
                assertThat(question.content()).isEqualTo("I'd like to build a robot to feed my cat. Should I use Arduino or Raspberry Pi?");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample152() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_152.json"), LinkOrObject.class);
        switch(object) {
            case Question question -> {
                assertThat(question.name()).isEqualTo("A question about robots");
                assertThat(question.id()).isEqualTo("http://polls.example.org/question/1");
                assertThat(question.content()).isEqualTo("I'd like to build a robot to feed my cat. Which platform is best?");
                assertThat(question.oneOf()).hasSize(2);
                switch(question.oneOf().get(0)) {
                    case UntypedObject asobject -> assertThat(asobject.name()).isEqualTo("arduino");
                    default -> fail("Did not get the expected type for question.oneOf[0]");
                }
                switch(question.oneOf().get(1)) {
                    case UntypedObject asobject -> assertThat(asobject.name()).isEqualTo("raspberry pi");
                    default -> fail("Did not get the expected type for question.oneOf[1]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample153() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_153.json"), LinkOrObject.class);
        switch(object) {
            case UntypedObject asobject -> {
                switch (asobject.attributedTo()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("Did not get the expected type for asobject.attributedTo");
                }
                switch (asobject.inReplyTo()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://polls.example.org/question/1");
                    default -> fail("Did not get the expected type for asobject.inReplyTo");
                }
                assertThat(asobject.name()).isEqualTo("arduino");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample154() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_154.json"), LinkOrObject.class);
        switch(object) {
            case Question question -> {
                assertThat(question.name()).isEqualTo("A question about robots");
                assertThat(question.id()).isEqualTo("http://polls.example.org/question/1");
                assertThat(question.content()).isEqualTo("I'd like to build a robot to feed my cat. Which platform is best?");
                switch(question.oneOf()) {
                    case LinkOrObjectList list -> {
                        switch(list.get(0)) {
                            case UntypedObject asobject -> assertThat(asobject.name()).isEqualTo("arduino");
                            default -> fail("Did not get the expected type for list[0]");
                        }
                        switch(list.get(1)) {
                            case UntypedObject asobject -> assertThat(asobject.name()).isEqualTo("raspberry pi");
                            default -> fail("Did not get the expected type for list[1]");
                        }
                    }
                    default -> fail("Did not get the expected type for question.oneOf");
                }
                assertThat(question.replies().totalItems()).isEqualTo(3);
                assertThat(question.replies().items()).hasSize(3);
                switch(question.replies().items().get(0)) {
                    case UntypedObject asobject -> {
                        switch(asobject.attributedTo()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                            default -> fail("Did not get the expected type for question.items[0].attributedTo");
                        }
                        switch(asobject.inReplyTo()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://polls.example.org/question/1");
                            default -> fail("Did not get the expected type for question.items[0].inReplyTo");
                        }
                        assertThat(asobject.name()).isEqualTo("arduino");
                    }
                    default -> fail("Did not get the expected type for question.items[0]");
                }
                switch(question.replies().items().get(1)) {
                    case UntypedObject asobject -> {
                        switch(asobject.attributedTo()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://joe.example.org");
                            default -> fail("Did not get the expected type for question.items[1].attributedTo");
                        }
                        switch(asobject.inReplyTo()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://polls.example.org/question/1");
                            default -> fail("Did not get the expected type for question.items[1].inReplyTo");
                        }
                        assertThat(asobject.name()).isEqualTo("arduino");
                    }
                    default -> fail("Did not get the expected type for question.items[1]");
                }
                switch(question.replies().items().get(2)) {
                    case UntypedObject asobject -> {
                        switch(asobject.attributedTo()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://john.example.org");
                            default -> fail("Did not get the expected type for question.items[2].attributedTo");
                        }
                        switch(asobject.inReplyTo()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://polls.example.org/question/1");
                            default -> fail("Did not get the expected type for question.items[2].inReplyTo");
                        }
                        assertThat(asobject.name()).isEqualTo("raspberry pi");
                    }
                    default -> fail("Did not get the expected type for question.items[2]");
                }
                switch(question.result()) {
                    case Note note -> assertThat(note.content()).isEqualTo("Users are favoriting &quot;arduino&quot; by a 33% margin.");
                    default -> fail("Did not get the expected type for question.result");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample155() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_155.json"), LinkOrObject.class);
        switch(object) {
            case Collection collection -> {
                assertThat(collection.summary()).isEqualTo("History of John's note");
                assertThat(collection.items()).hasSize(2);
                switch (collection.items().get(0)) {
                    case Like like -> {
                        assertThat(like.summary()).isEqualTo("Sally liked John's note");
                        switch(like.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                            default -> fail("Did not get expected type for like.actor");
                        }
                        assertThat(like.id()).isEqualTo("http://activities.example.com/1");
                        assertThat(like.published()).isEqualTo(ZonedDateTime.parse("2015-11-12T12:34:56Z"));
                        switch(like.object()) {
                            case Note note -> {
                                assertThat(note.summary()).isEqualTo("John's note");
                                assertThat(note.id()).isEqualTo("http://notes.example.com/1");
                                switch(note.attributedTo()) {
                                    case Link link -> assertThat(link.href()).isEqualTo("http://john.example.org");
                                    default -> fail("Did not get expected type for note.attributedTo");
                                }
                                assertThat(note.content()).isEqualTo("My note");
                            }
                            default -> fail("Did not get the expected type for like.object");
                        }
                    }
                    default -> fail("Did not get the expected type for collection.orderedItems[0]");
                }
                switch (collection.items().get(1)) {
                    case Dislike dislike -> {
                        assertThat(dislike.summary()).isEqualTo("Sally disliked John's note");
                        switch(dislike.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                            default -> fail("Did not get expected type for dislike.actor");
                        }
                        assertThat(dislike.id()).isEqualTo("http://activities.example.com/2");
                        assertThat(dislike.published()).isEqualTo(ZonedDateTime.parse("2015-12-11T21:43:56Z"));
                        switch(dislike.object()) {
                            case Note note -> {
                                assertThat(note.summary()).isEqualTo("John's note");
                                assertThat(note.id()).isEqualTo("http://notes.example.com/1");
                                switch(note.attributedTo()) {
                                    case Link link -> assertThat(link.href()).isEqualTo("http://john.example.org");
                                    default -> fail("Did not get expected type for note.attributedTo");
                                }
                                assertThat(note.content()).isEqualTo("My note");
                            }
                            default -> fail("Did not get the expected type for dislike.object");
                        }
                    }
                    default -> fail("Did not get the expected type for collection.orderedItems[1]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample156() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_156.json"), LinkOrObject.class);
        switch(object) {
            case Collection collection -> {
                assertThat(collection.summary()).isEqualTo("History of John's note");
                assertThat(collection.items()).hasSize(2);
                switch (collection.items().get(0)) {
                    case Like like -> {
                        assertThat(like.summary()).isEqualTo("Sally liked John's note");
                        switch(like.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                            default -> fail("Did not get expected type for like.actor");
                        }
                        assertThat(like.id()).isEqualTo("http://activities.example.com/1");
                        assertThat(like.published()).isEqualTo(ZonedDateTime.parse("2015-11-12T12:34:56Z"));
                        switch(like.object()) {
                            case Note note -> {
                                assertThat(note.summary()).isEqualTo("John's note");
                                assertThat(note.id()).isEqualTo("http://notes.example.com/1");
                                switch(note.attributedTo()) {
                                    case Link link -> assertThat(link.href()).isEqualTo("http://john.example.org");
                                    default -> fail("Did not get expected type for note.attributedTo");
                                }
                                assertThat(note.content()).isEqualTo("My note");
                            }
                            default -> fail("Did not get the expected type for like.object");
                        }
                    }
                    default -> fail("Did not get the expected type for collection.orderedItems[0]");
                }
                switch (collection.items().get(1)) {
                    case Undo undo -> {
                        assertThat(undo.summary()).isEqualTo("Sally no longer likes John's note");
                        assertThat(undo.id()).isEqualTo("http://activities.example.com/2");
                        switch(undo.actor()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                            default -> fail("Did not get expected type for undo.actor");
                        }
                        assertThat(undo.published()).isEqualTo(ZonedDateTime.parse("2015-12-11T21:43:56Z"));
                        switch(undo.object()) {
                            case Link link -> assertThat(link.href()).isEqualTo("http://activities.example.com/1");
                            default -> fail("Did not get expected type for undo.object");
                        }
                    }
                    default -> fail("Did not get the expected type for collection.orderedItems[1]");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample157() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_157.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.name()).isEqualTo("A thank-you note");
                assertThat(note.content())
                    .isEqualTo("Thank you <a href='http://sally.example.org'>@sally</a>\nfor all your hard work!\n<a href='http://example.org/tags/givingthanks'>#givingthanks</a>");
                switch(note.to()) {
                    case Person person -> {
                        assertThat(person.name()).isEqualTo("Sally");
                        assertThat(person.id()).isEqualTo("http://sally.example.org");
                    }
                    default -> fail("Did not get expected type for note.to");
                }
                switch(note.tag()) {
                    case UntypedObject asobject -> assertThat(asobject.name()).isEqualTo("#givingthanks");
                    default -> fail("Did not get expected type for note.tag");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample158() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_158.json"), LinkOrObject.class);
        switch(object) {
            case Note note -> {
                assertThat(note.name()).isEqualTo("A thank-you note");
                assertThat(note.content()).isEqualTo("Thank you @sally for all your hard work! #givingthanks");
                switch(note.tag()) {
                    case LinkOrObjectList list -> {
                        assertThat(list).hasSize(2);
                        switch(list.get(0)) {
                            case Mention mention -> {
                                assertThat(mention.href()).isEqualTo("http://example.org/people/sally");
                                assertThat(mention.name()).isEqualTo("@sally");
                            }
                            default -> fail("Did not get expected type for list[0]");
                        }
                        switch(list.get(1)) {
                            case UntypedObject asobject -> {
                                assertThat(asobject.id()).isEqualTo("http://example.org/tags/givingthanks");
                                assertThat(asobject.name()).isEqualTo("#givingthanks");
                            }
                            default -> fail("Did not get expected type for list[1]");
                        }
                    }
                    default -> fail("Did not get expected type for note.tag");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample159() throws Exception {
        LinkOrObject object = mapper.readValue(activityStreamsExample("example_159.json"), LinkOrObject.class);
        switch(object) {
            case Move move -> {
                assertThat(move.summary()).isEqualTo("Sally moved the sales figures from Folder A to Folder B");
                switch(move.actor()) {
                    case Link link -> assertThat(link.href()).isEqualTo("http://sally.example.org");
                    default -> fail("Did not get the expected type for move.actor");
                }
                switch(move.object()) {
                    case Document document -> assertThat(document.name()).isEqualTo("sales figures");
                    default -> fail("Did not get the expected type for move.object");
                }
                switch(move.origin()) {
                    case Collection collection -> assertThat(collection.name()).isEqualTo("Folder A");
                    default -> fail("Did not get the expected type for move.origin");
                }
                switch(move.target()) {
                    case Collection collection -> assertThat(collection.name()).isEqualTo("Folder B");
                    default -> fail("Did not get the expected type for move.target");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    private InputStream activityStreamsExample(String classpathResource) {
        return this.getClass().getResourceAsStream("/json/activitystreams-vocabulary/" + classpathResource);
    }

}
