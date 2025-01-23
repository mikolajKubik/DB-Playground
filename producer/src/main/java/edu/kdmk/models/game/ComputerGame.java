
package edu.kdmk.models.game;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@ToString(callSuper = true)
public class ComputerGame extends Game {
    @Getter
    private final String platform;

    public ComputerGame(UUID id, String name, int pricePerDay, int rentalStatusCount, String platform) {
        super(id, name, GameType.COMPUTER_GAME, pricePerDay, rentalStatusCount);
        this.platform = platform;
    }

    public ComputerGame(String name, int pricePerDay, String platform) {
        super(name, GameType.COMPUTER_GAME, pricePerDay); // UUID is auto-assigned
        this.platform = platform;
    }
}