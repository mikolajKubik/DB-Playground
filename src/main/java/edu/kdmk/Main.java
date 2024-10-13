package edu.kdmk;

import edu.kdmk.model.Client;
import edu.kdmk.model.Rent;
import edu.kdmk.model.vehicle.Car;
import edu.kdmk.repositories.implemntations.ClientRepository;
import edu.kdmk.repositories.implemntations.RentRepository;
import edu.kdmk.repositories.implemntations.VehicleRepository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.Date;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
    public static void main(String[] args) {
//        EntityManagerFactory entityManagerFactory = null;
//        EntityManager entityManager = null;
//
//        try {
//            entityManagerFactory = Persistence.createEntityManagerFactory("my-persistence-unit");
//            entityManager = entityManagerFactory.createEntityManager();
//
//
//            var client = Client.builder()
//                    .name("John Doeeee")
//                    .phoneNumber("123-456-789")
//                    .build();
//
//            ClientRepository clientRepository = new ClientRepository(entityManager);
//            clientRepository.add(client);
//
//        } finally {
//            if (entityManager != null && entityManager.isOpen()) {
//                entityManager.close();
//            }
//            if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
//                entityManagerFactory.close();
//            }
//        }

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("my-persistence-unit");

        ClientRepository clientRepository = new ClientRepository(entityManagerFactory);


        var client1 = Client.builder()
                .name("Adam A")
                .phoneNumber("123-456-789")
                .address("123 Main St")
                .build();

        var client2 = Client.builder()
                .name("Bogdan B")
                .phoneNumber("123-456-789")
                .address("123 Main St")
                .build();

        var vehicle1 = Car.builder()
                        .licensePlate("123-456-789")
                        .brand("Toyota")
                        .model("Corolla")
                        .year(2021)
                        .price(100)
                        .numberOfDoors(5)
                        .numberOfSeats(5)
                        .build();

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

        var cRepo = new ClientRepository(entityManagerFactory);
        var vRepo = new VehicleRepository(entityManagerFactory);


        cRepo.add(client1);
        cRepo.add(client2);
        vRepo.add(vehicle1);

//        entityManagerFactory.close();
        //rRepo.add(rent1);

        // Tworzymy CountDownLatch z wartością początkową 1, aby oba wątki czekały na sygnał startu
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(2);

        // Tworzenie ExecutorService do zarządzania wątkami
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Tworzenie dwóch wątków, które dodają wypożyczenia
        Runnable rentTask1 = () -> {
            try {
//                EntityManagerFactory entityManagerFactory2 = Persistence.createEntityManagerFactory("my-persistence-unit");

                var rRepo1 = new RentRepository(entityManagerFactory);
                latch.await();  // Czekamy na sygnał startu
                //Thread.sleep(5000);
                rRepo1.add(rent1);
                System.out.println("Rent1 added by thread: " + Thread.currentThread().getName());
//                entityManagerFactory2.close();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                latch2.countDown();
            }
        };

        Runnable rentTask2 = () -> {
            try {
//                EntityManagerFactory entityManagerFactory3 = Persistence.createEntityManagerFactory("my-persistence-unit");
                var rRepo2 = new RentRepository(entityManagerFactory);
                latch.await();  // Czekamy na sygnał startu
                rRepo2.add(rent2);
                System.out.println("Rent2 added by thread: " + Thread.currentThread().getName());
//                entityManagerFactory3.close();
            } catch (Exception e) {
                e.printStackTrace();
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

        // Zakończenie działania ExecutorService
        //executor.shutdown();
        try {
            latch2.await();  // Wait for both threads to complete
            System.out.println("Both threads have completed.");
            entityManagerFactory.close();  // Now safe to close EntityManagerFactory
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Zakończenie działania ExecutorService
            executor.shutdown();
        }

        //System.out.println("Main thread finished");
        //entityManagerFactory.close();
    }


}