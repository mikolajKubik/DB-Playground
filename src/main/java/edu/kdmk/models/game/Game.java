package edu.kdmk.models.game;

import edu.kdmk.models.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@ToString(callSuper = true)
public abstract class Game extends AbstractEntity {
    @Getter @Setter
    private String name;

    private GameType type;

    private int pricePerDay;

    @Getter @Setter
    private int rentalStatusCount;

    public Game() {
    }

    public Game(UUID id, String name, GameType type, int pricePerDay, int rentalStatusCount) {
        super(id);
        this.name = name;
        this.type = type;
        this.pricePerDay = pricePerDay;
        this.rentalStatusCount = rentalStatusCount;
    }

}