package persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sellers")
@Builder
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    private String firstName;
    private String lastName;

    public Seller(String firstName, String lastName, String email, int phone, String city) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.city = city;
    }

    private String email;
    private int phone;
    private String city;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "seller", fetch = FetchType.EAGER)
    Set<Car> cars = new HashSet<>();

    public void addCar(Car car){
        if(car != null){
            cars.add(car);
            car.setSeller(this);
        }
    }
    public void removeCar(Car car){
        if(car != null){
            cars.remove(car);
        }
    }

}
