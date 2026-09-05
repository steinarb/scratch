/*
 * Copyright 2025-2026 Steinar Bang
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

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import no.priv.bang.ratatoskr.services.RatatoskrService;
import no.priv.bang.ratatoskr.services.activitypub.PersonCollection;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class FollowingResource {

    @Inject
    public RatatoskrService ratatoskr;

    @GET
    @Path("following/{username}")
    public PersonCollection getFollowing(@Context UriInfo uriInfo, @PathParam("username") String username) {
        var following = List.copyOf(ratatoskr.findProfilesFollowedByUsername(username));
        return PersonCollection.with()
            .id(followingid(uriInfo, username))
            .totalItems(following.size())
            .orderedItems(following)
            .current(following.getFirst())
            .build();
    }

    private String followingid(UriInfo uriInfo, String username) {
        return uriInfo.getBaseUriBuilder().path("following").path(username).build().toString();
    }

}
