package org.example.demo.resources;

import org.example.demo.requests.LoginRequest;
import org.example.demo.services.UserService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/auth")
public class AuthResource {

    @Inject
    private UserService userService;

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest request) {

        Map<String, Object> result = userService.login(request.getEmail(), request.getPassword());

        if (result.containsKey("error"))
            return Response.status((int) result.get("status")).entity(result).build();

        return Response.ok(result).build();
    }
}