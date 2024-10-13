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
public class Motorcycle extends Vehicle {

    @Basic(optional = false)
    private int cylinderCapacity;

    @Basic(optional = false)
    private int power;
}
