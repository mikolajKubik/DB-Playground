package edu.kdmk.model.vehicle;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)

@Entity
public class Motorcycle extends Vehicle {

    @NotNull
    private int cylinderCapacity;
    @NotNull
    private int power;
}
