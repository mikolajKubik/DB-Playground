package edu.kdmk;

import com.mongodb.client.MongoDatabase;
import edu.kdmk.config.MongoConfig;
import edu.kdmk.managers.ClientManager;
import edu.kdmk.managers.GameManager;
import edu.kdmk.managers.RentManager;
import edu.kdmk.models.Client;
import edu.kdmk.models.game.BoardGame;
import edu.kdmk.models.game.Game;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class RentRepositoryTest {

    private static MongoConfig mongoConfig;
    private static MongoDatabase database;

    @BeforeAll
    static void setup() {
        String connectionString = "mongodb://root:root@111.222.32.4:27017,111.222.32.3:27018,111.222.32.2:27019/?replicaSet=rs0&authSource=admin";
        String databaseName = "ndb";

        // Initialize MongoConfig before all tests
        mongoConfig = new MongoConfig(connectionString, databaseName);
        database = mongoConfig.getDatabase();

        System.out.println("MongoDB connection setup before all tests");
    }

    @AfterAll
    static void tearDown() {
        try {
            // Close MongoConfig after all tests
            if (mongoConfig != null) {
                mongoConfig.close();
            }
            System.out.println("MongoDB connection closed after all tests");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void optimisticLockTestTwoRentsOneClient() {

        GameManager gameManager = new GameManager(mongoConfig.getMongoClient(), mongoConfig.getDatabase());

        Game newGame = new BoardGame( "Uno", 2, 6);
        gameManager.insertGame(newGame);


        ClientManager clientManager = new ClientManager(mongoConfig.getDatabase());

        Client newClient = new Client("John WDoe", "123 Main St");
        clientManager.insertClient(newClient);
        Client newClient2 = new Client("Jane WDoe", "123 Main St");
        clientManager.insertClient(newClient2);


        RentManager rentManager = new RentManager(mongoConfig.getMongoClient(), mongoConfig.getDatabase());
        RentManager rentManager2 = new RentManager(mongoConfig.getMongoClient(), mongoConfig.getDatabase());


        AtomicBoolean exceptionCaught = new AtomicBoolean(false);

        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(2);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable rentTask1 = () -> {
            try {
                latch.await();

                rentManager.createRent(newClient, newGame, LocalDate.now(), LocalDate.now().plusDays(9));

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
                latch.await();

                rentManager2.createRent(newClient2, newGame, LocalDate.now(), LocalDate.now().plusDays(9));

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

        //assertTrue(exceptionCaught.get());
    }

}
