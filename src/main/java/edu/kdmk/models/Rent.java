package edu.kdmk.models;

import edu.kdmk.models.game.Game;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@ToString(callSuper = true)

public class Rent extends AbstractEntity {
    private LocalDate startDate;

    private LocalDate endDate;

    private Client client;

    private Game game;

    private int rentalPrice;

    public Rent() {
        super();
    }

    public Rent(UUID id, LocalDate startDate, LocalDate endDate, Client client, Game game, int rentalPrice) {
        super(id);
        this.startDate = startDate;
        this.endDate = endDate;
        this.client = client;
        this.game = game;
        this.rentalPrice = rentalPrice;
    }
}