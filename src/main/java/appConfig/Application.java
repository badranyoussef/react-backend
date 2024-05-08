package appConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controller.AuthController;
import dtos.UserDTO;
import exceptions.APIException;
import exceptions.DatabaseException;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.HttpStatus;
import util.AppLogger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Application {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String timestamp = dateFormat.format(new Date());

    private static Application instance;
    private Javalin app;
    private static final Logger logger = AppLogger.getLogger();
    private ObjectMapper om = new ObjectMapper();


    //1.1 creating java-project using javalin framework
    // Initialize Javalin app
    private Application() {
        app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
            config.routing.contextPath = "/api";

            //tillad
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost();
                });
            });
        });
    }

    public static Application getInstance() {
        if (instance == null) {
            instance = new Application();
        }
        return instance;
    }

    public Application startServer(int portNumber) {
        app.start(portNumber);
        return instance;
    }

    public Application setRoute(EndpointGroup route) {
        app.routes(route);
        return instance;
    }

    public Application setExceptionHandlers() {

        //2 error handling. I'm implementing global error handling. Controller and DAO throws the exceptions
        app.exception(APIException.class, (e, ctx) -> {
            logger.warning(
                    "HTTP: " + e.getStatusCode() + " Time: " + e.getTimeStamp() + " Method: " + ctx.method() + " Path: " + ctx.path() + " Client IP: " + ctx.ip() + "\n"
            );
            ctx.status(e.getStatusCode());
            ctx.json(Map.of(
                    "status", e.getStatusCode(),
                    "api error", e.getMessage(),
                    "timestamp", e.getTimeStamp().toString()
            ));
        });

        app.exception(DatabaseException.class, (e, ctx) -> {
            logger.warning(
                    "HTTP: " + e.getStatusCode() + " Time: " + e.getTimeStamp() + " Method: " + ctx.method() + " Path: " + ctx.path() + " Client IP: " + ctx.ip() + "\n"
            );
            ctx.status(e.getStatusCode());
            ctx.json(Map.of(
                    "status", e.getStatusCode(),
                    "database error", e.getMessage(),
                    "timestamp", e.getTimeStamp().toString()
            ));
        });

//        app.exception(Exception.class, (e, ctx) -> {
//            ObjectNode node = om.createObjectNode().put("errorMessage", e.getMessage());
//            ctx.status(500).json(node);
//        });
//
//        app.error(404, ctx -> {
//            ctx.status(404).result("Not Found");
//        });
//
//        app.exception(IllegalStateException.class, (e, ctx) -> {
//            ctx.status(400).result("Bad Request: " + e.getMessage());
//        });
        return instance;
    }

    public Application checkSecurityRoles() {
        // Check roles on the user (ctx.attribute("username") and compare with permittedRoles using securityController.authorize()
        app.updateConfig(config -> {

            config.accessManager((handler, ctx, permittedRoles) -> {
                // permitted roles are defined in the last arg to routes: get("/", ctx -> ctx.result("Hello World"), Role.ANYONE);

                Set<String> allowedRoles = permittedRoles.stream().map(role -> role.toString().toUpperCase()).collect(Collectors.toSet());
                if (allowedRoles.contains("ANYONE") || ctx.method().toString().equals("OPTIONS")) {
                    // Allow requests from anyone and OPTIONS requests (preflight in CORS)
                    handler.handle(ctx);
                    return;
                }

                UserDTO user = ctx.attribute("user");
                System.out.println("USER IN CHECK_SEC_ROLES: " + user);
                if (user == null)
                    ctx.status(HttpStatus.FORBIDDEN)
                            .json(om.createObjectNode()
                                    .put("msg", "Not authorized. No username were added from the token"));

                if (AuthController.authorize(user, allowedRoles))
                    handler.handle(ctx);
                else
                    throw new APIException(HttpStatus.FORBIDDEN.getCode(), "Unauthorized with roles: " + allowedRoles,timestamp );
            });
        });
        return instance;
    }

//    public Application grantAccess() {
//    }


    public void stopServer() {
        app.stop();
    }

}
