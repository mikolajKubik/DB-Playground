package edu.kdmk.models;

import edu.kdmk.models.game.Game;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Date;
import java.util.UUID;

@Data
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Rent extends AbstractEntity {

    @BsonProperty("start_date")
    private Date startDate;

    @BsonProperty("end_date")
    private Date endDate;

    @BsonProperty("rental_price")
    private int rentalPrice;

    @BsonProperty("client")
    private Client client;

    @BsonProperty("vehicle")
    private Game game;

    @BsonCreator
    public Rent(@BsonProperty("id") UUID id,
            @BsonProperty("start_date") Date startDate,
            @BsonProperty("end_date") Date endDate,
            @BsonProperty("rental_price") int rentalPrice,
            @BsonProperty("client") Client client,
            @BsonProperty("game") Game game) {
    super(id);
    this.startDate = startDate;
    this.endDate = endDate;
    this.rentalPrice = rentalPrice;
    this.client = client;
    this.game = game;
}

    public Rent(Date startDate, Date endDate, int rentalPrice, Client client, Game game) {
        super(UUID.randomUUID());
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentalPrice = rentalPrice;
        this.client = client;
        this.game = game;
    }
}
