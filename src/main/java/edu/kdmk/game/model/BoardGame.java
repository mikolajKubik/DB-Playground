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
public class BoardGame extends Game {
    @CqlName("min_players")
    private int minPlayers;

    @CqlName("max_players")
    private int maxPlayers;

    public BoardGame(UUID gameId, String name, String type, boolean rented, int pricePerDay, int minPlayers, int maxPlayers) {
        super(gameId, name, type, rented, pricePerDay);
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }
}
