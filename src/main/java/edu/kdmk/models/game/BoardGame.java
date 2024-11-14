package edu.kdmk.models.game;

import lombok.Getter;

import java.util.UUID;

@Getter
public class BoardGame extends Game {
    private final int minPlayers;
    private final int maxPlayers;

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
}