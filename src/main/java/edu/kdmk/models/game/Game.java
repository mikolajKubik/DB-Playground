package edu.kdmk.models.game;

import edu.kdmk.models.AbstractEntity;

import java.util.UUID;

public abstract class Game extends AbstractEntity {
    private String name;
    private GameType type;

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

    public String getName() {
        return name;
    }

    public GameType getGameType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }
}