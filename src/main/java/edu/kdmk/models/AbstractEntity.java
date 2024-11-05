package edu.kdmk.models;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.UUID;

@Data
@Getter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AbstractEntity {

    @EqualsAndHashCode.Include
    @BsonProperty("_id")
    private UUID uuid;

    public AbstractEntity(UUID uuid) {
        this.uuid = uuid;
    }
}
