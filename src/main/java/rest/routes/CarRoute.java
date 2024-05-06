package rest.routes;

import controller.CarController;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.get;

public class CarRoute {

    CarController carController = new CarController();

    //1.4.2 rest API
    public EndpointGroup getCarRoutes() {
        return () -> path("/cars", () -> {

            // get all cars
            get("/", ctx -> carController.getAll().handle(ctx), Role.ADMIN);

            // get a car by id
            get("/{id}", ctx -> carController.getById().handle(ctx), Role.ANYONE);

            // get cars by seller
            get("/sellers/{id}", ctx -> carController.getCarsBySeller().handle(ctx), Role.ANYONE);

            // create a car
            post("/", ctx -> carController.create().handle(ctx), Role.ANYONE);

            // update car
            put("/{id}", ctx -> carController.update().handle(ctx), Role.ANYONE);

            // add car to seller
            put("/add_to_seller/{sellerid}/{carid}", ctx -> carController.addCarToSeller().handle(ctx), Role.ANYONE);

            // delete car by id
            delete("/{id}", ctx -> carController.delete().handle(ctx), Role.ANYONE);
        });
    }
}
