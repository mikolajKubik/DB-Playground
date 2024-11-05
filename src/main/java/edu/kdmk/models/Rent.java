package edu.kdmk.models;

import edu.kdmk.models.vehicle.Vehicle;
import lombok.*;
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
    private Vehicle vehicle;

    public Rent(Date startDate, Date endDate, int rentalPrice, Client client, Vehicle vehicle) {
        super(UUID.randomUUID());
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentalPrice = rentalPrice;
        this.client = client;
        this.vehicle = vehicle;
    }
}
