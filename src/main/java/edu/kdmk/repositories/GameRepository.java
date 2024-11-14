package edu.kdmk.repositories;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import edu.kdmk.models.game.Game;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;

public class GameRepository {
    private final MongoCollection<Game> gameCollection;

    public GameRepository(MongoDatabase database) {
        this.gameCollection = database.getCollection("games", Game.class);
    }

    // Insert a new Game
    public boolean insert(Game game) {
        return gameCollection.insertOne(game).wasAcknowledged();
    }

    public void insert(ClientSession session, Game game) {
        gameCollection.insertOne(session, game);
    }

    public Optional<Game> findById(UUID id) {
        Document filter = new Document("_id", id.toString());
        return Optional.ofNullable(gameCollection.find(filter).first());
    }

    // Find a Game by its UUID
    public Game findById(ClientSession session, UUID id) {
        Document filter = new Document("_id", id.toString());
        return gameCollection.find(session, filter).first();
    }

    // Update a Game by its UUID, using the UUID from the Game object
    public boolean update(Game updatedGame) {
        Document filter = new Document("_id", updatedGame.getId().toString());
        return gameCollection.replaceOne(filter, updatedGame).wasAcknowledged();
    }

    // Update a Game by its UUID, using the UUID from the Game object
    public void updateById(ClientSession session, Game updatedGame) {
        Document filter = new Document("_id", updatedGame.getId().toString());
        gameCollection.replaceOne(session, filter, updatedGame);
    }

    // Delete a Game by its UUID
    public boolean deleteById(UUID id) {
        Document filter = new Document("_id", id.toString());
        return gameCollection.deleteOne(filter).wasAcknowledged();
    }

    // Delete a Game by its UUID
    public void deleteById(ClientSession session, UUID id) {
        Document filter = new Document("_id", id.toString());
        gameCollection.deleteOne(session, filter);
    }

    public List<Game> findAll() {
        return StreamSupport.stream(gameCollection.find().spliterator(), false)
                .collect(Collectors.toList());
    }

    public boolean markAsRented(ClientSession session, UUID gameId) {
        Bson filter = and(eq("_id", gameId.toString()), eq("rentalStatusCount", 0)); // Ensure game is not rented
        Bson update = inc("rentalStatusCount", 1); // Increment rental status count by 1
        // Use Document as the return type to match the MongoDB expectations
        Document updatedGame = gameCollection.withDocumentClass(Document.class)
                .findOneAndUpdate(session, filter, update);
        System.out.println("Timestamp: " + java.time.LocalDateTime.now());

        return updatedGame != null; // Returns true if update was successful, false if not
    }

    // Atomic decrement to unmark as rented
    public boolean unmarkAsRented(ClientSession session, UUID gameId) {
        Bson filter = and(eq("_id", gameId.toString()), eq("rentalStatusCount", 1)); // Ensure game is rented by one renter
        Bson update = inc("rentalStatusCount", -1); // Decrement rental status count by 1

        // Use Document as the return type to match the MongoDB expectations
        Document updatedGame = gameCollection.withDocumentClass(Document.class)
                .findOneAndUpdate(session, filter, update);
        return updatedGame != null; // Returns true if update was successful, false if not
    }

}