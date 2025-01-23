package edu.kdmk;

import edu.kdmk.config.MongoConfig;
import edu.kdmk.manager.ClientManager;
import edu.kdmk.manager.GameManager;
import edu.kdmk.manager.InactiveRentManager;
import edu.kdmk.model.Client;
import edu.kdmk.model.Rent;
import edu.kdmk.model.game.BoardGame;
import edu.kdmk.repository.ClientRepository;
import edu.kdmk.repository.GameRepository;
import edu.kdmk.repository.InactiveRentRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class InactiveRentRepositoryTest {

    private static MongoConfig mongoConfig;
    private static GameManager gameManager;
    private static ClientManager clientManager;
    Client client;
    BoardGame game;


    @BeforeAll
    static void setup() {
        String connectionString = ConnectionStringProvider.getConnectionString();
        String databaseName = "ndb";

        mongoConfig = new MongoConfig(connectionString, databaseName);
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
        game = new BoardGame("Uno2", 10, 2, 6);
        gameManager.insertGame(game);
        client = new Client("John", "Doe", "123 Main St");
        clientManager.insertClient(client);
    }

    @Test
    public void insertRentTest() {
        InactiveRentManager inactiveRentManager = new InactiveRentManager(new InactiveRentRepository(mongoConfig.getDatabase()));

        Rent rent = new Rent(LocalDate.now(), LocalDate.now().plusDays(9), client, game, "KDMK");

        assertTrue(inactiveRentManager.createInactiveRent(rent));
        assertTrue(inactiveRentManager.findInactiveRentById(rent.getId()).isPresent());
        assertInstanceOf(Rent.class, inactiveRentManager.findInactiveRentById(rent.getId()).get());
    }

    @Test
    public void deleteRentTest() {
        InactiveRentManager inactiveRentManager = new InactiveRentManager(new InactiveRentRepository(mongoConfig.getDatabase()));

        Rent rent = new Rent(LocalDate.now(), LocalDate.now().plusDays(9), client, game, "KDMK");

        assertTrue(inactiveRentManager.createInactiveRent(rent));

        assertTrue(inactiveRentManager.findInactiveRentById(rent.getId()).isPresent());
        assertInstanceOf(Rent.class, inactiveRentManager.findInactiveRentById(rent.getId()).get());

        inactiveRentManager.deleteInactiveRent(rent);

        assertTrue(inactiveRentManager.findInactiveRentById(rent.getId()).isEmpty());
    }
}