package edu.kdmk.models.game;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter @Setter

@ToString(callSuper = true)
public class BoardGame extends Game {

    public BoardGame() {
    }

    public BoardGame(UUID id, String name, GameType type, int pricePerDay, int rentalStatusCount, int minPlayers, int maxPlayers) {
        super(id, name, type, pricePerDay, rentalStatusCount);
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    private int minPlayers;

    private int maxPlayers;
}