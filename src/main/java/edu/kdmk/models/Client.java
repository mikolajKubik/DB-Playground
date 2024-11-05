package edu.kdmk.models;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.UUID;
//
//@Data
//@Getter
//@Setter
//@ToString(callSuper = true)
//@NoArgsConstructor
//@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
//public class Client extends AbstractEntity {
//
//    @BsonProperty("first_name")
//    private String firstName;
//
//    @BsonProperty("last_name")
//    private String lastName;
//
//    @BsonProperty("phone_number")
//    private String phoneNumber;
//
//    @BsonProperty("address")
//    private String address;
//
//    public Client(String firstName, String lastName, String phoneNumber, String address) {
//        super(UUID.randomUUID());
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.phoneNumber = phoneNumber;
//        this.address = address;
//    }
//
//    @BsonCreator
//    public Client(@BsonProperty("id") UUID id,
//                  @BsonProperty("first_name") String firstName,
//                  @BsonProperty("last_name") String lastName,
//                  @BsonProperty("phone_number") String phoneNumber,
//                  @BsonProperty("address") String address) {
//        super(id);
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.phoneNumber = phoneNumber;
//        this.address = address;
//    }
//}
@Data
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Client extends AbstractEntity {

    @BsonProperty("first_name")
    private String firstName;

    @BsonProperty("last_name")
    private String lastName;

    @BsonProperty("phone_number")
    private String phoneNumber;

    @BsonProperty("address")
    private String address;

    @BsonCreator
    public Client(
            @BsonProperty("_id") UUID uuid,
            @BsonProperty("first_name") String firstName,
            @BsonProperty("last_name") String lastName,
            @BsonProperty("phone_number") String phoneNumber,
            @BsonProperty("address") String address
    ) {
        super(uuid);
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    // Convenience constructor for creating a new Client with a generated UUID
    public Client(String firstName, String lastName, String phoneNumber, String address) {
        this(UUID.randomUUID(), firstName, lastName, phoneNumber, address);
    }
}