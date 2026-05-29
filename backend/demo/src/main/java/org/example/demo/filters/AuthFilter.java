package org.example.demo.filters;

import org.example.demo.entities.User;
import org.example.demo.services.UserService;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    @Inject
    private UserService userService;

    public static final String USER_PROPERTY = "authenticatedUser";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();
        System.out.println("METHOD: "+method+" PATH: " + path);
        if ("OPTIONS".equals(method)) {
            return;
        }

        if (isPublicPath(path,method)) {
            return;
        }

        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abort(requestContext, "Token nije prosledjen");
            return;
        }

        String token = authHeader.substring(7);

        User user = userService.getUserFromToken(token);
        if (user == null) {
            abort(requestContext, "Token nije validan ili je istekao");
            return;
        }

        if ("INACTIVE".equals(user.getStatus())) {
            abort(requestContext, "Vas nalog je deaktiviran");
            return;
        }


        System.out.println("USER: " + user);
        requestContext.setProperty(USER_PROPERTY, user);
    }

    private boolean isPublicPath(String path, String method) {
        return path.startsWith("public")
                || path.contains("auth/login")
                || ("GET".equals(method) && path.equals("news"))
                || path.contains("news/latest")
                || path.contains("news/most-visited")
                || path.contains("news/most-reacted")
                || path.contains("news/search")
                || path.contains("news/tag/")
                || path.contains("news/category/")
                || path.contains("reaction")
                || (path.startsWith("news/") && path.endsWith("/comments"))
                || ("GET".equals(method) && path.matches("news/\\d+"))
                || ("GET".equals(method) && path.matches("categories/"));
    }

    private void abort(ContainerRequestContext ctx, String message) {
        ctx.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"" + message + "\"}")
                        .type("application/json")
                        .build()
        );
    }
}
