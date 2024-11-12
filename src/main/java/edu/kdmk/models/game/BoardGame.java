package edu.kdmk.models.game;

import java.util.UUID;

public class BoardGame extends Game {
    private int minPlayers;
    private int maxPlayers;

    public BoardGame(UUID id, String name, int minPlayers, int maxPlayers) {
        super(id, name, GameType.BOARD_GAME);
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    public BoardGame(String name, int minPlayers, int maxPlayers) {
        super(name, GameType.BOARD_GAME); // UUID is auto-assigned
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}