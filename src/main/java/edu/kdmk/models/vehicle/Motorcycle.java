package edu.kdmk.models.vehicle;


import lombok.*;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Motorcycle extends Vehicle {

    @BsonProperty("cylinder_capacity")
    private int cylinderCapacity;
}
