package rest.routes;

import appConfig.Application;
import dao.CarDAO;
import io.javalin.http.ContentType;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import persistence.config.HibernateConfig;
import persistence.model.Car;
import persistence.model.Seller;
import java.time.LocalDate;

import static org.hamcrest.core.IsEqual.equalTo;

class CarRouteTest {
    private static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryConfig(true);
    private static Application app;
    private static int port = 7070;
    private static CarRoute route = new CarRoute();
    private  CarDAO dao = new CarDAO();
    private static Car car1;
    private static Car car2;
    private static Car car3;
    private static Car car4;
    private static Seller seller1;
    private static Seller seller2;

    @BeforeAll
    static void setUpBeforeAll() {
        RestAssured.baseURI = "http://localhost:7070/carshop/api/cars";
        app = Application.getInstance();
        app.startServer(port)
                .setExceptionHandlers()
                .setRoute(route.getCarRoutes())
                .checkSecurityRoles();
    }

    @BeforeEach
    void setUp() {
        car1 = new Car("Toyota", "Corolla", "Japan", LocalDate.of(2020, 1, 1), 20000.0, 2020);
        car2= new Car("Honda", "Civic", "Japan", LocalDate.of(2019, 6, 1), 25000.0, 2019);
        car3= new Car("Ford", "Focus", "USA", LocalDate.of(2018, 3, 1), 18000.0, 2018);
        car4= new Car("Chevrolet", "Cruze", "USA", LocalDate.of(2017, 9, 1), 22000.0, 2017);

        seller1 = new Seller("Hans","Hansen", "kontakt@auto.dk",12345678,"Amager");
        seller2 = new Seller("Tom","Tomsen", "kontakt@autotomsen.dk",12345678,"Glostrup");

        seller1.addCar(car1);
        seller1.addCar(car2);
        seller2.addCar(car3);
        seller2.addCar(car4);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(seller1);
        em.persist(seller2);
        em.getTransaction().commit();

    }

    @AfterEach
    void tearDownAfterEach() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Car ").executeUpdate();
            em.createQuery("DELETE FROM Seller ").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @AfterAll
    static void tearDown() {
        emf.close();
        app.stopServer();
    }

    @Test
    @DisplayName("Testing if data is persistet")
    public void test1() {

        int expectedCars = 4;

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/")
                .then()
                .statusCode(200)
                .body("size()", equalTo(expectedCars));

    }

    @Test
    @DisplayName("Testing if the correct data is persisted")
    public void test2() {
        Car car = null;
        try(EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            car = em.find(Car.class,1);
            em.getTransaction().commit();
        }

        String expectedBrand = car.getBrand();
        String expectedModel = car.getModel();
        int expectedYear = car.getYear();

        String respons = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/1")
                .then()
                .statusCode(200)
                .body("brand", equalTo(expectedBrand))
                .body("model",equalTo(expectedModel))
                .body("year", equalTo(expectedYear))
                .extract().asString();

    }

    @Test
    @DisplayName("Testing that 2 storages er created and saved in DB")
    public void test3() {
        String setBody = "{\"brand\": \"TOYO\", \"model\": \"MOYO\", \"make\": \"DENMARK\", \"firstRegistrationDate\": \"2020-01-01\", \"year\":\"2020\" }";

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(setBody)
                .when()
                .post("/")
                .then()
                .statusCode(200)
                .body("brand", equalTo("TOYO"))
                .body("model", equalTo("MOYO"))
                .extract().asPrettyString();

    }
}