package org.example.demo.resources;

import org.example.demo.entities.User;
import org.example.demo.filters.AuthFilter;
import org.example.demo.requests.CreateCommentRequest;
import org.example.demo.requests.CreateNewsRequest;
import org.example.demo.requests.ReactionRequest;
import org.example.demo.services.NewsService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.*;
import java.util.Map;

@Path("/news")
public class NewsResource {

    @Inject
    private NewsService newsService;


    // JAVNE RUTE

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("page") @DefaultValue("1") int page,
                           @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        return Response.ok(newsService.getAll(page, pageSize)).build();
    }

    @GET
    @Path("/latest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLatest() {
        return Response.ok(newsService.getLatest(10)).build();
    }

    @GET
    @Path("/most-visited")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMostVisited() {
        return Response.ok(newsService.getMostVisited(10, 30)).build();
    }

    @GET
    @Path("/most-reacted")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMostReacted() {
        return Response.ok(newsService.getMostReacted(3)).build();
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@QueryParam("q") String query,
                           @QueryParam("page") @DefaultValue("1") int page,
                           @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        return Response.ok(newsService.search(query, page, pageSize)).build();
    }

    @GET
    @Path("/tag/{tagId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByTag(@PathParam("tagId") Integer tagId,
                             @QueryParam("page") @DefaultValue("1") int page,
                             @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        return Response.ok(newsService.getByTag(tagId, page, pageSize)).build();
    }

    @GET
    @Path("/category/{categoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByCategory(@PathParam("categoryId") Integer categoryId,
                                  @QueryParam("page") @DefaultValue("1") int page,
                                  @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        return Response.ok(newsService.getByCategory(categoryId, page, pageSize)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") Integer id,
                            @CookieParam("sessionId") String sessionId) {

        Map<String, Object> result = newsService.getById(id, sessionId);

        if (result.containsKey("error"))
            return Response.status((int) result.get("status")).entity(result).build();

        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}/comments")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComments(@PathParam("id") Integer id,
                                @QueryParam("page") @DefaultValue("1") int page,
                                @QueryParam("pageSize") @DefaultValue("10") int pageSize,
                                @CookieParam("sessionId") String sessionId) {
        return Response.ok(newsService.getComments(id, page, pageSize, sessionId)).build();
    }

    @POST
    @Path("/{id}/comments")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addComment(@PathParam("id") Integer id,
                               CreateCommentRequest request) {

        Map<String, Object> result = newsService.addComment(id, request);

        if (result.containsKey("error"))
            return Response.status((int) result.get("status")).entity(result).build();

        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @POST
    @Path("/{id}/reaction")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response reactToNews(@PathParam("id") Integer id,
                                ReactionRequest request,
                                @CookieParam("sessionId") String sessionId) {

        Map<String, Object> result = newsService.reactToNews(id, sessionId, request);

        if (result.containsKey("error"))
            return Response.status((int) result.get("status")).entity(result).build();

        return Response.ok(result).build();
    }

    @POST
    @Path("/comments/{commentId}/reaction")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response reactToComment(@PathParam("commentId") Integer commentId,
                                   ReactionRequest request,
                                   @CookieParam("sessionId") String sessionId) {

        Map<String, Object> result = newsService.reactToComment(commentId, sessionId, request);

        if (result.containsKey("error"))
            return Response.status((int) result.get("status")).entity(result).build();

        return Response.ok(result).build();
    }






    // ---------------------------------------------------------------------------
    // CMS RUTE
    // ---------------------------------------------------------------------------


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(CreateNewsRequest request, @Context ContainerRequestContext ctx) {

        User currentUser = (User) ctx.getProperty(AuthFilter.USER_PROPERTY);

        Map<String, Object> result = newsService.create(request, currentUser);

        if (result.containsKey("error"))
            return Response.status((int) result.get("status")).entity(result).build();

        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Integer id,
                           CreateNewsRequest request,
                           @Context ContainerRequestContext ctx) {

        User currentUser = (User) ctx.getProperty(AuthFilter.USER_PROPERTY);
        System.out.println("CURRENT USER IN RESOURCE: " + currentUser);
        Map<String, Object> result = newsService.update(id, request, currentUser);

        if (result.containsKey("error"))
            return Response.status((int) result.get("status")).entity(result).build();

        return Response.ok(result).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Integer id, @Context ContainerRequestContext ctx) {

        User currentUser = (User) ctx.getProperty(AuthFilter.USER_PROPERTY);

        Map<String, Object> result = newsService.delete(id, currentUser);

        if (result.containsKey("error"))
            return Response.status((int) result.get("status")).entity(result).build();

        return Response.ok(result).build();
    }
}