package edu.kdmk;

import edu.kdmk.config.MongoConfig;
import edu.kdmk.managers.ClientManager;
import edu.kdmk.managers.GameManager;
import edu.kdmk.managers.InactiveRentManager;
import edu.kdmk.models.Client;
import edu.kdmk.models.Rent;
import edu.kdmk.models.game.BoardGame;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class InactiveRentRepository {

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
        clientManager = new ClientManager(mongoConfig.getDatabase());
        gameManager = new GameManager(mongoConfig.getDatabase());
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
        InactiveRentManager inactiveRentManager = new InactiveRentManager(mongoConfig.getDatabase());

        Rent rent = new Rent(LocalDate.now(), LocalDate.now().plusDays(9), client, game);

        assertTrue(inactiveRentManager.createInactiveRent(rent));
        assertTrue(inactiveRentManager.findInactiveRentById(rent.getId()).isPresent());
        assertInstanceOf(Rent.class, inactiveRentManager.findInactiveRentById(rent.getId()).get());
    }

    @Test
    public void deleteRentTest() {
        InactiveRentManager inactiveRentManager = new InactiveRentManager(mongoConfig.getDatabase());

        Rent rent = new Rent(LocalDate.now(), LocalDate.now().plusDays(9), client, game);

        assertTrue(inactiveRentManager.createInactiveRent(rent));

        assertTrue(inactiveRentManager.findInactiveRentById(rent.getId()).isPresent());
        assertInstanceOf(Rent.class, inactiveRentManager.findInactiveRentById(rent.getId()).get());

        inactiveRentManager.deleteInactiveRent(rent);

        assertTrue(inactiveRentManager.findInactiveRentById(rent.getId()).isEmpty());
    }
}