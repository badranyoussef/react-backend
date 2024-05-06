package rest.routes;

import controller.UserController;
import dao.UserDAO;
import exceptions.APIException;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class UserRoutes {

    private UserDAO userDAO;

    public UserRoutes(EntityManagerFactory emf) {
        userDAO = UserDAO.getInstance(emf);

    }

    public EndpointGroup getUserRoutes() {
        return () -> path("/users", () ->
        {

            // Get all users.
            get("/", ctx ->
                    {
                        try {
                            UserController.getAllUsers(userDAO).handle(ctx);
                        } catch (APIException e) {
                            ctx.status(e.getStatusCode()).result(e.getMessage());
                        }
                    }
                    , Role.ANYONE
            );

            // Get a single user.
            get("/{id}", ctx ->
                    {
                        try {
                            UserController.getUserById(userDAO).handle(ctx);
                        } catch (APIException e) {
                            ctx.status(e.getStatusCode()).result(e.getMessage());
                        }
                    }
                    , Role.ANYONE
            );

            // Create a new user.
            post("/", ctx ->
                    {
                        try {
                            UserController.createUser(userDAO).handle(ctx);
                        } catch (APIException e) {
                            ctx.status(e.getStatusCode()).result(e.getMessage());
                        }
                    }
                    , Role.ANYONE
            );

            // update user
            put("/{id}", ctx ->
                {
                    try {
                        UserController.updateUser(userDAO).handle(ctx);
                    } catch (APIException e) {
                        ctx.status(e.getStatusCode()).result(e.getMessage());
                    }
                }
                , Role.ANYONE
            );

        });
    }
}
