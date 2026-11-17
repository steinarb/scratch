package no.priv.bang.handlereg.web.api.resources;
/*
 * Copyright 2019-2026 Steinar Bang
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

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.priv.bang.handlereg.services.HandleregService;
import no.priv.bang.handlereg.services.NyHandling;
import no.priv.bang.handlereg.services.Oversikt;
import no.priv.bang.handlereg.services.Transaction;


@Path("")
@Produces(MediaType.APPLICATION_JSON)
@RequiresUser
@RequiresRoles("handleregbruker")
public class HandlingResource {
    private Logger logger = LoggerFactory.getLogger(HandlingResource.class);

    @Inject
    HandleregService handlereg;

    @GET
    @Path("/handlinger/{accountid}")
    public List<Transaction> getHandlinger(
        @PathParam("accountid") int accountId,
        @DefaultValue("1") @QueryParam("pagenumber") int pageNumber,
        @DefaultValue("10") @QueryParam("pagesize") int pageSize)
    {
        try {
            return handlereg.findTransactions(accountId, pageNumber, pageSize);
        } catch (Exception e) {
            String message = String.format("Failed to get transactions for account %d", accountId);
            logger.error(message, e);
            throw new InternalServerErrorException(message + ", see the log for details");
        }
    }

    @POST
    @Path("/nyhandling")
    @Consumes(MediaType.APPLICATION_JSON)
    public Oversikt nyhandling(NyHandling handling) {
        try {
            return handlereg.registrerHandling(handling);
        } catch (Exception e) {
            String message = "Failed to add transaction";
            logger.error(message, e);
            throw new InternalServerErrorException(message + ", see the log for details");
        }
    }
}
