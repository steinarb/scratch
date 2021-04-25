package no.priv.bang.handlereg.web.api.resources;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import no.priv.bang.handlereg.services.Favoritt;
import no.priv.bang.handlereg.services.Favorittpar;
import no.priv.bang.handlereg.services.HandleregService;
import no.priv.bang.handlereg.services.NyFavoritt;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class FavorittResource {

    @Inject
    HandleregService handlereg;

    @GET
    @Path("favoritter")
    public List<Favoritt> getFavoritter(@QueryParam("username") String username) {
        return handlereg.finnFavoritter(username);
    }

    @POST
    @Path("favoritt/leggtil")
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Favoritt> leggTilFavoritt(NyFavoritt nyFavoritt) {
        return handlereg.leggTilFavoritt(nyFavoritt);
    }

    @POST
    @Path("favoritt/slett")
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Favoritt> slettFavoritt(Favoritt favorittSomSkalSlettes) {
        return handlereg.slettFavoritt(favorittSomSkalSlettes);
    }

    @POST
    @Path("favoritter/bytt")
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Favoritt> byttRekkefolge(Favorittpar favoritterSomSkalBytteRekkefolge) {
        return handlereg.byttRekkefolge(favoritterSomSkalBytteRekkefolge);
    }

}
