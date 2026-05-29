package org.example.demo.resources;

import org.example.demo.requests.CreateCategoryRequest;
import org.example.demo.services.CategoryService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.*;
import java.util.Map;

@Path("/categories")
public class CategoryResource {

    @Inject
    private CategoryService categoryService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("page") @DefaultValue("1") int page,
                           @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        return Response.ok(categoryService.getAll(page, pageSize)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") Integer id) {

        Map<String, Object> result = categoryService.findById(id);

        if (result.containsKey("error"))
            return Response.status((int) result.get("status")).entity(result).build();

        return Response.ok(result).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(CreateCategoryRequest request) {

        Map<String, Object> result = categoryService.create(request);

        if (result.containsKey("error"))
            return Response.status((int) result.get("status")).entity(result).build();

        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Integer id,
                           CreateCategoryRequest request,
                           @Context ContainerRequestContext ctx) {

        Map<String, Object> result = categoryService.update(id, request);

        if (result.containsKey("error"))
            return Response.status((int) result.get("status")).entity(result).build();

        return Response.ok(result).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Integer id,
                           @Context ContainerRequestContext ctx) {

        Map<String, Object> result = categoryService.delete(id);

        if (result.containsKey("error"))
            return Response.status((int) result.get("status")).entity(result).build();

        return Response.ok(result).build();
    }
}