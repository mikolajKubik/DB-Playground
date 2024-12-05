package edu.kdmk;

import edu.kdmk.config.MongoConfig;
import edu.kdmk.managers.ClientManager;
import edu.kdmk.managers.GameManager;
import edu.kdmk.managers.InactiveRentManager;
import edu.kdmk.managers.RentManager;
import edu.kdmk.models.Client;
import edu.kdmk.models.Rent;
import edu.kdmk.models.codec.ClientCodec;
import edu.kdmk.models.codec.GameCodec;
import edu.kdmk.models.codec.RentCodec;
import edu.kdmk.models.game.BoardGame;
import edu.kdmk.models.game.Game;
import edu.kdmk.models.game.GameType;
import edu.kdmk.repositories.ClientRepository;
import edu.kdmk.repositories.GameRepository;
import edu.kdmk.repositories.InactiveRentRepository;
import edu.kdmk.repositories.RentRepository;
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
    Client client;
    BoardGame game;

    @BeforeAll
    static void setup() {
        mongoConfig = new MongoConfig();
        clientManager = new ClientManager(new ClientRepository(mongoConfig.getDatabase()));
        gameManager = new GameManager(new GameRepository(mongoConfig.getDatabase()));
        inactiveRentManager = new InactiveRentManager(new InactiveRentRepository(mongoConfig.getDatabase()));
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
        game = new BoardGame(UUID.randomUUID(),"Uno2", GameType.BOARD_GAME, 10, 0, 2, 6);
        gameManager.insertGame(game);
        client = new Client(UUID.randomUUID(), "John", "Doe", "123 Main St", 0);
        clientManager.insertClient(client);
    }

    @Test
    public void insertRentTest() {
        RentManager rentManager = new RentManager(mongoConfig.getMongoClient(),
                new RentRepository(mongoConfig.getDatabase()),
                new GameRepository(mongoConfig.getDatabase()),
                new ClientRepository(mongoConfig.getDatabase()),
                new InactiveRentRepository(mongoConfig.getDatabase()));

        Rent rent = new Rent(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(9), client, game, 0);
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
        RentManager rentManager = new RentManager(mongoConfig.getMongoClient(),
                new RentRepository(mongoConfig.getDatabase()),
                new GameRepository(mongoConfig.getDatabase()),
                new ClientRepository(mongoConfig.getDatabase()),
                new InactiveRentRepository(mongoConfig.getDatabase()));

        Rent rent = new Rent(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(9), client, game, 0);

        rentManager.createRent(rent);

        assertTrue(rentManager.findRentById(rent.getId()).isPresent());
        assertEquals(1, gameManager.findGameById(game.getId()).get().getRentalStatusCount());

        Rent rent2 = new Rent(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(9), client, game, 0);

        assertThrows(IllegalStateException.class, () -> rentManager.createRent(rent2));
        assertTrue(rentManager.findRentById(rent2.getId()).isEmpty());
        assertEquals(1, gameManager.findGameById(game.getId()).get().getRentalStatusCount());
        assertEquals(1, clientManager.findClientById(client.getId()).get().getRentalCount());

    }

    @Test
    public void endRentTest() {
        RentManager rentManager = new RentManager(mongoConfig.getMongoClient(),
                new RentRepository(mongoConfig.getDatabase()),
                new GameRepository(mongoConfig.getDatabase()),
                new ClientRepository(mongoConfig.getDatabase()),
                new InactiveRentRepository(mongoConfig.getDatabase()));
        Rent rent = new Rent(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(2), client, game, 0);
        rentManager.createRent(rent);

        assertTrue(rentManager.completeRent(rent.getId()));
        assertTrue(rentManager.findRentById(rent.getId()).isEmpty());
        assertTrue(inactiveRentManager.findInactiveRentById(rent.getId()).isPresent());
        assertEquals(game.getPricePerDay(), inactiveRentManager.findInactiveRentById(rent.getId()).get().getRentalPrice());
    }

    @Test
    public void extendedRentTest() {
        RentManager rentManager = new RentManager(mongoConfig.getMongoClient(),
                new RentRepository(mongoConfig.getDatabase()),
                new GameRepository(mongoConfig.getDatabase()),
                new ClientRepository(mongoConfig.getDatabase()),
                new InactiveRentRepository(mongoConfig.getDatabase()));
        Rent rent = new Rent(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(9), client, game, 0);

        rentManager.createRent(rent);

        assertTrue(rentManager.updateRentalEndDate(rent.getId(), LocalDate.now().plusDays(14)));
        assertTrue(rentManager.findRentById(rent.getId()).isPresent());
        assertEquals(LocalDate.now().plusDays(14), rentManager.findRentById(rent.getId()).get().getEndDate());
        assertEquals(150, rentManager.findRentById(rent.getId()).get().getRentalPrice());
    }

    @Test
    public void endRentDateBeforeStart() {
        RentManager rentManager = new RentManager(mongoConfig.getMongoClient(),
                new RentRepository(mongoConfig.getDatabase()),
                new GameRepository(mongoConfig.getDatabase()),
                new ClientRepository(mongoConfig.getDatabase()),
                new InactiveRentRepository(mongoConfig.getDatabase()));
        Rent rent = new Rent(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(9), client, game, 0);
        rentManager.createRent(rent);

        assertFalse(rentManager.updateRentalEndDate(rent.getId(), LocalDate.now().minusDays(1)));
    }

    @Test
    void encodeTest() {
        RentCodec codec = new RentCodec(new ClientCodec(), new GameCodec());

        BsonDocument document = new BsonDocument();
        BsonWriter writer = new BsonDocumentWriter(document);
        Rent rent = new Rent(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(9), client, game, 0);
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

        Game newGame = new BoardGame(UUID.randomUUID(), "Uno", GameType.BOARD_GAME,2 ,0, 2, 6);
        gameManager.insertGame(newGame);


        ClientManager clientManager = new ClientManager(new ClientRepository(mongoConfig.getDatabase()));

        Client newClient = new Client(UUID.randomUUID(),  "John", "WDoe", "123 Main St", 0);
        clientManager.insertClient(newClient);
        Client newClient2 = new Client(UUID.randomUUID(), "Jane", "WDoe", "123 Main St", 0);
        clientManager.insertClient(newClient2);


        RentManager rentManager = new RentManager(mongoConfig.getMongoClient(),
                new RentRepository(mongoConfig.getDatabase()),
                new GameRepository(mongoConfig.getDatabase()),
                new ClientRepository(mongoConfig.getDatabase()),
                new InactiveRentRepository(mongoConfig.getDatabase()));

        RentManager rentManager2 = new RentManager(mongoConfig.getMongoClient(),
                new RentRepository(mongoConfig.getDatabase()),
                new GameRepository(mongoConfig.getDatabase()),
                new ClientRepository(mongoConfig.getDatabase()),
                new InactiveRentRepository(mongoConfig.getDatabase()));


        AtomicBoolean exceptionCaught = new AtomicBoolean(false);

        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(2);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable rentTask1 = () -> {
            try {
                latch.await();
                Rent rent = new Rent(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(9), newClient, newGame, 0);
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

                Rent rent2 = new Rent(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(9), newClient2, newGame, 0);
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
