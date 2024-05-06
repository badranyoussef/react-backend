package rest.routes;

import io.javalin.security.RouteRole;

public enum Role implements RouteRole {
    ANYONE, STUDENT, INSTRUCTOR, ADMIN
}
