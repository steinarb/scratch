package no.priv.bang.ratatoskr.services.activitypub;
/*
 * Copyright 2026 Steinar Bang
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class ParseTest {

    static ObjectMapper mapper = JsonMapper.builder()
    .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
    .build();

    @Test
    void testMastodonToot() throws Exception {
        var create = mapper.readValue(mastodonExample("mastodon-toot-01.json"), Create.class);
        switch(create.actor()) {
        case Link link -> assertThat(link.href()).isEqualTo("https://mastodon.example");
        default -> fail("Did not get the expected type for move.actor");
    }
        switch(object) {
            case Create create -> {
                switch(create.object()) {
                    case Note note -> assertThat(note.summary()).isEqualTo("Optional Content Warning");
                    default -> fail("Did not get the expected type for move.object");
                }
            }
            default -> fail("Did not get the expected type when parsing");
        }
    }

    private InputStream mastodonExample(String classpathResource) {
        return this.getClass().getResourceAsStream("/json/mastodon-examples/" + classpathResource);
    }

}
