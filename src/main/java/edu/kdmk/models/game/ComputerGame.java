
package edu.kdmk.models.game;

import lombok.*;

import java.util.UUID;

@Getter @Setter
@ToString(callSuper = true)
public class ComputerGame extends Game {
    private String platform;

    public ComputerGame() {
        super();
    }

    public ComputerGame(UUID id, String name, GameType type, int pricePerDay, int rentalStatusCount, String platform) {
        super(id, name, type, pricePerDay, rentalStatusCount);
        this.platform = platform;
    }
}