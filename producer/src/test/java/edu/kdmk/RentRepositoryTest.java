package edu.kdmk;

import edu.kdmk.config.KafkaConfig;
import edu.kdmk.config.MongoConfig;
import edu.kdmk.sender.RentSender;
import edu.kdmk.manager.ClientManager;
import edu.kdmk.manager.GameManager;
import edu.kdmk.manager.InactiveRentManager;
import edu.kdmk.manager.RentManager;
import edu.kdmk.model.Client;
import edu.kdmk.model.Rent;
import edu.kdmk.model.codec.ClientCodec;
import edu.kdmk.model.codec.GameCodec;
import edu.kdmk.model.codec.RentCodec;
import edu.kdmk.model.game.BoardGame;
import edu.kdmk.model.game.Game;
import edu.kdmk.repository.ClientRepository;
import edu.kdmk.repository.GameRepository;
import edu.kdmk.repository.InactiveRentRepository;
import edu.kdmk.repository.RentRepository;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.bson.*;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class RentRepositoryTest {

    private static MongoConfig mongoConfig;
    private static ClientManager clientManager;
    private static GameManager gameManager;
    private static InactiveRentManager inactiveRentManager;
    private static InactiveRentRepository inactiveRentRepository;
    private static ClientRepository clientRepository;
    private static GameRepository gameRepository;
    private static RentSender rentSender;

    Client client;
    BoardGame game;

    @BeforeAll
    static void setup() throws InterruptedException {
        String connectionString = ConnectionStringProvider.getConnectionString();
        String databaseName = "ndb";

        mongoConfig = new MongoConfig(connectionString, databaseName);
        clientRepository = new ClientRepository(mongoConfig.getDatabase());
        clientManager = new ClientManager(clientRepository);

        gameRepository = new GameRepository(mongoConfig.getDatabase());
        gameManager = new GameManager(gameRepository);

        inactiveRentRepository = new InactiveRentRepository(mongoConfig.getDatabase());
        inactiveRentManager = new InactiveRentManager(inactiveRentRepository);


        rentSender = new RentSender("rents", new KafkaConfig().getProducer());
    }

    @AfterAll
    static void tearDown() {
        try {
            // Close MongoConfig after all tests
            if (mongoConfig != null) {
                mongoConfig.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setupRent() {
        game = new BoardGame("Uno2", 10, 2, 6);
        gameManager.insertGame(game);
        client = new Client("John", "Doe", "123 Main St");
        clientManager.insertClient(client);
    }

    @Test
    public void insertRentTest() {
        RentManager rentManager = new RentManager(mongoConfig.getMongoClient(), new RentRepository(mongoConfig.getDatabase()), gameRepository, clientRepository, inactiveRentRepository, rentSender);

        Rent rent = new Rent(LocalDate.now(), LocalDate.now().plusDays(9), client, game, "KDMK");
        assertTrue(rentManager.createRent(rent));
        assertTrue(rentManager.findRentById(rent.getId()).isPresent());
        assertInstanceOf(Rent.class, rentManager.findRentById(rent.getId()).get());
        assertTrue(gameManager.findGameById(game.getId()).isPresent());
        assertEquals(1, gameManager.findGameById(game.getId()).get().getRentalStatusCount());
        assertTrue(clientManager.findClientById(client.getId()).isPresent());
        assertEquals(1, clientManager.findClientById(client.getId()).get().getRentalCount());
        assertEquals(100, rentManager.findRentById(rent.getId()).get().getRentalPrice());
    }

    @Test
    public void rentSameGame() {
        RentManager rentManager = new RentManager(mongoConfig.getMongoClient(), new RentRepository(mongoConfig.getDatabase()), gameRepository, clientRepository, inactiveRentRepository, rentSender);

        Rent rent = new Rent(LocalDate.now(), LocalDate.now().plusDays(9), client, game, "KDMK");

        rentManager.createRent(rent);

        assertTrue(rentManager.findRentById(rent.getId()).isPresent());
        assertEquals(1, gameManager.findGameById(game.getId()).get().getRentalStatusCount());

        Rent rent2 = new Rent(LocalDate.now(), LocalDate.now().plusDays(9), client, game, "KDMK");

        assertThrows(IllegalStateException.class, () -> rentManager.createRent(rent2));
        assertTrue(rentManager.findRentById(rent2.getId()).isEmpty());
        assertEquals(1, gameManager.findGameById(game.getId()).get().getRentalStatusCount());
        assertEquals(1, clientManager.findClientById(client.getId()).get().getRentalCount());

    }

    @Test
    public void endRentTest() {
        RentManager rentManager = new RentManager(mongoConfig.getMongoClient(), new RentRepository(mongoConfig.getDatabase()), gameRepository, clientRepository, inactiveRentRepository, rentSender);
        Rent rent = new Rent(LocalDate.now(), LocalDate.now().plusDays(2), client, game, "KDMK");
        rentManager.createRent(rent);

        assertTrue(rentManager.completeRent(rent.getId()));
        assertTrue(rentManager.findRentById(rent.getId()).isEmpty());
        assertTrue(inactiveRentManager.findInactiveRentById(rent.getId()).isPresent());
        assertEquals(game.getPricePerDay(), inactiveRentManager.findInactiveRentById(rent.getId()).get().getRentalPrice());
    }

    @Test
    public void extendedRentTest() {
        RentManager rentManager = new RentManager(mongoConfig.getMongoClient(), new RentRepository(mongoConfig.getDatabase()), gameRepository, clientRepository, inactiveRentRepository, rentSender);
        Rent rent = new Rent(LocalDate.now(), LocalDate.now().plusDays(9), client, game, "KDMK");

        rentManager.createRent(rent);

        assertTrue(rentManager.updateRentalEndDate(rent.getId(), LocalDate.now().plusDays(14)));
        assertTrue(rentManager.findRentById(rent.getId()).isPresent());
        assertEquals(LocalDate.now().plusDays(14), rentManager.findRentById(rent.getId()).get().getEndDate());
        assertEquals(150, rentManager.findRentById(rent.getId()).get().getRentalPrice());
    }

    @Test
    public void endRentDateBeforeStart() {
        RentManager rentManager = new RentManager(mongoConfig.getMongoClient(), new RentRepository(mongoConfig.getDatabase()), gameRepository, clientRepository, inactiveRentRepository, rentSender);
        Rent rent = new Rent(LocalDate.now(), LocalDate.now().plusDays(9), client, game, "KDMK");
        rentManager.createRent(rent);

        assertFalse(rentManager.updateRentalEndDate(rent.getId(), LocalDate.now().minusDays(1)));
    }

    @Test
    void encodeTest() {
        RentCodec codec = new RentCodec(new ClientCodec(), new GameCodec());

        BsonDocument document = new BsonDocument();
        BsonWriter writer = new BsonDocumentWriter(document);
        Rent rent = new Rent(LocalDate.now(), LocalDate.now().plusDays(9), client, game, "KDMK");
        codec.encode(writer, rent, EncoderContext.builder().build());

        assertEquals(rent.getId().toString(), document.getString("_id").getValue());
        assertEquals(rent.getStartDate().toString(), document.getString("startDate").getValue());
        assertEquals(rent.getEndDate().toString(), document.getString("endDate").getValue());

    }

    @Test
    void decodeTest() {
        RentCodec codec = new RentCodec(new ClientCodec(), new GameCodec());

        UUID id = UUID.randomUUID();

        BsonDocument document = new BsonDocument()
                .append("_id", new BsonString(id.toString()))
                .append("startDate", new BsonString(LocalDate.now().toString()))
                .append("endDate", new BsonString(LocalDate.now().plusDays(9).toString()));

        BsonReader reader = new BsonDocumentReader(document);
        Rent rent = codec.decode(reader, DecoderContext.builder().build());

        assertEquals(id, rent.getId());
        assertEquals(LocalDate.now(), rent.getStartDate());
        assertEquals(LocalDate.now().plusDays(9), rent.getEndDate());
    }

    @Test
    public void optimisticLockTestTwoRentsOneClient() {

        GameManager gameManager = new GameManager(new GameRepository(mongoConfig.getDatabase()));

        Game newGame = new BoardGame( "Uno", 2 ,2, 6);
        gameManager.insertGame(newGame);


        ClientManager clientManager = new ClientManager(new ClientRepository(mongoConfig.getDatabase()));

        Client newClient = new Client("John", "WDoe", "123 Main St");
        clientManager.insertClient(newClient);
        Client newClient2 = new Client("Jane", "WDoe", "123 Main St");
        clientManager.insertClient(newClient2);


        RentManager rentManager = new RentManager(mongoConfig.getMongoClient(), new RentRepository(mongoConfig.getDatabase()), gameRepository, clientRepository, inactiveRentRepository, rentSender);
        RentManager rentManager2 = new RentManager(mongoConfig.getMongoClient(), new RentRepository(mongoConfig.getDatabase()), gameRepository, clientRepository, inactiveRentRepository, rentSender);


        AtomicBoolean exceptionCaught = new AtomicBoolean(false);

        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(2);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable rentTask1 = () -> {
            try {
                latch.await();
                Rent rent = new Rent(LocalDate.now(), LocalDate.now().plusDays(9), newClient, newGame, "KDMK");
                rentManager.createRent(rent);

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

                Rent rent2 = new Rent(LocalDate.now(), LocalDate.now().plusDays(9), newClient2, newGame, "KDMK");
                rentManager2.createRent(rent2);

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
