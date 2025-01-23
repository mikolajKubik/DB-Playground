package edu.kdmk.model.game;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@ToString(callSuper = true)
public class BoardGame extends Game {
    @Getter
    private final int minPlayers;

    @Getter
    private final int maxPlayers;

    public BoardGame(UUID id, String name, int pricePerDay, int rentalStatusCount, int minPlayers, int maxPlayers) {
        super(id, name, GameType.BOARD_GAME, pricePerDay, rentalStatusCount);
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    public BoardGame(String name, int pricePerDay, int minPlayers, int maxPlayers) {
        super(name, GameType.BOARD_GAME, pricePerDay);
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }
}