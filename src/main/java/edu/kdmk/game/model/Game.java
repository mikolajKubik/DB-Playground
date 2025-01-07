package edu.kdmk.game.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity(defaultKeyspace = "rent_a_game")
@CqlName("games")
public class Game {
    @EqualsAndHashCode.Include
    @PartitionKey
    @CqlName("game_id")
    private UUID game;

    @CqlName("name")
    private String name;

    @CqlName("type")
    private String type;

    @CqlName("rented")
    private boolean rented;

    @CqlName("price_per_day") // elo
    private int pricePerDay;

}
