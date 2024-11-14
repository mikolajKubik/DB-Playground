package edu.kdmk;

import edu.kdmk.config.MongoConfig;
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
    BoardGame boardGame;
    ComputerGame computerGame;

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
        boardGame = new BoardGame("Chinese board game",  2, 4);
        computerGame = new ComputerGame("Cyberpunk 2077", "PC");
    }

    @Test
    void insertGame() {
        GameManager gameManager = new GameManager(mongoConfig.getDatabase());

        assertTrue(gameManager.insertGame(boardGame));
        assertTrue(gameManager.insertGame(computerGame));

        assertTrue(gameManager.findGameById(boardGame.getId()).isPresent());
        assertEquals(boardGame, gameManager.findGameById(boardGame.getId()).get());
        assertInstanceOf(BoardGame.class, gameManager.findGameById(boardGame.getId()).get());

        assertTrue(gameManager.findGameById(computerGame.getId()).isPresent());
        assertEquals(computerGame, gameManager.findGameById(computerGame.getId()).get());
        assertInstanceOf(ComputerGame.class, gameManager.findGameById(computerGame.getId()).get());
    }

    @Test
    void deleteGamePositiveTest() {
        GameManager gameManager = new GameManager(mongoConfig.getDatabase());

        assertTrue(gameManager.insertGame(boardGame));
        assertTrue(gameManager.deleteGameById(boardGame.getId()));
        assertTrue(gameManager.findGameById(boardGame.getId()).isEmpty());

        assertTrue(gameManager.insertGame(computerGame));
        assertTrue(gameManager.deleteGameById(computerGame.getId()));
        assertTrue(gameManager.findGameById(computerGame.getId()).isEmpty());
    }

    @Test
    void deleteNonExistingGame() {
        GameManager gameManager = new GameManager(mongoConfig.getDatabase());

        assertTrue(gameManager.findGameById(boardGame.getId()).isEmpty());
        assertTrue(gameManager.deleteGameById(boardGame.getId()));

        assertTrue(gameManager.findGameById(computerGame.getId()).isEmpty());
        assertTrue(gameManager.deleteGameById(computerGame.getId()));
    }

    @Test
    void updateGameTest() {
        GameManager gameManager = new GameManager(mongoConfig.getDatabase());

        assertTrue(gameManager.insertGame(boardGame));
        boardGame.setName("New Chinese board game");
        assertTrue(gameManager.updateGame(boardGame));
        assertTrue(gameManager.findGameById(boardGame.getId()).isPresent());
        assertEquals(boardGame.getName(), gameManager.findGameById(boardGame.getId()).get().getName());
        assertInstanceOf(BoardGame.class, gameManager.findGameById(boardGame.getId()).get());


        assertTrue(gameManager.insertGame(computerGame));
        computerGame.setName("Cyberpunk 2077: The Game");
        assertTrue(gameManager.updateGame(computerGame));
        assertTrue(gameManager.findGameById(computerGame.getId()).isPresent());
        assertEquals(computerGame.getName(), gameManager.findGameById(computerGame.getId()).get().getName());
        assertInstanceOf(ComputerGame.class, gameManager.findGameById(computerGame.getId()).get());
    }
}
