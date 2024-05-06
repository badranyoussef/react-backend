package appConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import exceptions.APIException;
import util.AppLogger;
import exceptions.DatabaseException;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;

import java.util.Map;
import java.util.logging.Logger;

public class Application {

    private static Application instance;
    private Javalin app;
    private static final Logger logger = AppLogger.getLogger();
    private ObjectMapper om = new ObjectMapper();


    //1.1 creating java-project using javalin framework
    // Initialize Javalin app
    private Application() {
        app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
            config.routing.contextPath = "/healthstore/api";
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

        //2.2 error handling. I'm implementing global error handling. Controller and DAO throws the exceptions
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

        app.exception(Exception.class, (e, ctx) -> {
            ObjectNode node = om.createObjectNode().put("errorMessage", e.getMessage());
            ctx.status(500).json(node);
        });

        app.error(404, ctx -> {
            ctx.status(404).result("Not Found");
        });

        app.exception(IllegalStateException.class, (e, ctx) -> {
            ctx.status(400).result("Bad Request: " + e.getMessage());
        });
        return instance;
    }

    public void stopServer() {
        app.stop();
    }

}
