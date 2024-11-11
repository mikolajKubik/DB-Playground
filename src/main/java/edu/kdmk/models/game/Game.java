package edu.kdmk.models.game;

import edu.kdmk.models.AbstractEntity;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.UUID;

@Data
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public abstract class Game extends AbstractEntity {

//    @BsonProperty("license_plate")
//    private String licensePlate;
//
//    @BsonProperty("brand")
//    private String brand;
//
//    @BsonProperty("model")
//    private String model;
//
//    @BsonProperty("year_of_production")
//    private int yearOfProduction;
//
//    @BsonProperty("color")
//    private String color;
//
//    @BsonProperty("price_per_day")
//    private int pricePerDay;
//
//    @BsonProperty("is_rented")
//    private int isRented = 0;
//
//    public Game(String licensePlate, String brand, String model, int yearOfProduction, String color, int pricePerDay) {
//        super(UUID.randomUUID());
//        this.licensePlate = licensePlate;
//        this.brand = brand;
//        this.model = model;
//        this.yearOfProduction = yearOfProduction;
//        this.color = color;
//        this.pricePerDay = pricePerDay;
//    }
    @BsonProperty("game_name")
    private String gameName;

    @BsonProperty("recommended_age")
    private int recommendedAge;

    @BsonProperty("release_year")
    protected int releaseYear;

    @BsonProperty("publisher")
    protected String publisher;

    @BsonProperty("is_rented")
    private int isRented = 0;

    @BsonProperty("price_per_day")
    private int pricePerDay;

    @BsonCreator
    public Game(@BsonProperty("id") UUID id,
                @BsonProperty("game_name") String gameName,
                @BsonProperty("recommended_age") int recommendedAge,
                @BsonProperty("release_year") int releaseYear,
                @BsonProperty("publisher") String publisher,
                @BsonProperty("price_per_day") int pricePerDay,
                @BsonProperty("is_rented") int isRented) {
        super(id);
        this.gameName = gameName;
        this.recommendedAge = recommendedAge;
        this.releaseYear = releaseYear;
        this.publisher = publisher;
        this.pricePerDay = pricePerDay;
        this.isRented = isRented;
    }

    public Game(String gameName, int recommendedAge, int releaseYear, String publisher, int pricePerDay) {
        super(UUID.randomUUID());
        this.gameName = gameName;
        this.recommendedAge = recommendedAge;
        this.releaseYear = releaseYear;
        this.publisher = publisher;
        this.pricePerDay = pricePerDay;
        this.isRented = 0;
    }
}
