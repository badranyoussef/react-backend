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
        //carShopApp.grantAccessToHost("http://localhost:5173"); // <- Tillader alle adgang til serverens ressourcer... Dette blev brugt i forbindelse at få adgang fra frontend localhost til server
        carShopApp.grantAccessToHost("http://159.223.19.33:7074");
        carShopApp.setExceptionHandlers();
        carShopApp.setRoute(routes.getCarRoutes());
        carShopApp.setRoute(userRoutes.getUserRoutes());
        carShopApp.setRoute(authRoutes.getAuthRoutes());
        carShopApp.setRoute(authRoutes.authBefore()); // --> ved createUser er alt ok men denne skaber fejl 403 forbidden. user oprettes dog
        carShopApp.checkSecurityRoles();
    }
}