package edu.kdmk.models.game;

import edu.kdmk.models.AbstractEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter @Setter
@ToString(callSuper = true)
public abstract class Game extends AbstractEntity {
    private String name;

    private GameType type;

    private int pricePerDay;

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