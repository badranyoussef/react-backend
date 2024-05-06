package rest.routes;

import controller.AuthController;
import dao.AuthDAO;
import dao.UserDAO;
import exceptions.APIException;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;
import persistence.config.HibernateConfig;

import static io.javalin.apibuilder.ApiBuilder.*;

public class AuthenticationRoutes {
    private static UserDAO userDAO;
    private static AuthDAO authDAO;

    public AuthenticationRoutes(EntityManagerFactory emf) {
        userDAO = UserDAO.getInstance(emf);
        authDAO = AuthDAO.getInstance(emf);
    }

    public EndpointGroup authBefore() {
        return () -> {
            path("/", () -> before(AuthController.authenticate()));
        };
    }

    public EndpointGroup getAuthRoutes() {
        return () -> {
            path("/auth", () -> {
                //Login route
                post("/login", ctx -> {
                            try {
                                AuthController.login(authDAO).handle(ctx);
                            } catch (APIException e) {
                                ctx.status(e.getStatusCode()).result(e.getMessage());
                            }
                        }
                        , Role.ANYONE
                );
                //Logout route
                post("/logout", ctx -> {
                            try {
                                AuthController.logout(authDAO).handle(ctx);
                            } catch (APIException e) {
                                ctx.status(e.getStatusCode()).result(e.getMessage());
                            }
                        }
                        , Role.ANYONE
                );
                //Register new user route
                post("/register", ctx -> {
                            try {
                                AuthController.register(userDAO).handle(ctx);
                            } catch (APIException e) {
                                ctx.status(e.getStatusCode()).result(e.getMessage());
                            }
                        }
                        , Role.ANYONE
                );
                post("/reset-password", ctx -> {
                    try {
                        AuthController.resetPassword(authDAO).handle(ctx);
                    } catch (Exception e) {

                    }
                });
            });
        };
    }
}
