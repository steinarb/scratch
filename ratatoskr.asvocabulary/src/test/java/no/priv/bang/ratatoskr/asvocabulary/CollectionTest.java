/*
 * Copyright 2025 Steinar Bang
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

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class CollectionTest {

    @Test
    void testCreate() {
        Object context = null;
        String id = "http://localhost:8081/";
        String name = "John's followers";
        Map<String, String> nameMap = Collections.emptyMap();
        String summary = "Followers collection";
        Map<String, String> summaryMap = Collections.emptyMap();
        String content = "";
        Map<String, String> contentMap = Collections.emptyMap();
        String mediaType = "";
        List<Link> url = Collections.emptyList();
        LinkOrObject attributedTo = null;
        String duration = "";
        ZonedDateTime startTime = null;
        ZonedDateTime endTime = null;
        ZonedDateTime published = null;
        ZonedDateTime updated = null;
        LinkOrObject attachment = null;
        LinkOrObject audience = null;
        LinkOrObject to = null;
        LinkOrObject bcc = null;
        LinkOrObject bto = null;
        LinkOrObject cc = null;
        LinkOrObject generator = null;
        LinkOrObject icon = null;
        LinkOrObject image = null;
        LinkOrObject inReplyTo = null;
        LinkOrObject location = null;
        LinkOrObject preview = null;
        Collection replies = null;
        LinkOrObject tag = null;
        int totalItems = 2;
        LinkOrObject person1 = Person.with().id("person1").build();
        LinkOrObject person2 = Person.with().id("person2").build();
        List<LinkOrObject> items = List.of(person1, person2);
        LinkOrObject current = person1;
        LinkOrObject first = person1;
        LinkOrObject last = person2;

        var collection = Collection.with()
            .context(context)
            .id(id)
            .name(name)
            .nameMap(nameMap)
            .summary(summary)
            .summaryMap(summaryMap)
            .content(content)
            .contentMap(contentMap)
            .mediaType(mediaType)
            .url(url)
            .attributedTo(attributedTo)
            .duration(duration)
            .startTime(startTime)
            .endTime(endTime)
            .published(published)
            .updated(updated)
            .attachment(attachment)
            .audience(audience)
            .to(to)
            .bcc(bcc)
            .bto(bto)
            .cc(cc)
            .generator(generator)
            .icon(icon)
            .image(image)
            .inReplyTo(inReplyTo)
            .location(location)
            .preview(preview)
            .replies(replies)
            .tag(tag)
            .totalItems(totalItems)
            .items(items)
            .current(current)
            .first(first)
            .last(last)
            .build();

        assertThat(collection.type()).isEqualTo(ActivityStreamObjectType.Collection);
        assertThat(collection.totalItems()).isEqualTo(2);
        assertThat(collection.items()).hasSize(2);
        assertThat(collection.current()).isEqualTo(person1);
        assertThat(collection.first()).isEqualTo(person1);
        assertThat(collection.last()).isEqualTo(person2);
    }

}
