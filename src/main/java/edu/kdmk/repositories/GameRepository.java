package edu.kdmk.repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import com.mongodb.client.ClientSession;
import edu.kdmk.models.game.Game;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Optional;
import java.util.UUID;

public class GameRepository {
    private final MongoCollection<Game> gameCollection;

    public GameRepository(MongoCollection<Game> gameCollection) {
        this.gameCollection = gameCollection;
    }

    public boolean insertGame(ClientSession session, Game game) {
        try {
            return gameCollection.insertOne(session, game).wasAcknowledged();
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean deleteGame(ClientSession session, Game game) {
        try {
            Bson filter = Filters.eq("id", game.getUuid());
            return gameCollection.deleteOne(session, filter).wasAcknowledged();
        } catch (Exception e) {
            throw e;
        }
    }

    public Optional<Game> getGame(UUID id) {
        try {
            Bson filter = Filters.eq("id", id.toString());
            System.out.println(gameCollection.find(filter).first());



            return Optional.ofNullable(gameCollection.find(filter).first());
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean updateGame(ClientSession session, Game game) {
        try {
            Bson filter = Filters.eq("id", game.getUuid());
            return gameCollection.replaceOne(session, filter, game).wasAcknowledged();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update game", e);
        }
    }

    public boolean startRent(ClientSession session, UUID gameId) {
        // Filter to find games with the specified ID and that are not currently rented
        Bson filter = Filters.and(
                Filters.eq("id", gameId),
                Filters.eq("isRented", 0)
        );
        Bson update = Updates.set("isRented", 1); // Set to 1 to indicate rented status

        // Use findOneAndUpdate for atomic operation
        Game updatedGame = gameCollection.findOneAndUpdate(
                session,
                filter,
                update,
                new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        );

        // Return true if the update succeeded (i.e., a game was found and updated)
        return updatedGame != null;
    }

    public boolean endRent(ClientSession session, UUID gameId) {
        // Filter to find games with the specified ID and that are currently rented
        Bson filter = Filters.and(
                Filters.eq("id", gameId),
                Filters.eq("isRented", 1)
        );
        Bson update = Updates.set("isRented", 0); // Set to 0 to indicate not rented

        // Use findOneAndUpdate for atomic operation
        Game updatedGame = gameCollection.findOneAndUpdate(
                session,
                filter,
                update,
                new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        );

        // Return true if the update succeeded (i.e., a game was found and updated)
        return updatedGame != null;
    }

//    public boolean startRent(ClientSession session, UUID gameId) {
//        Bson filter = Filters.eq("id", gameId);
//        Bson update = Updates.inc("isRented", 1);
//        return gameCollection.updateOne(session, filter, update).wasAcknowledged();
//    }
//
//    public boolean endRent(ClientSession session, UUID gameId) {
//        Bson filter = Filters.eq("id", gameId);
//        Bson update = Updates.inc("isRented", -1);
//        return gameCollection.updateOne(session, filter, update).wasAcknowledged();
//    }

    // zle jest trzebe przerobic na pewno bo wchodzi tu dziedziczenie
//    public boolean updateGame(ClientSession session, Game game) {
//        try {
//            Bson filter = Filters.eq("id", game.getUuid());
//            Bson update = Updates.combine(
//                    Updates.set("gameName", game.getGameName()),
//                    Updates.set("recommendedAge", game.getRecommendedAge()),
//                    Updates.set("releaseYear", game.getReleaseYear()),
//                    Updates.set("publisher", game.getPublisher())
//            );
//            return gameCollection.updateOne(session, filter, update).wasAcknowledged();
//        } catch (Exception e) {
//            throw e;
//        }
//    }
}
