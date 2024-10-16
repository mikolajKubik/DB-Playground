
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
                .price(100)
                .numberOfDoors(5)
                .numberOfSeats(5)
                .build();

        vehicle2 = Car.builder()
                .licensePlate("987-654-321")
                .brand("Opel")
                .model("Insignia")
                .year(2023)
                .price(130)
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

        cManager.addClient(client1);
        vManager.addVehicle(vehicle1);

        var rent = Rent.builder()
                .client(client1)
                .vehicle(vehicle1)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        rManager.addRent(rent);


        Rent rentFromDb = rManager.getRentById(rent.getId());
        assertEquals(rent, rentFromDb);
    }

    @Test
    public void removeRentTest() {
        cManager.addClient(client1);
        vManager.addVehicle(vehicle1);

        var rent = Rent.builder()
                .client(client1)
                .vehicle(vehicle1)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        rManager.addRent(rent);
        assertTrue(rManager.removeRent(rent));
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
    public void updateRentTest() { //  Ale co robi ten test to nie mam pojęcia co nie xD
        // ale już działa, był problem bo 2 transakcje chciały updateować go po sobie a nie dostawwały z bazy nowej wersji
        // wieć poprawnie dostawaliście optimnistica locka wyjątekiem na morde okok?
        cManager.addClient(client1);
        vManager.addVehicle(vehicle1);

        var rent = Rent.builder()
                .client(client1)
                .vehicle(vehicle1)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        rManager.addRent(rent);

        assertEquals(rent, rManager.getRentById(rent.getId()));

        var rentFromDb = rManager.getRentById(rent.getId());
        rentFromDb.setEndDate(Date.valueOf(LocalDate.now().plusDays(10)));

        assertEquals(rManager.updateRent(rentFromDb).getId(), rent.getId());
    }

    @Test
    public void optimisticLockTestTwoRentsOneClient() {
//        var cRepo = new ClientRepository();
//        var vRepo = new VehicleRepository();
//
//        cRepo.add(client1);
//        vRepo.add(vehicle1);
//        vRepo.add(vehicle2);
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

        final boolean[] exceptionCaught = {false};

        // Tworzymy CountDownLatch z wartością początkową 1, aby oba wątki czekały na sygnał startu
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(2);

        // Tworzenie ExecutorService do zarządzania wątkami
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Tworzenie dwóch wątków, które dodają wypożyczenia
        Runnable rentTask1 = () -> {
            try {

                var rManager1 = new RentManager(emf, new RentRepository(), new VehicleRepository(), new ClientRepository());
                // var rRepo1 = new RentRepository();
                latch.await();  // Czekamy na sygnał startu

                //rRepo1.add(rent1);
                rManager1.addRent(rent1);
                System.out.println("Rent1 added by thread: " + Thread.currentThread().getName());
            } catch (Exception e) {
                e.printStackTrace();
                exceptionCaught[0] = true;
            }finally {
                latch2.countDown();
            }
        };

        Runnable rentTask2 = () -> {
            try {
//                EntityManagerFactory entityManagerFactory3 = Persistence.createEntityManagerFactory("my-persistence-unit");
//                var rRepo2 = new RentRepository();
                var rManager2 = new RentManager(emf, new RentRepository(), new VehicleRepository(), new ClientRepository());
                latch.await();  // Czekamy na sygnał startu
//                rRepo2.add(rent2);
                rManager2.addRent(rent2);
                System.out.println("Rent2 added by thread: " + Thread.currentThread().getName());
//                entityManagerFactory3.close();
            } catch (Exception e) {
                e.printStackTrace();
                exceptionCaught[0] = true;
            } finally {
                latch2.countDown();
            }
        };

        // Uruchamianie obu wątków
        executor.submit(rentTask1);
        executor.submit(rentTask2);

        // Główny wątek zwalnia blokadę, aby oba wątki mogły się rozpocząć
        System.out.println("Main thread releasing latch...");
        latch.countDown();

        try {
            latch2.await();
            System.out.println("Both threads have completed.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        assertTrue(exceptionCaught[0]);
    }

    @Test
    public void optimisticLockTestTwoRentsOneVehicle() {
//        var cRepo = new ClientRepository();
//        var vRepo = new VehicleRepository();
//
//        cRepo.add(client1);
//        cRepo.add(client2);
//        vRepo.add(vehicle1);

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

        final boolean[] exceptionCaught = {false};

        // Tworzymy CountDownLatch z wartością początkową 1, aby oba wątki czekały na sygnał startu
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(2);

        // Tworzenie ExecutorService do zarządzania wątkami
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Tworzenie dwóch wątków, które dodają wypożyczenia
        Runnable rentTask1 = () -> {
            try {

//                var rRepo1 = new RentRepository();
                var rManager1 = new RentManager(emf, new RentRepository(), new VehicleRepository(), new ClientRepository());
                latch.await();  // Czekamy na sygnał startu

//                rRepo1.add(rent1);
                rManager1.addRent(rent1);
                System.out.println("Rent1 added by thread: " + Thread.currentThread().getName());
            } catch (Exception e) {
                e.printStackTrace();
                exceptionCaught[0] = true;
            }finally {
                latch2.countDown();
            }
        };

        Runnable rentTask2 = () -> {
            try {
//                EntityManagerFactory entityManagerFactory3 = Persistence.createEntityManagerFactory("my-persistence-unit");
//                var rRepo2 = new RentRepository();
                var rManager2 = new RentManager(emf, new RentRepository(), new VehicleRepository(), new ClientRepository());
                latch.await();  // Czekamy na sygnał startu
//                rRepo2.add(rent2);
                rManager2.addRent(rent2);
                System.out.println("Rent2 added by thread: " + Thread.currentThread().getName());
//                entityManagerFactory3.close();
            } catch (Exception e) {
                e.printStackTrace();
                exceptionCaught[0] = true;
            } finally {
                latch2.countDown();
            }
        };

        // Uruchamianie obu wątków
        executor.submit(rentTask1);
        executor.submit(rentTask2);

        // Główny wątek zwalnia blokadę, aby oba wątki mogły się rozpocząć
        System.out.println("Main thread releasing latch...");
        latch.countDown();

        try {
            latch2.await();
            System.out.println("Both threads have completed.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        assertTrue(exceptionCaught[0]);
    }
//
//
//
    @Test
    public void testRentVehicleLimit() {
//        var cRepo = new ClientRepository();
//        var vRepo = new VehicleRepository();
//
//        cRepo.add(client1);
//        cRepo.add(client2);
//        vRepo.add(vehicle1);
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


//        var rRepo = new RentRepository();
//        rRepo.add(rent1);
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

}


/*
* @Test
    public void optimisticLockTestTwoRentsOneClient() {
        var cRepo = new ClientRepository();
        var vRepo = new VehicleRepository();

        cRepo.add(client1);
        vRepo.add(vehicle1);
        vRepo.add(vehicle2);

        var rent1 = Rent.builder()
                .client(client1)
                .vehicle(vehicle1)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        var rent2 = Rent.builder()
                .client(client1)
                .vehicle(vehicle2)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        final boolean[] exceptionCaught = {false};

        // Tworzymy CountDownLatch z wartością początkową 1, aby oba wątki czekały na sygnał startu
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(2);

        // Tworzenie ExecutorService do zarządzania wątkami
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Tworzenie dwóch wątków, które dodają wypożyczenia
        Runnable rentTask1 = () -> {
            try {

                var rRepo1 = new RentRepository();
                latch.await();  // Czekamy na sygnał startu

                rRepo1.add(rent1);
                System.out.println("Rent1 added by thread: " + Thread.currentThread().getName());
            } catch (Exception e) {
                e.printStackTrace();
                exceptionCaught[0] = true;
            }finally {
                latch2.countDown();
            }
        };

        Runnable rentTask2 = () -> {
            try {
//                EntityManagerFactory entityManagerFactory3 = Persistence.createEntityManagerFactory("my-persistence-unit");
                var rRepo2 = new RentRepository();
                latch.await();  // Czekamy na sygnał startu
                rRepo2.add(rent2);
                System.out.println("Rent2 added by thread: " + Thread.currentThread().getName());
//                entityManagerFactory3.close();
            } catch (Exception e) {
                e.printStackTrace();
                exceptionCaught[0] = true;
            } finally {
                latch2.countDown();
            }
        };

        // Uruchamianie obu wątków
        executor.submit(rentTask1);
        executor.submit(rentTask2);

        // Główny wątek zwalnia blokadę, aby oba wątki mogły się rozpocząć
        System.out.println("Main thread releasing latch...");
        latch.countDown();

        try {
            latch2.await();
            System.out.println("Both threads have completed.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        assertTrue(exceptionCaught[0]);
    }
* */
