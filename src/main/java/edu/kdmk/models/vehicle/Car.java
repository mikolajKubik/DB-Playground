package edu.kdmk.models.vehicle;


import lombok.*;
import lombok.experimental.SuperBuilder;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Car extends Vehicle {

    @BsonProperty("number_of_doors")
    private int numberOfDoors;

    @BsonProperty("number_of_seats")
    private int numberOfSeats;

    public Car(String licensePlate, String brand, String model, int yearOfProduction, String color, int pricePerDay, int numberOfDoors, int numberOfSeats) {
        super(licensePlate, brand, model, yearOfProduction, color, pricePerDay);
        this.numberOfDoors = numberOfDoors;
        this.numberOfSeats = numberOfSeats;
    }
}
