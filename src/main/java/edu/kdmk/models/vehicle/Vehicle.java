package edu.kdmk.models.vehicle;

import edu.kdmk.models.AbstractEntity;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.UUID;

@Data
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public abstract class Vehicle extends AbstractEntity {

    @BsonProperty("license_plate")
    private String licensePlate;

    @BsonProperty("brand")
    private String brand;

    @BsonProperty("model")
    private String model;

    @BsonProperty("year_of_production")
    private int yearOfProduction;

    @BsonProperty("color")
    private String color;

    @BsonProperty("price_per_day")
    private int pricePerDay;

    @BsonProperty("is_rented")
    private int isRented = 0;

    public Vehicle(String licensePlate, String brand, String model, int yearOfProduction, String color, int pricePerDay) {
        super(UUID.randomUUID());
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.yearOfProduction = yearOfProduction;
        this.color = color;
        this.pricePerDay = pricePerDay;
    }
}
