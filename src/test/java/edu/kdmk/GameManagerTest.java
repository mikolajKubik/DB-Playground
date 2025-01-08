package edu.kdmk;

import edu.kdmk.client.Client;
import edu.kdmk.client.ClientMapper;
import edu.kdmk.client.ClientMapperBuilder;
import edu.kdmk.config.CassandraConnector;
import edu.kdmk.config.CassandraSchemaCreator;
import edu.kdmk.game.GameManager;
import edu.kdmk.game.GameMapper;
import edu.kdmk.game.GameMapperBuilder;
import edu.kdmk.game.model.BoardGame;
import edu.kdmk.game.model.ComputerGame;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameManagerTest {
    private static CassandraConnector connector;
    private static GameMapper mapper;

    private ComputerGame computerGame;
    private BoardGame boardGame;
    private UUID gameId1;
    private UUID gameId2;

    @BeforeAll
    static void setup() {
        connector = new CassandraConnector();

        CassandraSchemaCreator schemaCreator = new CassandraSchemaCreator(connector.getSession());
        schemaCreator.createClientsTable("rent_a_game");
        schemaCreator.createGamesTable("rent_a_game");
        schemaCreator.createRentByClientTable("rent_a_game");
        schemaCreator.createRentByGameTable("rent_a_game");

        mapper = new GameMapperBuilder(connector.getSession()).build();
    }

    @AfterAll
    static void tearDown(){
        try {
            connector.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setupClient() {
        gameId1 = UUID.randomUUID();
        gameId2 = UUID.randomUUID();
        computerGame = new ComputerGame(gameId1, "Mortal Kombat", "computer_game", false, 5, "PC");
        boardGame = new BoardGame(gameId2, "Monopoly", "board_game", false, 10, 2, 6);
    }

    @Test
    void saveGameTest() {
        GameManager gameManager = new GameManager(mapper.gameDao("rent_a_game", "games"));

        gameManager.saveGame(computerGame);
        gameManager.saveGame(boardGame);

        assertEquals(computerGame, gameManager.findGameById(gameId1));
        assertEquals(boardGame, gameManager.findGameById(gameId2));

    }

    @Test
    void saveGameNullTest() {
        GameManager gameManager = new GameManager(mapper.gameDao("rent_a_game", "games"));

        assertThrows(RuntimeException.class, () -> gameManager.saveGame(null));
    }

    @Test
    void deleteGameTest() {
        GameManager gameManager = new GameManager(mapper.gameDao("rent_a_game", "games"));

        gameManager.saveGame(computerGame);
        gameManager.saveGame(boardGame);

        assertEquals(computerGame, gameManager.findGameById(gameId1));
        assertEquals(boardGame, gameManager.findGameById(gameId2));

        gameManager.deleteGame(computerGame);
        gameManager.deleteGame(boardGame);

        assertNull(gameManager.findGameById(gameId1));
        assertNull(gameManager.findGameById(gameId2));

    }

    @Test
    void updateGameTest() {
        GameManager gameManager = new GameManager(mapper.gameDao("rent_a_game", "games"));

        gameManager.saveGame(computerGame);
        gameManager.saveGame(boardGame);

        assertEquals(computerGame, gameManager.findGameById(gameId1));
        assertEquals(boardGame, gameManager.findGameById(gameId2));

        computerGame.setName("Updated Mortal Kombat");
        boardGame.setName("Updated Monopoly");

        gameManager.updateGame(computerGame);
        gameManager.updateGame(boardGame);

        assertEquals(computerGame.getName(), gameManager.findGameById(gameId1).getName());
        assertEquals(boardGame.getName(), gameManager.findGameById(gameId2).getName());

    }


}
