package edu.kdmk.game.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString
@Entity(defaultKeyspace = "rent_a_game")
@CqlName("games")
public class ComputerGame extends Game {
    @CqlName("platform")
    private String platform;

    public ComputerGame(UUID gameId, String name, String type, boolean rented, int pricePerDay, String platform) {
        super(gameId, name, type, rented, pricePerDay);
        this.platform = platform;
    }
}
