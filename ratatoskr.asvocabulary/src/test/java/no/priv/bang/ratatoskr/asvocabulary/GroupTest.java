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

class GroupTest {

    @Test
    void testCreate() {
        Object context = null;
        String id = "https://example.com/groups/1";
        String name = "Project XYZ Working Group";
        Map<String, String> nameMap = Collections.emptyMap();
        String summary = null;
        Map<String, String> summaryMap = Collections.emptyMap();
        String content = null;
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

        var article = Group.with()
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
            .build();

        assertThat(article.type()).isEqualTo(ActivityStreamObjectType.Group);
        assertThat(article.name()).isEqualTo(name);
    }

    @Test
    void testCopyAndModify() {
        Object context = null;
        String id = "https://example.com/groups/1";
        String name = "Project XYZ Working Group";
        Map<String, String> nameMap = Collections.emptyMap();
        String summary = null;
        Map<String, String> summaryMap = Collections.emptyMap();
        String content = null;
        Map<String, String> contentMap = Collections.emptyMap();
        String mediaType = "";
        List<Link> url = Collections.emptyList();
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

        var article = Group.with()
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
            .attributedTo(null)
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
            .build();

        var attributedTo = Link.with().href("https://sally.example.com").build();
        var articleCopy = Group.with(article).attributedTo(attributedTo).build();

        assertThat(articleCopy.type()).isEqualTo(ActivityStreamObjectType.Group);
        assertThat(articleCopy.name()).isEqualTo(name);
        assertThat(articleCopy.content()).isEqualTo(content);
        assertThat(articleCopy.attributedTo()).isEqualTo(attributedTo);
    }

}
