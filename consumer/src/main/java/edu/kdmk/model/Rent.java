package edu.kdmk.model;

import edu.kdmk.model.game.Game;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

public class Rent extends AbstractEntity {
    @Getter @Setter
    private String rentalCompanyName;

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

    public Rent(LocalDate startDate, LocalDate endDate, Client client, Game game, String rentalCompanyName) {
        super(); // UUID is auto-assigned
        this.startDate = startDate;
        this.endDate = endDate;
        this.client = client;
        this.game = game;
        this.rentalPrice = 0;
        this.rentalCompanyName = rentalCompanyName;
    }

    public Rent(UUID id, LocalDate startDate, LocalDate endDate, Client client, Game game, int rentalPrice, String rentalCompanyName) {
        super(id); // Use provided UUID
        this.startDate = startDate;
        this.endDate = endDate;
        this.client = client;
        this.game = game;
        this.rentalPrice = rentalPrice;
        this.rentalCompanyName = rentalCompanyName;
    }

    @Override
    public String toString() {
        return "Rent{" +
                "id=" + getId() +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", client=" + client +
                ", game=" + game +
                ", rentalPrice=" + rentalPrice +
                '}';
    }
}