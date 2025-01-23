package edu.kdmk.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public abstract class AbstractEntity {

    @Getter @Setter
    @EqualsAndHashCode.Include
    private UUID id;

    public AbstractEntity(UUID id) {
        this.id = id;
    }

    public AbstractEntity() {
        this.id = UUID.randomUUID();
    }
}