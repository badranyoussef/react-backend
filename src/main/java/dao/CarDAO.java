package dao;

import dtos.CarDTO;
import exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import persistence.config.HibernateConfig;
import persistence.model.Car;
import persistence.model.Seller;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CarDAO implements iDAO<CarDTO, Car> {
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(false);

    @Override
    public Set<CarDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT c FROM Car c";
            TypedQuery<Car> query = em.createQuery(jpql, Car.class);
            Set<CarDTO> result = query.getResultList().stream()
                    .map(this::convertToDTO)// Konverter hvert Product til en HealthProductDTO, bruger this for at indikerer at det er klassens egen metode der bruges
                    .collect(Collectors.toSet());
            return result;
        } catch (DatabaseException e) {
            throw new DatabaseException(e.getStatusCode(), "Unable to get cars", e.getTimeStamp());
        }
    }

    @Override
    public CarDTO getById(int id) {
        Car car = null;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            car = em.find(Car.class, id);
            em.getTransaction().commit();
            return convertToDTO(car);
        } catch (DatabaseException e) {
            throw new DatabaseException(e.getStatusCode(), "Car could not be added to database. Car is null", e.getTimeStamp());
        }
    }

    @Override
    public CarDTO create(Car car) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(car);
            em.getTransaction().commit();
            return convertToDTO(car);
        } catch (DatabaseException e) {
            throw new DatabaseException(e.getStatusCode(), "Car could not be added to database. Car is null", e.getTimeStamp());
        }
    }

    @Override
    public CarDTO update(CarDTO carDTO) {
        Car product = convertToEntity(carDTO);
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(product);
            em.getTransaction().commit();
        } catch (DatabaseException e) {
            throw new DatabaseException(e.getStatusCode(), "Was unable to update car in database", e.getTimeStamp());
        }
        return carDTO;
    }

    @Override
    public CarDTO delete(int id) {
        CarDTO carDTO = getById(id);
        Car product = convertToEntity(carDTO);
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.remove(product);
            em.getTransaction().commit();
            return carDTO;
        } catch (DatabaseException e) {
            throw new DatabaseException(e.getStatusCode(), "Unable to delete car from database. car might not be in DB", e.getTimeStamp());
        }
    }

    public Set<CarDTO> getCarsByYear(int year) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT c FROM Car c WHERE c.year = :year";
            TypedQuery<Car> query = em.createQuery(jpql, Car.class);
            query.setParameter("year", year);
            Set<CarDTO> result = query.getResultList().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toSet());
            return result;
        } catch (DatabaseException e) {
            throw new DatabaseException(e.getStatusCode(), "Unable to get cars from DB. No cars available", e.getTimeStamp());
        }
    }


    public double getTotalPrivateByband(String brand) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT c FROM Car c WHERE c.brand = :brand";
            TypedQuery<Double> query = em.createQuery(jpql, Double.class);
            query.setParameter("brand", brand);
            Double result = query.getSingleResult();
            return result != null ? result : 0.0;
        } catch (DatabaseException e) {
            throw new DatabaseException(e.getStatusCode(), "Unable to get total price by brand. No cars available", e.getTimeStamp());
        }
    }

    public boolean addCarToSeller(int sellerId, int carId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Seller seller = em.find(Seller.class, sellerId);
            Car car = em.find(Car.class, carId);
            car.setSeller(seller);
            seller.addCar(car);
            em.merge(seller);
            em.getTransaction().commit();
            return true;
        } catch (DatabaseException e) {
            throw new DatabaseException(e.getStatusCode(), "Unable to add car to seller. one of the entities might not exists (null)", e.getTimeStamp());
        }
    }

    public Set<CarDTO> getCarsBySeller(int sellerId) {
        try (EntityManager em = emf.createEntityManager()) {
            String jpql = "SELECT c FROM Car c WHERE c.seller.id = :sellerId";
            TypedQuery<Car> query = em.createQuery(jpql, Car.class);
            query.setParameter("sellerId", sellerId);
            Set<CarDTO> cars = new HashSet<>(query.getResultList().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toSet()));
            return cars;
        } catch (DatabaseException e) {
            throw new DatabaseException(e.getStatusCode(), "Unable to retrieve cars from seller. Seller might not have any cars in stock - error message: " + e.getMessage(), e.getTimeStamp());
        }
    }

    public CarDTO convertToDTO(Car car) {
        return CarDTO.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .make(car.getMake())
                .model(car.getModel())
                .year(car.getYear())
                .firstRegistrationDate(car.getFirstRegistrationDate())
                .price(car.getPrice())
                .build();
    }


    public Car convertToEntity(CarDTO carDTO) {
        return Car.builder()
                .id(carDTO.getId())
                .brand(carDTO.getBrand())
                .make(carDTO.getMake())
                .model(carDTO.getModel())
                .year(carDTO.getYear())
                .firstRegistrationDate(carDTO.getFirstRegistrationDate())
                .price(carDTO.getPrice())
                .build();
    }
}
