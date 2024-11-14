package edu.kdmk.models.game;

import edu.kdmk.models.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


public abstract class Game extends AbstractEntity {
    @Getter @Setter
    private String name;

    @Getter
    private final GameType type;

    @Getter @Setter
    private int rentalStatusCount;

    public Game(UUID id, String name, GameType type) {
        super(id);
        this.name = name;
        this.type = type;
    }

    public Game(String name, GameType gameType) {
        super(); // UUID auto-assigned
        this.name = name;
        this.type = gameType;
    }
}