package edu.kdmk.model.vehicle;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
public class Car extends Vehicle {

    private int numberOfDoors;
    private int numberOfSeats;
}
