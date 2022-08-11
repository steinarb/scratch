package no.priv.bang.servlet.jersey.test.webapiresources;


import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import no.priv.bang.servlet.jersey.test.HelloService;

@Path("/hi")
public class HiResource {

    @Inject
    HelloService service;

    @GET
    @Produces("text/plain")
    public String getHello() {
        return service.hello();
    }

}
