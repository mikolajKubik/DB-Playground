package edu.kdmk.model.vehicle;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
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
public class Car extends Vehicle {

    @Basic(optional = false)
    private int numberOfDoors;

    @Basic(optional = false)
    private int numberOfSeats;
}
