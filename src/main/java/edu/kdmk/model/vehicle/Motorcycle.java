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
@ToString(callSuper = true)

@Entity
public class Motorcycle extends Vehicle {

    private int cylinderCapacity;
    private int power;
}
