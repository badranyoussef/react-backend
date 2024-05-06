package dtos;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CarDTO {
    private int id;
    private String brand;
    private String model;
    private String make;
    private LocalDate firstRegistrationDate;
    private double price;
    private int year;

}
