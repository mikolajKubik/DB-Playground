package edu.kdmk.models;

import java.util.UUID;

public class Client extends AbstractEntity {
    private String name;
    private String address;

    // Constructor for a new Client with auto-assigned UUID
    public Client(String name, String address) {
        super(); // UUID is auto-assigned
        this.name = name;
        this.address = address;
    }

    // Optional constructor for specifying UUID (e.g., when loading from DB)
    public Client(UUID id, String name, String address) {
        super(id); // Use provided UUID
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}