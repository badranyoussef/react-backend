package persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import persistence.config.HibernateConfig;

import java.time.LocalDate;

public class Populator {

private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(false);

    private static Car car1 = new Car("Toyota", "Corolla", "Japan", LocalDate.of(2020, 1, 1), 20000.0, 2020);
    private static Car car2= new Car("Honda", "Civic", "Japan", LocalDate.of(2019, 6, 1), 25000.0, 2019);
    private static Car car3= new Car("Ford", "Focus", "USA", LocalDate.of(2018, 3, 1), 18000.0, 2018);
    private static Car car4= new Car("Chevrolet", "Cruze", "USA", LocalDate.of(2017, 9, 1), 22000.0, 2017);
    private static Car car5= new Car("Volkswagen", "Golf", "Germany", LocalDate.of(2016, 12, 1), 19000.0, 2016);
    private static Car car6= new Car("BMW", "3 Series", "Germany", LocalDate.of(2015, 5, 1), 30000.0, 2015);
    private static Car car7= new Car("Mercedes-Benz", "C-Class", "Germany", LocalDate.of(2014, 8, 1), 35000.0, 2014);
    private static Car car8= new Car("Audi", "A4", "Germany", LocalDate.of(2013, 2, 1), 28000.0, 2013);
    private static Car car9= new Car("Hyundai", "Elantra", "South Korea", LocalDate.of(2012, 7, 1), 17000.0, 2012);
    private static Car car10= new Car("Kia", "Optima", "South Korea", LocalDate.of(2011, 11, 1), 16000.0, 2011);

    private static Seller seller1 = new Seller("Hans","Hansen", "kontakt@auto.dk",12345678,"Amager");
    private static Seller seller2 = new Seller("Tom","Tomsen", "kontakt@autotomsen.dk",12345678,"Glostrup");

    public static void populate(){
        seller1.addCar(car1);
        seller1.addCar(car2);
        seller1.addCar(car3);
        seller1.addCar(car4);
        seller2.addCar(car5);
        seller2.addCar(car6);
        seller2.addCar(car7);
        seller2.addCar(car8);
        seller2.addCar(car9);
        seller2.addCar(car10);

        try(EntityManager em = emf.createEntityManager()){
            em.getTransaction().begin();
            em.persist(seller1);
            em.persist(seller2);
            em.getTransaction().commit();
        }
    }

}

