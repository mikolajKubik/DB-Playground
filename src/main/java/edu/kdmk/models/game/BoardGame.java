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
public class BoardGame extends Game {

    @BsonProperty("number_of_players")
    private int numberOfPlayers;

    @BsonProperty("average_play_time")
    private int averagePlayTime;

    @BsonCreator
    public BoardGame(@BsonProperty("game_name") String gameName,
                     @BsonProperty("recommended_age") int recommendedAge,
                     @BsonProperty("release_year") int releaseYear,
                     @BsonProperty("publisher") String publisher,
                     @BsonProperty("price_per_day") int pricePerDay,
                     @BsonProperty("number_of_players") int numberOfPlayers,
                     @BsonProperty("average_play_time") int averagePlayTime) {
        super(gameName, recommendedAge, releaseYear, publisher, pricePerDay);
        this.numberOfPlayers = numberOfPlayers;
        this.averagePlayTime = averagePlayTime;
    }

}



