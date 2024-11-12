package edu.kdmk.repositories;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.models.game.Game;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GameRepository {
    private final MongoCollection<Game> gameCollection;

    public GameRepository(MongoDatabase database) {
        this.gameCollection = database.getCollection("games", Game.class);
    }

    // Insert a new Game
    public void insert(ClientSession session, Game game) {
        gameCollection.insertOne(session, game);
    }

    // Find a Game by its UUID
    public Game findById(ClientSession session, UUID id) {
        Document filter = new Document("id", id.toString());
        return gameCollection.find(session, filter).first();
    }

    // Update a Game by its UUID, using the UUID from the Game object
    public void updateById(ClientSession session, Game updatedGame) {
        Document filter = new Document("id", updatedGame.getId().toString());
        gameCollection.replaceOne(session, filter, updatedGame);
    }

    // Delete a Game by its UUID
    public void deleteById(ClientSession session, UUID id) {
        Document filter = new Document("id", id.toString());
        gameCollection.deleteOne(session, filter);
    }

    // Retrieve all games
    public List<Game> findAll(ClientSession session) {
        return StreamSupport.stream(gameCollection.find(session).spliterator(), false)
                .collect(Collectors.toList());
    }
}