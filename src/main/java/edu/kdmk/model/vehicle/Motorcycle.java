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
@ToString(callSuper = true)
public class Motorcycle extends Vehicle {

    private int cylinderCapacity;
    private int power;
}
