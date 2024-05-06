package app;

import appConfig.Application;
import jakarta.persistence.EntityManagerFactory;
import persistence.config.HibernateConfig;
import rest.routes.AuthenticationRoutes;
import rest.routes.CarRoute;
import rest.routes.UserRoutes;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(false);

        CarRoute routes = new CarRoute();
        UserRoutes userRoutes = new UserRoutes(emf);
        AuthenticationRoutes authRoutes = new AuthenticationRoutes(emf);

        //1.1 creating java-project using javalin framework
        Application carShopApp = Application.getInstance();
        carShopApp.startServer(7075);
        carShopApp.setExceptionHandlers();
        carShopApp.setRoute(routes.getCarRoutes());
        carShopApp.setRoute(userRoutes.getUserRoutes());
        carShopApp.setRoute(authRoutes.getAuthRoutes());
        carShopApp.setRoute(authRoutes.authBefore());
        carShopApp.checkSecurityRoles();
    }
}