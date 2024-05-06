package rest.routes;

import controller.HealthProductController;
import controller.StorageController;
import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.get;

public class Route {

    HealthProductController healthProductController = new HealthProductController();
    StorageController storageController = new StorageController();

    //1.4 & 1.4.2 develop rest API
    public EndpointGroup getHealthRoutes() {
        return () -> path("/healthproducts", () -> {

//        frem for at implementere exceptions ved hver endpoint brnytter jeg globale exceptions som sætte op i javalin konfigurationen
//
//            post("/initiate", ctx ->
//            {
//                try {
//                    healthProductController.initiateProducts().handle(ctx);
//                } catch (APIException e) {
//                    ctx.json(Map.of(
//                            "status", e.getStatusCode(),
//                            "message", "API ERROR " + e.getMessage(),
//                            "timestamp", e.getTimeStamp().toString()
//                    ));
//                }
//            });

            // initiate all product
            post("/initiate", ctx -> healthProductController.initiateProducts().handle(ctx));

            // get all products
            get("/", ctx -> healthProductController.getAll().handle(ctx));

            // get a product by id
            get("/{id}", ctx -> healthProductController.getById().handle(ctx));

            // create a product
            post("/", ctx -> healthProductController.create().handle(ctx));

            // update product
            put("/{id}", ctx -> healthProductController.update().handle(ctx));

            // delete product by id
            delete("/{id}", ctx -> healthProductController.delete().handle(ctx));
        });
    }

    public EndpointGroup getStorageRoutes() {
        return () -> path("/storages", () -> {

            // initiate all product
            post("/initiate", ctx -> storageController.initiateProducts().handle(ctx));

            // get all products
            get("/", ctx -> storageController.getAll().handle(ctx));

            // get a product by id
            get("/{id}", ctx -> storageController.getById().handle(ctx));

            // get products by storage shelf
            get("/storage_shelf/{id}", ctx -> storageController.getProductsByStorageShelf().handle(ctx));

            // create a product
            post("/", ctx -> storageController.create().handle(ctx));

            // update product
            put("/{id}", ctx -> storageController.update().handle(ctx));

            // add product to storage
            put("/{id_storage}/{id_product}", ctx -> storageController.addProductToStorage().handle(ctx));

            // delete product by id
            delete("/{id}", ctx -> storageController.delete().handle(ctx));
        });
    }
}
