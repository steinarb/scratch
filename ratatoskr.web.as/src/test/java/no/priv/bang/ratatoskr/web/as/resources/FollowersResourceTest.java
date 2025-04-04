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

import no.priv.bang.ratatoskr.asvocabulary.Person;
import no.priv.bang.ratatoskr.services.RatatoskrService;

class FollowersResourceTest {

    @Test
    void testGetFollowers() {
        var person1 = Person.with()
            .id("http://localhost:8181/ratatoskr/as/actor/sally")
            .preferredUsername("sally")
            .name("Sally Shaw")
            .summary("Just Shaw them")
            .inbox("http://localhost:8181/ratatoskr/as/commoninbox")
            .following("http://localhost:8181/ratatoskr/as/following/sally")
            .followers("http://localhost:8181/ratatoskr/as/followers/sally")
            .liked("http://localhost:8181/ratatoskr/as/liked/sally")
            .icon("http://localhost:8181/ratatoskr/image/165987aklre4")
            .build();
        var person2 = Person.with()
            .id("http://localhost:8181/ratatoskr/as/actor/george")
            .preferredUsername("george")
            .name("George Grant")
            .summary("Can I get a grant")
            .inbox("http://localhost:8181/ratatoskr/as/commoninbox")
            .following("http://localhost:8181/ratatoskr/as/following/george")
            .followers("http://localhost:8181/ratatoskr/as/followers/george")
            .liked("http://localhost:8181/ratatoskr/as/liked/george")
            .icon("http://localhost:8181/ratatoskr/image/165987aklre4")
            .build();
        var ratatoskr = mock(RatatoskrService.class);
        when(ratatoskr.findFollowersWithUsername(anyString())).thenReturn(List.of(person1, person2));

        var resource = new FollowersResource();
        resource.ratatoskr = ratatoskr;
        var uriInfo = mock(UriInfo.class);
        var baseUri = URI.create("http://localhost:8181/ratatoskr/as");
        when(uriInfo.getBaseUriBuilder())
            .thenReturn(JerseyUriBuilder.fromUri(baseUri));
        var username = "johnd";
        var followers = resource.getFollowers(uriInfo, username);
        assertThat(followers.id()).isEqualTo("http://localhost:8181/ratatoskr/as/followers/johnd");
        assertThat(followers.totalItems()).isEqualTo(2);
        assertThat(followers.items()).hasSize(2);
        assertThat(followers.current()).isEqualTo(person1);
        assertThat(followers.first()).isEqualTo(person1);
        assertThat(followers.last()).isEqualTo(person2);
    }

}
