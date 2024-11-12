package edu.kdmk.models;

import edu.kdmk.models.game.Game;

import java.time.LocalDate;
import java.util.UUID;

public class Rent extends AbstractEntity {
    private LocalDate startDate;
    private LocalDate endDate;
    private Client client;  // Embedded Client object
    private Game game;      // Embedded Game object

    // Constructor for new Rent with auto-assigned UUID
    public Rent(LocalDate startDate, LocalDate endDate, Client client, Game game) {
        super(); // UUID is auto-assigned
        this.startDate = startDate;
        this.endDate = endDate;
        this.client = client;
        this.game = game;
    }

    // Optional constructor to specify UUID (e.g., when loading from DB)
    public Rent(UUID id, LocalDate startDate, LocalDate endDate, Client client, Game game) {
        super(id); // Use provided UUID
        this.startDate = startDate;
        this.endDate = endDate;
        this.client = client;
        this.game = game;
    }

    // Getters and setters
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public String toString() {
        return "Rent{" +
                "id=" + getId() +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", client=" + client +
                ", game=" + game +
                '}';
    }
}