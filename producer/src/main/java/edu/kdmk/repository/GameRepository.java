package edu.kdmk.repository;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.model.game.Game;
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

    public boolean insert(Game game) {
        return gameCollection.insertOne(game).wasAcknowledged();
    }

    public boolean insert(ClientSession session, Game game) {
        return gameCollection.insertOne(session, game).wasAcknowledged();
    }

    public Optional<Game> findById(UUID id) {
        Document filter = new Document("_id", id.toString());
        return Optional.ofNullable(gameCollection.find(filter).first());
    }

    public Optional<Game> findById(ClientSession session, UUID id) {
        Document filter = new Document("_id", id.toString());
        return Optional.ofNullable(gameCollection.find(session, filter).first());
    }

    public boolean update(Game updatedGame) {
        Document filter = new Document("_id", updatedGame.getId().toString());
        return gameCollection.replaceOne(filter, updatedGame).wasAcknowledged();
    }

    public boolean updateById(ClientSession session, Game updatedGame) {
        Document filter = new Document("_id", updatedGame.getId().toString());
        return gameCollection.replaceOne(session, filter, updatedGame).wasAcknowledged();
    }

    public boolean deleteById(UUID id) {
        Document filter = new Document("_id", id.toString());
        return gameCollection.deleteOne(filter).wasAcknowledged();
    }

    public boolean deleteById(ClientSession session, UUID id) {
        Document filter = new Document("_id", id.toString());
        return gameCollection.deleteOne(session, filter).wasAcknowledged();
    }

    public List<Game> findAll() {
        return StreamSupport.stream(gameCollection.find().spliterator(), false)
                .collect(Collectors.toList());
    }

    // Atomic increment to mark as rented
    // https://medium.com/@codersauthority/handling-race-conditions-and-concurrent-resource-updates-in-node-and-mongodb-by-performing-atomic-9f1a902bd5fa
    public boolean markAsRented(ClientSession session, UUID gameId) {
        Bson filter = and(eq("_id", gameId.toString()), eq("rentalStatusCount", 0)); // Ensure game is not rented
        Bson update = inc("rentalStatusCount", 1); // Increment rental status count by 1

        Document updatedGame = gameCollection.withDocumentClass(Document.class)
                .findOneAndUpdate(session, filter, update);

        return updatedGame != null; // Returns true if update was successful, false if not
    }

    // Atomic decrement to unmark as rented
    // https://medium.com/@codersauthority/handling-race-conditions-and-concurrent-resource-updates-in-node-and-mongodb-by-performing-atomic-9f1a902bd5fa
    public boolean unmarkAsRented(ClientSession session, UUID gameId) {
        Bson filter = and(eq("_id", gameId.toString()), eq("rentalStatusCount", 1)); // Ensure game is rented by one renter
        Bson update = inc("rentalStatusCount", -1); // Decrement rental status count by 1

        Document updatedGame = gameCollection.withDocumentClass(Document.class)
                .findOneAndUpdate(session, filter, update);
        return updatedGame != null; // Returns true if update was successful, false if not
    }

}