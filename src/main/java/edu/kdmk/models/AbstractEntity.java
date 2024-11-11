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

//    @EqualsAndHashCode.Include
//    @BsonProperty("_id")
//    private UUID uuid;
//
//    public AbstractEntity(UUID uuid) {
//        this.uuid = uuid;
//    }
    @EqualsAndHashCode.Include
    @BsonProperty("_id")
    private String uuid; // Store UUID as a string in MongoDB

    public AbstractEntity(UUID uuid) {
        this.uuid = uuid.toString(); // Convert UUID to String for storage
    }

    public UUID getUuid() {
        return UUID.fromString(uuid); // Convert stored String back to UUID
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid.toString(); // Store as String
    }
}
