package edu.kdmk.model.vehicle;

import edu.kdmk.model.Rent;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode()
@ToString()
//@MappedSuperclass
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Vehicle {

    @EqualsAndHashCode.Exclude //wystarczy porownywac po id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String licensePlate;
    private String brand;
    private String model;
    private int year;
    private int price;

    @ToString.Exclude
    @OneToMany(mappedBy = "vehicle")
    private List<Rent> rents;
}
