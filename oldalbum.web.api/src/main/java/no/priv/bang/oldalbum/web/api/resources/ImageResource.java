/*
 * Copyright 2020-2024 Steinar Bang
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

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.osgi.service.log.LogService;
import org.osgi.service.log.Logger;

import no.priv.bang.oldalbum.services.OldAlbumException;
import no.priv.bang.oldalbum.services.OldAlbumService;
import no.priv.bang.oldalbum.services.bean.AlbumEntry;
import no.priv.bang.oldalbum.services.bean.ImageMetadata;
import no.priv.bang.oldalbum.services.bean.ImageRequest;

@Path("image")
public class ImageResource {

    @Inject
    OldAlbumService oldalbum;

    private Logger logger;

    @Inject
    public void setLogservice(LogService logservice) {
        logger = logservice.getLogger(ImageResource.class);
    }

    @POST
    @Path("metadata")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public ImageMetadata getMetadata(ImageRequest imageRequest) {
        return oldalbum.readMetadata(imageRequest.url());
    }

    @GET
    @Path("download/{albumEntryId}")
    @Produces(APPLICATION_OCTET_STREAM)
    public Response downloadAlbumEntry(@PathParam("albumEntryId") int albumEntryId) {
        try {
            var entry = oldalbum.getAlbumEntry(albumEntryId).orElseThrow(() -> new OldAlbumException(String.format("Couldn't find album entry from id=%d", albumEntryId)));
            var lastModified = Optional.ofNullable(entry.lastModified()).orElse(new Date());
            var filename = findFilenameFromAlbumEntryPath(entry);
            var streamingOutput = oldalbum.downloadAlbumEntry(albumEntryId);
            return Response.ok(streamingOutput)
                .header("Content-Disposition", "attachment; filename=" + filename)
                .header("Last-Modified", lastModified)
                .build();
        } catch (OldAlbumException e) {
            logger.error("Failed to download album entry with id {}", albumEntryId, e);
            return Response.status(Status.NOT_FOUND)
                .entity("FILE NOT FOUND! See log for details!")
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build();
        }
    }

    @GET
    @Path("downloadselection/{albumId}")
    @Produces(APPLICATION_OCTET_STREAM)
    public Response downloadAlbumEntrySelection(@PathParam("albumId") int albumId, @QueryParam("id") List<Integer> selectedentryIds) {
        try {
            var album = oldalbum.getAlbumEntry(albumId).orElseThrow(() -> new OldAlbumException(String.format("Couldn't find album rom id=%d", albumId)));
            var lastModified = new Date();
            var filename = findFilenameFromAlbumEntryPath(album);
            var streamingOutput = oldalbum.downloadAlbumEntrySelection(selectedentryIds);
            return Response.ok(streamingOutput)
                .header("Content-Disposition", "attachment; filename=" + filename)
                .header("Last-Modified", lastModified)
                .build();
        } catch (OldAlbumException e) {
            logger.error("Failed to download selection of entries from album with id {}", albumId, e);
            return Response.status(Status.NOT_FOUND)
                .entity("FILE NOT FOUND! See log for details!")
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build();
        }
    }

    String findFilenameFromAlbumEntryPath(AlbumEntry entry) {
        if (entry.album()) {
            return findFileNamePartOfUrl(entry.path()) + ".zip";
        }

        return findFileNamePartOfUrl(entry.imageUrl());
    }

    String findFileNamePartOfUrl(String imageUrl) {
        var urlComponents = imageUrl.split("/");
        return urlComponents[urlComponents.length - 1];
    }

}
