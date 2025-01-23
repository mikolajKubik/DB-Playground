package edu.kdmk.managers;

import com.mongodb.client.MongoDatabase;
import edu.kdmk.models.game.Game;
import edu.kdmk.repositories.GameRepository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public class GameManager {
    private final GameRepository gameRepository;

    public GameManager(MongoDatabase database) {
        this.gameRepository = new GameRepository(database);
    }

    public boolean insertGame(Game newGame) {
        try {
           return gameRepository.insert(newGame);
        } catch (Exception e) {
            throw e;
        }
    }

    public Optional<Game> findGameById(UUID id) {
        try {
            return gameRepository.findById(id);
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean updateGame(Game updatedGame) {
        try {
            return gameRepository.update(updatedGame);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public boolean deleteGameById(UUID gameId) {
        try {
            return gameRepository.deleteById(gameId);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public List<Game> getAllGames() {
        try {
            return gameRepository.findAll();
        } catch (Exception e) {
            throw e;
        }
    }
}