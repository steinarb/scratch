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

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ParseTest {
    static ObjectMapper mapper = new ObjectMapper();

    @Test
    void testParseExample01() throws Exception {
        LinkOrObject object = mapper.readValue(refactorExample("example_01.json"), LinkOrObject.class);
        switch(object) {
            case ActivityStreamObject asobject -> {
                assertThat(asobject.id()).isEqualTo("http://www.test.example/object/1");
                assertThat(asobject.name()).isEqualTo("A Simple, non-specific object");
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample02() throws Exception {
        LinkOrObject object = mapper.readValue(refactorExample("example_02.json"), LinkOrObject.class);
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
    void testParseExample03() throws Exception {
        LinkOrObject object = mapper.readValue(refactorExample("example_03.json"), LinkOrObject.class);
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
    void testParseExample04() throws Exception {
        LinkOrObject object = mapper.readValue(refactorExample("example_04.json"), LinkOrObject.class);
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
    void testParseExample05() throws Exception {
        LinkOrObject object = mapper.readValue(refactorExample("example_05.json"), LinkOrObject.class);
        switch(object) {
            case Collection collection -> {
                assertThat(collection.summary()).isEqualTo("Sally's notes");
                assertThat(collection.totalItems()).isEqualTo(2);
                switch (collection.items().get(0)) {
                    case Note note -> assertThat(note.name()).isEqualTo("A Simple Note");
                    default -> fail("Did not get the expected type for travel.actor");
                }
                switch (collection.items().get(1)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Another Simple Note");
                    default -> fail("Did not get the expected type for travel.actor");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample06() throws Exception {
        LinkOrObject object = mapper.readValue(refactorExample("example_06.json"), LinkOrObject.class);
        switch(object) {
            case OrderedCollection collection -> {
                assertThat(collection.summary()).isEqualTo("Sally's notes");
                assertThat(collection.totalItems()).isEqualTo(2);
                switch (collection.orderedItems().get(0)) {
                    case Note note -> assertThat(note.name()).isEqualTo("A Simple Note");
                    default -> fail("Did not get the expected type for travel.actor");
                }
                switch (collection.orderedItems().get(1)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Another Simple Note");
                    default -> fail("Did not get the expected type for travel.actor");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample07() throws Exception {
        LinkOrObject object = mapper.readValue(refactorExample("example_07.json"), LinkOrObject.class);
        switch(object) {
            case CollectionPage collectionPage -> {
                assertThat(collectionPage.summary()).isEqualTo("Page 1 of Sally's notes");
                assertThat(collectionPage.id()).isEqualTo("http://example.org/foo?page=1");
                assertThat(collectionPage.partOf()).isEqualTo("http://example.org/foo");
                switch (collectionPage.items().get(0)) {
                    case Note note -> assertThat(note.name()).isEqualTo("A Simple Note");
                    default -> fail("Did not get the expected type for travel.actor");
                }
                switch (collectionPage.items().get(1)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Another Simple Note");
                    default -> fail("Did not get the expected type for travel.actor");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    @Test
    void testParseExample08() throws Exception {
        LinkOrObject object = mapper.readValue(refactorExample("example_08.json"), LinkOrObject.class);
        switch(object) {
            case OrderedCollectionPage collectionPage -> {
                assertThat(collectionPage.summary()).isEqualTo("Page 1 of Sally's notes");
                assertThat(collectionPage.id()).isEqualTo("http://example.org/foo?page=1");
                assertThat(collectionPage.partOf()).isEqualTo("http://example.org/foo");
                switch (collectionPage.orderedItems().get(0)) {
                    case Note note -> assertThat(note.name()).isEqualTo("A Simple Note");
                    default -> fail("Did not get the expected type for travel.actor");
                }
                switch (collectionPage.orderedItems().get(1)) {
                    case Note note -> assertThat(note.name()).isEqualTo("Another Simple Note");
                    default -> fail("Did not get the expected type for travel.actor");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    private InputStream refactorExample(String classpathResource) {
        return this.getClass().getResourceAsStream("/json/activitystreams-vocabulary/" + classpathResource);
    }

}
