package edu.kdmk.rent.model;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import edu.kdmk.client.Client;
import edu.kdmk.game.model.Game;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Rent {

    private UUID rentId;

    private LocalDate startDate;

    private LocalDate endDate;

    //private Client client;
    //private Game game;

    private UUID clientId;

    private UUID gameId;

    private int rentalPrice;
}
