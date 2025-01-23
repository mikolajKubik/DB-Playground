package edu.kdmk.model.game;

import edu.kdmk.model.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@ToString(callSuper = true)
public abstract class Game extends AbstractEntity {
    @Getter @Setter
    private String name;

    @Getter
    private final GameType type;

    @Getter @Setter
    private int pricePerDay;

    @Getter @Setter
    private int rentalStatusCount;

    public Game(UUID id, String name, GameType type, int pricePerDay, int rentalStatusCount) {
        super(id);
        this.name = name;
        this.type = type;
        this.pricePerDay = pricePerDay;
        this.rentalStatusCount = rentalStatusCount;
    }

    public Game(String name, GameType gameType, int pricePerDay) {
        super(); // UUID auto-assigned
        this.name = name;
        this.type = gameType;
        this.pricePerDay = pricePerDay;
    }
}