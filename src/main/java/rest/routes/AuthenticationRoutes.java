package rest.routes;

import controllers.AuthController;
import daos.AuthDAO;
import daos.UserDAO;
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
                //todo: not done
                //request with email
                //create new temporary token and route from that token
                //response with that temporary token
                //let user post new password, unless token is expired
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
