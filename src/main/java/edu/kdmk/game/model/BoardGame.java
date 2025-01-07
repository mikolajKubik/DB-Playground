package edu.kdmk.game.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString
@Entity(defaultKeyspace = "rent_a_game")
@CqlName("games")
public class BoardGame extends Game {
    private int minPlayers;
    private int maxPlayers;
}
