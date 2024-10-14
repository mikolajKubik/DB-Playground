package edu.kdmk.model.vehicle;

import edu.kdmk.model.Rent;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString()
//@MappedSuperclass
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Vehicle {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Version
    private long version;

    @Basic(optional = false)
    private String licensePlate;

    @Basic(optional = false)
    private String brand;

    @Basic(optional = false)
    private String model;

    @Basic(optional = false)
    private int year;

    @Basic(optional = false)
    private int price;

    @ToString.Exclude
    @OneToMany(mappedBy = "vehicle")
    private List<Rent> rents = new ArrayList<>();




}
