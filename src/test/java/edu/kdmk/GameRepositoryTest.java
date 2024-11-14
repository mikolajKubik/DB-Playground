package edu.kdmk;

import edu.kdmk.config.MongoConfig;
import edu.kdmk.managers.ClientManager;
import edu.kdmk.managers.GameManager;
import edu.kdmk.models.game.BoardGame;
import edu.kdmk.models.game.ComputerGame;
import edu.kdmk.models.game.Game;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameRepositoryTest {
    private static MongoConfig mongoConfig;
    Game game1;
    Game game2;

    @BeforeAll
    static void setup() {
        String connectionString = ConnectionStringProvider.getConnectionString();
        String databaseName = "ndb";

        // Initialize MongoConfig before all tests
        mongoConfig = new MongoConfig(connectionString, databaseName);

        System.out.println("MongoDB connection setup before all Game repository tests");
    }

    @AfterAll
    static void tearDown() {
        try {
            // Close MongoConfig after all tests
            if (mongoConfig != null) {
                mongoConfig.close();
            }
            System.out.println("MongoDB connection closed after all Game repository tests");
        } catch (Exception e) {
            System.out.println("Error closing MongoDB connection");
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setupGames() {
        game1 = new BoardGame("Chinese board game",  2, 4);
        game2 = new ComputerGame("Cyberpunk 2077", "PC");
    }

    @Test
    void insertGame() {
        GameManager gameManager = new GameManager(mongoConfig.getDatabase());

        assertTrue(gameManager.insertGame(game1));
        assertTrue(gameManager.insertGame(game2));

        assertTrue(gameManager.findGameById(game1.getId()).isPresent());
        assertEquals(game1, gameManager.findGameById(game1.getId()).get());
        assertInstanceOf(BoardGame.class, gameManager.findGameById(game1.getId()).get());

        assertTrue(gameManager.findGameById(game2.getId()).isPresent());
        assertEquals(game2, gameManager.findGameById(game2.getId()).get());
        assertInstanceOf(ComputerGame.class, gameManager.findGameById(game2.getId()).get());
    }

    @Test
    void deleteGamePositiveTest() {
        GameManager gameManager = new GameManager(mongoConfig.getDatabase());

        assertTrue(gameManager.insertGame(game1));
        assertTrue(gameManager.deleteGameById(game1.getId()));
        assertTrue(gameManager.findGameById(game1.getId()).isEmpty());

        assertTrue(gameManager.insertGame(game2));
        assertTrue(gameManager.deleteGameById(game2.getId()));
        assertTrue(gameManager.findGameById(game2.getId()).isEmpty());
    }

    @Test
    void deleteNonExistingGame() {
        GameManager gameManager = new GameManager(mongoConfig.getDatabase());

        assertTrue(gameManager.findGameById(game1.getId()).isEmpty());
        assertTrue(gameManager.deleteGameById(game1.getId()));

        assertTrue(gameManager.findGameById(game2.getId()).isEmpty());
        assertTrue(gameManager.deleteGameById(game2.getId()));
    }

    @Test
    void updateGameTest() {
        GameManager gameManager = new GameManager(mongoConfig.getDatabase());

        assertTrue(gameManager.insertGame(game1));
        game1.setName("New Chinese board game");
        assertTrue(gameManager.updateGame(game1));
        assertTrue(gameManager.findGameById(game1.getId()).isPresent());
        assertEquals(game1.getName(), gameManager.findGameById(game1.getId()).get().getName());
        assertInstanceOf(BoardGame.class, gameManager.findGameById(game1.getId()).get());


        assertTrue(gameManager.insertGame(game2));
        game2.setName("Cyberpunk 2077: The Game");
        assertTrue(gameManager.updateGame(game2));
        assertTrue(gameManager.findGameById(game2.getId()).isPresent());
        assertEquals(game2.getName(), gameManager.findGameById(game2.getId()).get().getName());
        assertInstanceOf(ComputerGame.class, gameManager.findGameById(game2.getId()).get());
    }
}
