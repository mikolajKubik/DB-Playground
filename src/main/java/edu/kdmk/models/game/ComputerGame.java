
package edu.kdmk.models.game;


import lombok.*;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ComputerGame extends Game {

    @BsonProperty("platform")
    private String platform; // e.g., "PC", "Console", "Mobile"

    @BsonProperty("is_multiplayer")
    private boolean isMultiplayer;

    @BsonCreator
    public ComputerGame(@BsonProperty("game_name") String gameName,
                        @BsonProperty("recommended_age") int recommendedAge,
                        @BsonProperty("release_year") int releaseYear,
                        @BsonProperty("publisher") String publisher,
                        @BsonProperty("price_per_day") int pricePerDay,
                        @BsonProperty("platform") String platform,
                        @BsonProperty("is_multiplayer") boolean isMultiplayer) {
        super(gameName, recommendedAge, releaseYear, publisher, pricePerDay, GameType.COMPUTER_GAME);
        this.platform = platform;
        this.isMultiplayer = isMultiplayer;
    }
}

