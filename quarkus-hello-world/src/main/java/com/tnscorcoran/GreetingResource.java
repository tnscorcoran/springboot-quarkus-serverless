package com.tnscorcoran;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/greeting")
public class GreetingResource {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Greeting greeting(@PathParam String name) {
        return new Greeting(name);
    }
}