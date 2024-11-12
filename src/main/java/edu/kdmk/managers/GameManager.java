package edu.kdmk.managers;


import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.TransactionBody;
import edu.kdmk.models.game.Game;
import edu.kdmk.repositories.GameRepository;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.UUID;

public class GameManager {
    private final MongoClient mongoClient;
    private final GameRepository gameRepository;

    public GameManager(MongoClient mongoClient, MongoDatabase database) {
        this.mongoClient = mongoClient;
        this.gameRepository = new GameRepository(database);
    }

    // Transactional insert for a new Game
    public void insertGame(Game newGame) {
        try (ClientSession session = mongoClient.startSession()) {
            session.withTransaction((TransactionBody<Void>) () -> {
                gameRepository.insert(session, newGame);
                return null;
            });
        } catch (RuntimeException e) {
            System.err.println("Transaction aborted due to an error: " + e.getMessage());
        }
    }

    // Transactional find by UUID
    public Game findGameById(UUID id) {
        try (ClientSession session = mongoClient.startSession()) {
            return gameRepository.findById(session, id);
        }
    }

    // Transactional update of a Game, relying on UUID within Game object
    public void updateGameById(Game updatedGame) {
        try (ClientSession session = mongoClient.startSession()) {
            session.withTransaction((TransactionBody<Void>) () -> {
                gameRepository.updateById(session, updatedGame);
                return null;
            });
        } catch (RuntimeException e) {
            System.err.println("Transaction aborted due to an error: " + e.getMessage());
        }
    }

    // Transactional deletion of a Game by UUID
    public void deleteGameById(UUID gameId) {
        try (ClientSession session = mongoClient.startSession()) {
            session.withTransaction((TransactionBody<Void>) () -> {
                gameRepository.deleteById(session, gameId);
                return null;
            });
        } catch (RuntimeException e) {
            System.err.println("Transaction aborted due to an error: " + e.getMessage());
        }
    }

    // Transactional retrieval of all games
    public List<Game> getAllGames() {
        try (ClientSession session = mongoClient.startSession()) {
            return gameRepository.findAll(session);
        }
    }
}