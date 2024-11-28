package edu.kdmk.models;

import lombok.*;

import java.util.UUID;

@Getter @Setter
@ToString(callSuper = true)

public class Client extends AbstractEntity {

    private String firstName;

    private String lastName;

    private String address;

    private int rentalCount;

    public Client() {
        super();
    }

    public Client(UUID id, String firstName, String lastName, String address, int rentalCount) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.rentalCount = rentalCount;
    }
}