package edu.kdmk.manager;

import edu.kdmk.model.game.Game;
import edu.kdmk.repository.GameRepository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public class GameManager {
    private final GameRepository gameRepository;

    public GameManager(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
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