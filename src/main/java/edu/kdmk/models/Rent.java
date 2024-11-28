package edu.kdmk.models;

import edu.kdmk.models.game.Game;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

public class Rent extends AbstractEntity {
    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private Client client;

    @Getter @Setter
    private Game game;

    @Getter @Setter
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