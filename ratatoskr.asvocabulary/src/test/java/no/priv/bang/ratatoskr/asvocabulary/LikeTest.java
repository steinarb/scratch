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

class LikeTest {

    @Test
    void testCreate() {
        Object context = null;
        String id = "http://activities.example.com/1";
        String name = null;
        Map<String, String> nameMap = Collections.emptyMap();
        String summary = "John liked Sally's note";
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
        LinkOrObject to = Link.with().href("https://sally.example.org").build();
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
        LinkOrObject actor = Link.with().href("https://john.example.com").build();
        LinkOrObject object = Link.with().href("http://notes.example.com/1").build();

        var like = Like.with()
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
            .actor(actor)
            .object(object)
            .build();

        assertThat(like.type()).isEqualTo(ActivityStreamObjectType.Like);
        assertThat(like.name()).isEqualTo(name);
        assertThat(like.content()).isEqualTo(content);
        assertThat(like.to()).isEqualTo(to);
        assertThat(like.actor()).isEqualTo(actor);
        assertThat(like.object()).isEqualTo(object);
    }

    @Test
    void testCopyAndModify() {
        Object context = null;
        String id = "http://activities.example.com/1";
        String name = null;
        Map<String, String> nameMap = Collections.emptyMap();
        String summary = "John liked Sally's note";
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
        LinkOrObject audience = Group.with().name("Project XYZ Working Group").build();
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

        var like = Like.with()
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

        LinkOrObject actor = Link.with().href("https://john.example.com").build();
        LinkOrObject object = Link.with().href("http://notes.example.com/1").build();
        var likeCopy = Like.with(like).actor(actor).object(object).build();

        assertThat(likeCopy.type()).isEqualTo(ActivityStreamObjectType.Like);
        assertThat(likeCopy.name()).isEqualTo(name);
        assertThat(likeCopy.content()).isEqualTo(content);
        assertThat(likeCopy.to()).isEqualTo(to);
        assertThat(likeCopy.actor()).isEqualTo(actor);
        assertThat(likeCopy.object()).isEqualTo(object);
    }

}
