package edu.kdmk.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter @Setter
@ToString(callSuper = true)
public class Client extends AbstractEntity {

    private String firstName;

    private String lastName;

    private String address;

    @Setter(AccessLevel.NONE)
    private int rentalCount;

    public Client(String firstName, String lastName, String address) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    public Client(UUID id, String name, String lastName, String address, int rentalCount) {
        super(id);
        this.firstName = name;
        this.lastName = lastName;
        this.address = address;
        this.rentalCount = rentalCount;
    }
}