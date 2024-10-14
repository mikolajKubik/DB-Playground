package edu.kdmk.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
public class Client {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Version
    private long version;

    @Basic(optional = false)
    private String name;

    @Basic(optional = false)
    private String phoneNumber;

    @Basic(optional = false)
    private String address;

    @ToString.Exclude
    @OneToMany(mappedBy = "client")
    private List<Rent> rents = new ArrayList<>();

    public void addRent(Rent rent) {

    }
}
