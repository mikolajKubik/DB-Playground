
package edu.kdmk.models.game;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ComputerGame extends Game {

    private final String platform;

    public ComputerGame(UUID id, String name, String platform) {
        super(id, name, GameType.COMPUTER_GAME);
        this.platform = platform;
    }

    public ComputerGame(String name, String platform) {
        super(name, GameType.COMPUTER_GAME); // UUID is auto-assigned
        this.platform = platform;
    }
}