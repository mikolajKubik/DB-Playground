package edu.kdmk.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public abstract class AbstractEntity {

    @EqualsAndHashCode.Include
    private UUID id;

    public AbstractEntity() {
    }

    public AbstractEntity(UUID id) {
        this.id = id;
    }
}