package edu.kdmk.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Client {

    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;


    @ToString.Exclude
    @OneToMany(mappedBy = "client")
    private List<Rent> rents;
}
