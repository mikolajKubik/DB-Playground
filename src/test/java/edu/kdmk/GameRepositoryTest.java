package edu.kdmk;

import edu.kdmk.config.MongoConfig;
import edu.kdmk.managers.GameManager;
import edu.kdmk.models.codec.GameCodec;
import edu.kdmk.models.game.BoardGame;
import edu.kdmk.models.game.ComputerGame;
import edu.kdmk.models.game.Game;
import edu.kdmk.models.game.GameType;
import org.bson.*;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
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
    void setupGames() {
        boardGame = new BoardGame("Chinese board game", 5, 2, 4);
        computerGame = new ComputerGame("Cyberpunk 2077", 5, "PC");
    }

    @Test
    void insertGameTest() {
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

    @Test
    void encodeTest() {
        GameCodec codec = new GameCodec();

        BsonDocument boardGameDocument = new BsonDocument();
        BsonWriter writer = new BsonDocumentWriter(boardGameDocument);
        codec.encode(writer, boardGame, EncoderContext.builder().build());

        assertEquals(boardGame.getId().toString(), boardGameDocument.getString("_id").getValue());
        assertEquals(boardGame.getName(), boardGameDocument.getString("name").getValue());
        assertEquals(boardGame.getType().getTypeName(), boardGameDocument.getString("gameType").getValue());
        assertEquals(boardGame.getPricePerDay(), boardGameDocument.getInt32("pricePerDay").getValue());
        assertEquals(boardGame.getMinPlayers(), boardGameDocument.getInt32("minPlayers").getValue());
        assertEquals(boardGame.getMaxPlayers(), boardGameDocument.getInt32("maxPlayers").getValue());

        BsonDocument computerGameDocument = new BsonDocument();
        writer = new BsonDocumentWriter(computerGameDocument);
        codec.encode(writer, computerGame, EncoderContext.builder().build());

        assertEquals(computerGame.getId().toString(), computerGameDocument.getString("_id").getValue());
        assertEquals(computerGame.getName(), computerGameDocument.getString("name").getValue());
        assertEquals(computerGame.getType().getTypeName(), computerGameDocument.getString("gameType").getValue());
        assertEquals(computerGame.getPricePerDay(), computerGameDocument.getInt32("pricePerDay").getValue());
        assertEquals(computerGame.getPlatform(), computerGameDocument.getString("platform").getValue());

    }

    @Test
    void decodeTest() {
        GameCodec codec = new GameCodec();

        BsonDocument boardGameDocument = new BsonDocument()
                .append("_id", new BsonString(boardGame.getId().toString()))
                .append("name", new BsonString(boardGame.getName()))
                .append("gameType", new BsonString(boardGame.getType().getTypeName()))
                .append("pricePerDay", new BsonInt32(boardGame.getPricePerDay()))
                .append("minPlayers", new BsonInt32(boardGame.getMinPlayers()))
                .append("maxPlayers", new BsonInt32(boardGame.getMaxPlayers()));

        BsonReader reader = new BsonDocumentReader(boardGameDocument);
        BoardGame decodedBoardGame = (BoardGame) codec.decode(reader, DecoderContext.builder().build());

        assertEquals(boardGame.getId(), decodedBoardGame.getId());
        assertEquals(boardGame.getName(), decodedBoardGame.getName());
        assertEquals(boardGame.getType(), decodedBoardGame.getType());
        assertEquals(boardGame.getMinPlayers(), decodedBoardGame.getMinPlayers());
        assertEquals(boardGame.getMaxPlayers(), decodedBoardGame.getMaxPlayers());


        BsonDocument computerGameDocument = new BsonDocument()
                .append("_id", new BsonString(computerGame.getId().toString()))
                .append("name", new BsonString(computerGame.getName()))
                .append("gameType", new BsonString(computerGame.getType().getTypeName()))
                .append("pricePerDay", new BsonInt32(computerGame.getPricePerDay()))
                .append("platform", new BsonString(computerGame.getPlatform()));

        reader = new BsonDocumentReader(computerGameDocument);
        ComputerGame decodedComputerGame = (ComputerGame) codec.decode(reader, DecoderContext.builder().build());

        assertEquals(computerGame.getId(), decodedComputerGame.getId());
        assertEquals(computerGame.getName(), decodedComputerGame.getName());
        assertEquals(computerGame.getType(), decodedComputerGame.getType());
        assertEquals(computerGame.getPlatform(), decodedComputerGame.getPlatform());
    }
}
