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
            Bson filter = Filters.eq("id", id);
            return Optional.ofNullable(gameCollection.find(filter).first());
        } catch (Exception e) {
            throw e;
        }
    }

    // zle jest trzebe przerobic na pewno bo wchodzi tu dziedziczenie
    public boolean updateGame(ClientSession session, Game game) {
        try {
            Bson filter = Filters.eq("id", game.getUuid());
            Bson update = Updates.combine(
                    Updates.set("gameName", game.getGameName()),
                    Updates.set("recommendedAge", game.getRecommendedAge()),
                    Updates.set("releaseYear", game.getReleaseYear()),
                    Updates.set("publisher", game.getPublisher())
            );
            return gameCollection.updateOne(session, filter, update).wasAcknowledged();
        } catch (Exception e) {
            throw e;
        }
    }
}
