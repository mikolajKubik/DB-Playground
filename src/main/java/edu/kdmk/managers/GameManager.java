package edu.kdmk.managers;

import edu.kdmk.config.MongoDBConnection;
import edu.kdmk.models.game.Game;
import edu.kdmk.repositories.GameRepository;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class GameManager {
    private final MongoDBConnection mongoDBConnection;
    private final GameRepository gameRepository;

    public Optional<Game> insertGame(Game game) {
        var session = mongoDBConnection.startSession();
        Optional<Game> result;
        try {
            session.startTransaction();

            if (gameRepository.insertGame(session, game)) {
                result = Optional.of(game);
            } else {
                result = Optional.empty();
            }

            session.commitTransaction();
        } catch (Exception e) {
            session.abortTransaction();
            throw e;
        } finally {
            session.close();
        }
        return result;
    }

    public boolean removeGame(Game game) {
        var session = mongoDBConnection.startSession();
        boolean result;
        try {
            session.startTransaction();

            result = gameRepository.deleteGame(session, game);

            session.commitTransaction();
        } catch (Exception e) {
            session.abortTransaction();
            throw e;
        } finally {
            session.close();
        }
        return result;
    }

    public Optional<Game> getGameById(UUID id) {
        var session = mongoDBConnection.startSession();
        Optional<Game> result;
        try {
            session.startTransaction();

            result = gameRepository.getGame(id);

            session.commitTransaction();
        } catch (Exception e) {
            session.abortTransaction();
            throw e;
        } finally {
            session.close();
        }
        return result;
    }
    // trzeba zrobic dobrze, gdzies trzeba incrementa zrobic chyba albo metode start rent czy cos co podbija o 1 i end rent co zmniejsza o 1
    public Optional<Game> updateGame(Game game) {
        var session = mongoDBConnection.startSession();
        Optional<Game> result;
        try {
            session.startTransaction();

            if (gameRepository.updateGame(session, game)) {
                result = Optional.of(game);
            } else {
                result = Optional.empty();
            }

            session.commitTransaction();
        } catch (Exception e) {
            session.abortTransaction();
            throw e;
        } finally {
            session.close();
        }
        return result;
    }
}
