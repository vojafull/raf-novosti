package org.example.demo.resources;

import org.example.demo.entities.User;
import org.example.demo.filters.AuthFilter;
import org.example.demo.requests.CreateUserRequest;
import org.example.demo.requests.UpdateUserRequest;
import org.example.demo.services.UserService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.*;
import java.util.Map;

@Path("/users")
public class UserResource {

    @Inject
    private UserService userService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("page") @DefaultValue("1") int page,
                           @QueryParam("pageSize") @DefaultValue("10") int pageSize,
                           @Context ContainerRequestContext ctx) {

        User currentUser = (User) ctx.getProperty(AuthFilter.USER_PROPERTY);

        Map<String, Object> result = userService.getAllUsers(page, pageSize, currentUser);

        if (result.containsKey("error"))
            return Response.status((int) result.get("status")).entity(result).build();

        return Response.ok(result).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(CreateUserRequest request, @Context ContainerRequestContext ctx) {

        User currentUser = (User) ctx.getProperty(AuthFilter.USER_PROPERTY);

        Map<String, Object> result = userService.createUser(request, currentUser);

        if (result.containsKey("error"))
            return Response.status((int) result.get("status")).entity(result).build();

        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Integer id,
                           UpdateUserRequest request,
                           @Context ContainerRequestContext ctx) {

        User currentUser = (User) ctx.getProperty(AuthFilter.USER_PROPERTY);

        Map<String, Object> result = userService.updateUser(id, request, currentUser);

        if (result.containsKey("error"))
            return Response.status((int) result.get("status")).entity(result).build();

        return Response.ok(result).build();
    }

    @PUT
    @Path("/{id}/toggle-status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response toggleStatus(@PathParam("id") Integer id, @Context ContainerRequestContext ctx) {

        User currentUser = (User) ctx.getProperty(AuthFilter.USER_PROPERTY);

        Map<String, Object> result = userService.toggleUserStatus(id, currentUser);

        if (result.containsKey("error"))
            return Response.status((int) result.get("status")).entity(result).build();

        return Response.ok(result).build();
    }
}