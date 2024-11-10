
package edu.kdmk.models.vehicle;


import edu.kdmk.models.game.Game;
import lombok.*;
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

}



/*@BsonProperty("number_of_doors")
    private int numberOfDoors;

    @BsonProperty("number_of_seats")
    private int numberOfSeats;

    public BoardGame(String licensePlate, String brand, String model, int yearOfProduction, String color, int pricePerDay, int numberOfDoors, int numberOfSeats) {
        super(licensePlate, brand, model, yearOfProduction, color, pricePerDay);
        this.numberOfDoors = numberOfDoors;
        this.numberOfSeats = numberOfSeats;
    }*/



