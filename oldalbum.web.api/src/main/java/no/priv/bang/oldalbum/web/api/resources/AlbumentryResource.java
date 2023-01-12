/*
 * Copyright 2020-2023 Steinar Bang
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
package no.priv.bang.oldalbum.web.api.resources;

import static javax.ws.rs.core.MediaType.*;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.oldalbum.services.bean.BatchAddPicturesRequest;

@Path("")
@Produces(APPLICATION_JSON)
public class AlbumentryResource {

    @Inject
    public OldAlbumService oldalbum;

    @Path("modifyalbum")
    @POST
    @Consumes(APPLICATION_JSON)
    public List<AlbumEntry> modifyalbum(AlbumEntry modifiedAlbum) {
        return oldalbum.updateEntry(modifiedAlbum);
    }

    @Path("addalbum")
    @POST
    @Consumes(APPLICATION_JSON)
    public List<AlbumEntry> addalbum(AlbumEntry albumEntryToAdd) {
        return oldalbum.addEntry(albumEntryToAdd);
    }

    @Path("modifypicture")
    @POST
    @Consumes(APPLICATION_JSON)
    public List<AlbumEntry> modifypicture(AlbumEntry modifiedPicture) {
        return oldalbum.updateEntry(modifiedPicture);
    }

    @Path("addpicture")
    @POST
    @Consumes(APPLICATION_JSON)
    public List<AlbumEntry> addpicture(AlbumEntry pictureToAdd) {
        return oldalbum.addEntry(pictureToAdd);
    }

    @Path("batchaddpictures")
    @POST
    @Consumes(APPLICATION_JSON)
    public List<AlbumEntry> batchAddPictures(BatchAddPicturesRequest request) {
        return oldalbum.batchAddPictures(request);
    }

    @Path("sortalbumbydate")
    @POST
    @Consumes(APPLICATION_JSON)
    public List<AlbumEntry> sortAlbumByDate(AlbumEntry request) {
        return oldalbum.sortByDate(request.getId());
    }

    @Path("deleteentry")
    @POST
    @Consumes(APPLICATION_JSON)
    public List<AlbumEntry> deleteEntry(AlbumEntry entryToDelete) {
        return oldalbum.deleteEntry(entryToDelete);
    }

    @Path("movealbumentryup")
    @POST
    @Consumes(APPLICATION_JSON)
    public List<AlbumEntry> moveEntryUp(AlbumEntry entryToMove) {
        return oldalbum.moveEntryUp(entryToMove);
    }

    @Path("movealbumentrydown")
    @POST
    @Consumes(APPLICATION_JSON)
    public List<AlbumEntry> moveEntryDown(AlbumEntry entryToMove) {
        return oldalbum.moveEntryDown(entryToMove);
    }

}
