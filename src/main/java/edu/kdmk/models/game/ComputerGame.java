
package edu.kdmk.models.game;


import lombok.*;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ComputerGame extends Game {

//    @BsonProperty("cylinder_capacity")
//    private int chuj ;

    @BsonProperty("platform")
    private String platform; // e.g., "PC", "Console", "Mobile"

    @BsonProperty("is_multiplayer")
    private boolean isMultiplayer;
}

