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

    // Constructor for new Rent with auto-assigned UUID
    public Rent(LocalDate startDate, LocalDate endDate, Client client, Game game) {
        super(); // UUID is auto-assigned
        this.startDate = startDate;
        this.endDate = endDate;
        this.client = client;
        this.game = game;
        this.rentalPrice = 0;
    }

    // Optional constructor to specify UUID (e.g., when loading from DB)
    public Rent(UUID id, LocalDate startDate, LocalDate endDate, Client client, Game game, int rentalPrice) {
        super(id); // Use provided UUID
        this.startDate = startDate;
        this.endDate = endDate;
        this.client = client;
        this.game = game;
        this.rentalPrice = rentalPrice;
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