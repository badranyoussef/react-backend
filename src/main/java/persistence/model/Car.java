package persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "cars")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;
    private String brand;
    private String model;
    private String make;
    private LocalDate firstRegistrationDate;
    private double price;
    private int year;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    public Car(String brand, String model, String make, LocalDate firstRegistrationDate, double price, int year) {
        this.brand = brand;
        this.model = model;
        this.make = make;
        this.firstRegistrationDate = firstRegistrationDate;
        this.price = price;
        this.year = year;
    }
}
