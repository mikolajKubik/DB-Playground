package edu.kdmk.models;

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

    // Constructor for a new Client with auto-assigned UUID
    public Client(String firstName, String lastName, String address) {
        super(); // UUID is auto-assigned
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    // Optional constructor for specifying UUID (e.g., when loading from DB)
    public Client(UUID id, String name, String lastName, String address, int rentalCount) {
        super(id); // Use provided UUID
        this.firstName = name;
        this.lastName = lastName;
        this.address = address;
        this.rentalCount = rentalCount;
    }
}