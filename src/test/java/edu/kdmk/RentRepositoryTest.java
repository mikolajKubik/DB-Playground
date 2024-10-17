
package edu.kdmk;

import edu.kdmk.managers.ClientManager;
import edu.kdmk.managers.RentManager;
import edu.kdmk.managers.VehicleManager;
import edu.kdmk.model.Client;
import edu.kdmk.model.Rent;
import edu.kdmk.model.vehicle.Car;
import edu.kdmk.model.vehicle.Vehicle;
import edu.kdmk.repositories.EntityRepository;
import edu.kdmk.repositories.implemntations.ClientRepository;
import edu.kdmk.repositories.implemntations.RentRepository;
import edu.kdmk.repositories.implemntations.VehicleRepository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class RentRepositoryTest {


    Client client1;
    Client client2;
    Vehicle vehicle1;
    Vehicle vehicle2;

    EntityManagerFactory emf;
    EntityRepository<Client> cRepo;
    EntityRepository<Vehicle> vRepo;
    EntityRepository<Rent> rRepo;

    ClientManager cManager;
    VehicleManager vManager;
    RentManager rManager;


    @BeforeEach
    public void setUp() {
        client1 = Client.builder()
                .name("Adam A")
                .phoneNumber("123-456-789")
                .address("123 Main St")
                .build();

        client2 = Client.builder()
                .name("Bogdan B")
                .phoneNumber("123-456-789")
                .address("123 Main St")
                .build();

        vehicle1 = Car.builder()
                .licensePlate("123-456-789")
                .brand("Toyota")
                .model("Corolla")
                .year(2021)
                .pricePerDay(100)
                .numberOfDoors(5)
                .numberOfSeats(5)
                .build();

        vehicle2 = Car.builder()
                .licensePlate("987-654-321")
                .brand("Opel")
                .model("Insignia")
                .year(2023)
                .pricePerDay(130)
                .numberOfDoors(5)
                .numberOfSeats(5)
                .build();

        emf = Persistence.createEntityManagerFactory("my-persistence-unit");
        cRepo = new ClientRepository();
        vRepo = new VehicleRepository();
        rRepo = new RentRepository();
        cManager = new ClientManager(emf, cRepo);
        vManager = new VehicleManager(emf, vRepo);
        rManager = new RentManager(emf, rRepo, vRepo, cRepo);

    }

    @AfterEach
    public void tearDown() {
        emf.close();
    }

    @Test
    public void addRentTest() {

        Client client = cManager.addClient(client1);
        Vehicle vehicle = vManager.addVehicle(vehicle1);

        var rent = Rent.builder()
                .client(client)
                .vehicle(vehicle)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        Rent addedRent = rManager.addRent(rent);
        System.out.println(addedRent.getId());
        Rent rent2 = rManager.getRentById(addedRent.getId());
        assertEquals(addedRent, rent2);
    }

    @Test
    public void endRentTest() {
        Client client = cManager.addClient(client1);
        Vehicle vehicle = vManager.addVehicle(vehicle1);

        var rent = Rent.builder()
                .client(client)
                .vehicle(vehicle)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        Rent addedRent = rManager.addRent(rent);
        Rent endedRent = rManager.endRent(addedRent.getId());
        assertTrue(endedRent.isReturned());
    }

    @Test
    public void endRentNullTest() {
        assertThrows(IllegalArgumentException.class, () -> rManager.endRent(0));
    }

    @Test
    public void changeRentLength() {
        Client client = cManager.addClient(client1);
        Vehicle vehicle = vManager.addVehicle(vehicle1);

        var rent = Rent.builder()
                .client(client)
                .vehicle(vehicle)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf("2024-10-20"))
                .build();

        Rent addedRent = rManager.addRent(rent);
        Rent changedRent = rManager.changeRentLength(addedRent.getId(), 5);
        assertEquals(changedRent.getEndDate(), Date.valueOf("2024-10-25"));
    }

    @Test
    public void getRentByIdTest() {
        cManager.addClient(client1);
        vManager.addVehicle(vehicle1);

        var rent = Rent.builder()
                .client(client1)
                .vehicle(vehicle1)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        rManager.addRent(rent);
        assertEquals(rManager.getRentById(rent.getId()), rent);
    }

    @Test
    public void testRentVehicleLimit() {

        var cManager = new ClientManager(emf, new ClientRepository());
        var vManager = new VehicleManager(emf, new VehicleRepository());

        cManager.addClient(client1);
        vManager.addVehicle(vehicle1);
        vManager.addVehicle(vehicle2);

        var rent1 = Rent.builder()
                .client(client1)
                .vehicle(vehicle1)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        var rManager = new RentManager(emf, new RentRepository(), new VehicleRepository(), new ClientRepository());
        rManager.addRent(rent1);

        var rent2 = Rent.builder()
                .client(client2)
                .vehicle(vehicle1)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        assertThrows(IllegalArgumentException.class, () -> rManager.addRent(rent2));

    }


    @Test
    public void optimisticLockTestTwoRentsOneClient() {

        var cManager = new ClientManager(emf, new ClientRepository());
        var vManager = new VehicleManager(emf, new VehicleRepository());

        cManager.addClient(client1);
        vManager.addVehicle(vehicle1);
        vManager.addVehicle(vehicle2);

        var rent1 = Rent.builder()
                .client(client1)
                .vehicle(vehicle1)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        var rent2 = Rent.builder()
                .client(client2)
                .vehicle(vehicle2)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        AtomicBoolean exceptionCaught = new AtomicBoolean(false);

        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(2);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable rentTask1 = () -> {
            try {
                var rManager1 = new RentManager(emf, new RentRepository(), new VehicleRepository(), new ClientRepository());
                latch.await();

                rManager1.addRent(rent1);
                System.out.println("Rent1 added by thread: " + Thread.currentThread().getName());
            } catch (Exception e) {
                e.printStackTrace();
                exceptionCaught.set(true);
            }finally {
                latch2.countDown();
            }
        };

        Runnable rentTask2 = () -> {
            try {
                var rManager2 = new RentManager(emf, new RentRepository(), new VehicleRepository(), new ClientRepository());
                latch.await();

                rManager2.addRent(rent2);
                System.out.println("Rent2 added by thread: " + Thread.currentThread().getName());
            } catch (Exception e) {
                e.printStackTrace();
                exceptionCaught.set(true);
            } finally {
                latch2.countDown();
            }
        };

        executor.submit(rentTask1);
        executor.submit(rentTask2);

        latch.countDown();

        try {
            latch2.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        assertTrue(exceptionCaught.get());
    }

    @Test
    public void optimisticLockTestTwoRentsOneVehicle() {

        var cManager = new ClientManager(emf, new ClientRepository());
        var vManager = new VehicleManager(emf, new VehicleRepository());

        cManager.addClient(client1);
        cManager.addClient(client2);
        vManager.addVehicle(vehicle1);

        var rent1 = Rent.builder()
                .client(client1)
                .vehicle(vehicle1)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        var rent2 = Rent.builder()
                .client(client2)
                .vehicle(vehicle1)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        AtomicBoolean exceptionCaught = new AtomicBoolean(false);

        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(2);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable rentTask1 = () -> {
            try {
                var rManager1 = new RentManager(emf, new RentRepository(), new VehicleRepository(), new ClientRepository());
                latch.await();

                rManager1.addRent(rent1);
                System.out.println("Rent1 added by thread: " + Thread.currentThread().getName());
            } catch (Exception e) {
                e.printStackTrace();
                exceptionCaught.set(true);
            }finally {
                latch2.countDown();
            }
        };

        Runnable rentTask2 = () -> {
            try {
                var rManager2 = new RentManager(emf, new RentRepository(), new VehicleRepository(), new ClientRepository());
                latch.await();

                rManager2.addRent(rent2);
                System.out.println("Rent2 added by thread: " + Thread.currentThread().getName());
            } catch (Exception e) {
                e.printStackTrace();
                exceptionCaught.set(true);
            } finally {
                latch2.countDown();
            }
        };

        executor.submit(rentTask1);
        executor.submit(rentTask2);

        latch.countDown();

        try {
            latch2.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        assertTrue(exceptionCaught.get());
    }

}

