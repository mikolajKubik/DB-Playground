/*
package edu.kdmk;

import edu.kdmk.model.Client;
import edu.kdmk.model.Rent;
import edu.kdmk.model.vehicle.Car;
import edu.kdmk.model.vehicle.Vehicle;
import edu.kdmk.repositories.JPAUtil;
import edu.kdmk.repositories.implemntations.ClientRepository;
import edu.kdmk.repositories.implemntations.RentRepository;
import edu.kdmk.repositories.implemntations.VehicleRepository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.Persistence;
import jakarta.persistence.RollbackException;
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


    @BeforeEach
    public void beforeEach() {

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
    }

    @Test
    public void testAdd() {
        // given
        // when
        // then
    }

    @Test
    public void addRentTest() {
        var cRepo = new ClientRepository();
        var vRepo = new VehicleRepository();

        cRepo.add(client1);
        vRepo.add(vehicle1);

        var rent = Rent.builder()
                .client(client1)
                .vehicle(vehicle1)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        var rRepo = new RentRepository();
        rRepo.add(rent);


        Rent rentFromDb = rRepo.getById(rent.getId());

        assertEquals(rent, rentFromDb);
    }

    @Test
    public void removeRentTest() {
        var cRepo = new ClientRepository();
        var vRepo = new VehicleRepository();

        cRepo.add(client1);
        vRepo.add(vehicle1);

        var rent = Rent.builder()
                .client(client1)
                .vehicle(vehicle1)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        var rRepo = new RentRepository();
        rRepo.add(rent);
        assertTrue(rRepo.remove(rent));
    }

    @Test
    public void getRentByIdTest() {
        var cRepo = new ClientRepository();
        var vRepo = new VehicleRepository();

        cRepo.add(client1);
        vRepo.add(vehicle1);

        var rent = Rent.builder()
                .client(client1)
                .vehicle(vehicle1)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        var rRepo = new RentRepository();
        rRepo.add(rent);
        assertEquals(rRepo.getById(rent.getId()), rent);
    }

    @Test
    public void updateRentTest() {
        var cRepo = new ClientRepository();
        var vRepo = new VehicleRepository();

        cRepo.add(client1);
        vRepo.add(vehicle1);

        var rent = Rent.builder()
                .client(client1)
                .vehicle(vehicle1)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        var rRepo = new RentRepository();
        rRepo.add(rent);



        assertEquals(rent, rRepo.getById(rent.getId()));
    }

    @Test
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

    @Test
    public void optimisticLockTestTwoRentsOneVehicle() {
        var cRepo = new ClientRepository();
        var vRepo = new VehicleRepository();

        cRepo.add(client1);
        cRepo.add(client2);
        vRepo.add(vehicle1);

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



    @Test
    public void testRentVehicleLimit() {
        var cRepo = new ClientRepository();
        var vRepo = new VehicleRepository();

        cRepo.add(client1);
        cRepo.add(client2);
        vRepo.add(vehicle1);

        var rent1 = Rent.builder()
                .client(client1)
                .vehicle(vehicle1)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();


        var rRepo = new RentRepository();
        rRepo.add(rent1);

        var rent2 = Rent.builder()
                .client(client2)
                .vehicle(vehicle1)
                .startDate(Date.valueOf(LocalDate.now()))
                .endDate(Date.valueOf(LocalDate.now().plusDays(5)))
                .build();

        assertThrows(IllegalArgumentException.class, () -> rRepo.add(rent2));

    }





    @Test
    public void testRemove() {
        // given
        // when
        // then
    }

    @Test
    public void testGetById() {

    }

}

*/
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
