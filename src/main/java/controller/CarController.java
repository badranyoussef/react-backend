package controller;

import dao.CarDAO;
import dtos.CarDTO;
import exceptions.APIException;
import io.javalin.http.Handler;
import persistence.model.Car;

import javax.security.auth.callback.CallbackHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class CarController implements IController {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static String timestamp = dateFormat.format(new Date());
    private CarDAO dao = new CarDAO();

    @Override
    public Handler getAll() {
        return ctx -> {
            Set<CarDTO> carDTOS = dao.getAll();
            if (carDTOS.isEmpty()) {
                throw new APIException(200, "No products available", timestamp);
            } else {
                ctx.json(carDTOS);
            }
        };
    }

    @Override
    public Handler getById() {
        return ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            CarDTO dto = dao.getById(id);
            if (dto == null) {
                throw new APIException(400, "The product you are looking for is not available", timestamp);
            } else {
                ctx.json(dto);
            }
        };
    }

    @Override
    public Handler create() {
        return ctx -> {
            Car product = ctx.bodyAsClass(Car.class);
            CarDTO carDTO = dao.create(product);
            if (carDTO != null) {
                ctx.status(200).json(carDTO);
            } else {
                throw new APIException(500, "Failed to create the product", timestamp);
            }
        };
    }

    @Override
    public Handler update() {
        return ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            CarDTO carDTO = ctx.bodyAsClass(CarDTO.class);
            carDTO.setId(id);
            CarDTO updatedCar = dao.update(carDTO);
            if (updatedCar != null) {
                ctx.json(updatedCar);
            } else {
                throw new APIException(400, "No data found.", "" + timestamp);
            }
        };
    }

    @Override
    public Handler delete() {
        return ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            CarDTO carDTO = dao.delete(id);
            if (carDTO == null) {
                ctx.status(400).result("Product not found");
            } else {
                ctx.status(200).json(carDTO);
            }
        };
    }

    public Handler getCarsBySeller() {
        return ctx -> {
            int sellerId = Integer.parseInt(ctx.pathParam("id"));
            Set<CarDTO> carDTOs = dao.getCarsBySeller(sellerId);

            if (carDTOs.isEmpty()) {
                throw new APIException(200, "No products available", timestamp);
            } else {
                ctx.json(carDTOs);
            }

        };
    }

    public Handler addCarToSeller() {
        return ctx -> {
            int sellerId = Integer.parseInt(ctx.pathParam("sellerid"));
            int carid = Integer.parseInt(ctx.pathParam("carid"));
            if(dao.addCarToSeller(sellerId, carid)){
                ctx.status(200).json("Car added successfully");
            }else {
                throw new APIException(500, "Failed to add the car", timestamp);
            }
        };
    }
}
