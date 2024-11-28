package edu.kdmk;

import edu.kdmk.config.MongoConfig;
import edu.kdmk.config.RedisConfig;
import edu.kdmk.managers.GameManager;
import edu.kdmk.models.game.BoardGame;
import edu.kdmk.models.game.ComputerGame;
import edu.kdmk.models.game.GameType;
import edu.kdmk.repositories.cache.CachedGameRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class CachedGameRepositoryTest {

    private static RedisConfig redisConfig;
    private static MongoConfig mongoConfig;
    BoardGame boardGame;
    ComputerGame computerGame;

    @BeforeAll
    static void setup() {


        mongoConfig = new MongoConfig();
        redisConfig = new RedisConfig();

    }

    @AfterAll
    static void tearDown() {
        try {
            if (mongoConfig != null) {
                mongoConfig.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setupGames() {
        boardGame = new BoardGame(UUID.randomUUID(),"Chinese board game", GameType.BOARD_GAME, 5, 0, 4, 5);
        computerGame = new ComputerGame(UUID.randomUUID(),"Cyberpunk 2077", GameType.COMPUTER_GAME, 5, 0,"PC");
    }

    @Test
    void insertGameTest() {
        GameManager gameManager = new GameManager(new CachedGameRepository(mongoConfig.getDatabase(), redisConfig.getRedisJsonClient()));

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
        GameManager gameManager = new GameManager(new CachedGameRepository(mongoConfig.getDatabase(), redisConfig.getRedisJsonClient()));

        assertTrue(gameManager.insertGame(boardGame));
        assertTrue(gameManager.deleteGameById(boardGame.getId()));
        assertTrue(gameManager.findGameById(boardGame.getId()).isEmpty());

        assertTrue(gameManager.insertGame(computerGame));
        assertTrue(gameManager.deleteGameById(computerGame.getId()));
        assertTrue(gameManager.findGameById(computerGame.getId()).isEmpty());
    }

    @Test
    void deleteNonExistingGame() {
        GameManager gameManager = new GameManager(new CachedGameRepository(mongoConfig.getDatabase(), redisConfig.getRedisJsonClient()));

        assertTrue(gameManager.findGameById(boardGame.getId()).isEmpty());
        assertTrue(gameManager.deleteGameById(boardGame.getId()));

        assertTrue(gameManager.findGameById(computerGame.getId()).isEmpty());
        assertTrue(gameManager.deleteGameById(computerGame.getId()));
    }

    @Test
    void updateGameTest() {
        GameManager gameManager = new GameManager(new CachedGameRepository(mongoConfig.getDatabase(), redisConfig.getRedisJsonClient()));

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
