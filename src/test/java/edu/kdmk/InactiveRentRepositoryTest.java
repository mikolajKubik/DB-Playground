package edu.kdmk;

import edu.kdmk.config.MongoConfig;
import edu.kdmk.managers.ClientManager;
import edu.kdmk.managers.GameManager;
import edu.kdmk.managers.InactiveRentManager;
import edu.kdmk.models.Client;
import edu.kdmk.models.Rent;
import edu.kdmk.models.game.BoardGame;
import edu.kdmk.models.game.GameType;
import edu.kdmk.repositories.ClientRepository;
import edu.kdmk.repositories.GameRepository;
import edu.kdmk.repositories.InactiveRentRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class InactiveRentRepositoryTest {

    private static MongoConfig mongoConfig;
    private static GameManager gameManager;
    private static ClientManager clientManager;
    Client client;
    BoardGame game;


    @BeforeAll
    static void setup() {
        mongoConfig = new MongoConfig();
        clientManager = new ClientManager(new ClientRepository(mongoConfig.getDatabase()));
        gameManager = new GameManager(new GameRepository(mongoConfig.getDatabase()));
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
        game = new BoardGame(UUID.randomUUID(), "Uno2", GameType.BOARD_GAME,10, 0, 6, 8);
        gameManager.insertGame(game);
        client = new Client(UUID.randomUUID(), "John", "Doe", "123 Main St", 0);
        clientManager.insertClient(client);
    }

    @Test
    public void insertRentTest() {
        InactiveRentManager inactiveRentManager = new InactiveRentManager(new InactiveRentRepository(mongoConfig.getDatabase()));

        Rent rent = new Rent(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(9), client, game, 9);

        assertTrue(inactiveRentManager.createInactiveRent(rent));
        assertTrue(inactiveRentManager.findInactiveRentById(rent.getId()).isPresent());
        assertInstanceOf(Rent.class, inactiveRentManager.findInactiveRentById(rent.getId()).get());
    }

    @Test
    public void deleteRentTest() {
        InactiveRentManager inactiveRentManager = new InactiveRentManager(new InactiveRentRepository(mongoConfig.getDatabase()));

        Rent rent = new Rent(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(9), client, game, 12);

        assertTrue(inactiveRentManager.createInactiveRent(rent));

        assertTrue(inactiveRentManager.findInactiveRentById(rent.getId()).isPresent());
        assertInstanceOf(Rent.class, inactiveRentManager.findInactiveRentById(rent.getId()).get());

        inactiveRentManager.deleteInactiveRent(rent);

        assertTrue(inactiveRentManager.findInactiveRentById(rent.getId()).isEmpty());
    }
}