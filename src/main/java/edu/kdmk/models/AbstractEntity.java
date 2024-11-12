package edu.kdmk.models;

import java.util.UUID;

public abstract class AbstractEntity {
    private UUID id;

    public AbstractEntity(UUID id) {
        this.id = id;
    }

    public AbstractEntity() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}