package no.priv.bang.servlet.jersey.test.resources;


import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import no.priv.bang.servlet.jersey.test.HelloService;

@Path("/hello")
public class HelloResource {

    @Inject
    HelloService service;

    @GET
    @Produces("text/plain")
    public String getHello() {
        return service.hello();
    }

}
