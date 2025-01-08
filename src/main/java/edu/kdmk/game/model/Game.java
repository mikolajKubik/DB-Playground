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
    private UUID gameId;

    private String name;

    private String type;

    private boolean rented;

    private int pricePerDay;

}
