package edu.kdmk.game;

import edu.kdmk.game.model.Game;

import java.util.UUID;

public class GameManager {

    private final GameDao gameDao;

    public GameManager(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    public void saveGame(Game game) {
        if (game == null || game.getGameId() == null) {
            throw new RuntimeException();
        }
        gameDao.save(game);
    }

    public Game findGameById(UUID gameId) {
        if (gameId == null) {
            throw new RuntimeException();
        }
        return gameDao.findById(gameId);
    }

    public void deleteGame(Game game) {
        if (game == null || game.getGameId() == null) {
            throw new RuntimeException();
        }
        gameDao.delete(game);
    }

    public void updateGame(Game game) {
        if (game == null || game.getGameId() == null) {
            throw new RuntimeException();
        }
        gameDao.update(game);
    }

}
