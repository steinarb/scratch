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
package no.priv.bang.ratatoskr.web.as.resources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import org.junit.jupiter.api.Test;

import no.priv.bang.ratatoskr.asvocabulary.Group;
import no.priv.bang.ratatoskr.asvocabulary.Like;
import no.priv.bang.ratatoskr.asvocabulary.Link;
import no.priv.bang.ratatoskr.services.RatatoskrService;

class LikedResourceTest {

    @Test
    void testGetLiked() {
        var group = Group.with().name("Project XYZ Working Group").build();
        var like = Like.with()
            .summary("John liked Sally's note")
            .audience(group)
            .actor(Link.with().href("http://localhost:8181/ratatoskr/as/actor/johnd").build())
            .target(Link.with().href("https://sally.example.com/posts/124").build())
            .build();
        var ratatoskr = mock(RatatoskrService.class);
        when(ratatoskr.findLikedWithUsername(anyString())).thenReturn(List.of(like));

        var resource = new LikedResource();
        resource.ratatoskr = ratatoskr;
        var uriInfo = mock(UriInfo.class);
        var baseUri = URI.create("http://localhost:8181/ratatoskr/as");
        when(uriInfo.getBaseUriBuilder())
            .thenReturn(JerseyUriBuilder.fromUri(baseUri));
        var username = "johnd";
        var likes = resource.getLiked(uriInfo, username);
        assertThat(likes.id()).isEqualTo("http://localhost:8181/ratatoskr/as/liked/johnd");
        assertThat(likes.totalItems()).isEqualTo(1);
        assertThat(likes.items()).hasSize(1);
        assertThat(likes.current()).isEqualTo(like);
        assertThat(likes.first()).isEqualTo(like);
        assertThat(likes.last()).isEqualTo(like);
    }

}
