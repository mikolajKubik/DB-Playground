package edu.kdmk.client;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity(defaultKeyspace = "rent_a_game")
@CqlName("clients")
public class Client {

    @EqualsAndHashCode.Include
    @PartitionKey
    @CqlName("client_id")
    private UUID clientId;

    @CqlName("first_name")
    private String firstName;

    @CqlName("last_name")
    private String lastName;

    @CqlName("address")
    private String address;

}

//    public Client(UUID clientId, String firstName, String lastName, String address) {
//        this.clientId = clientId;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.address = address;
//    }
//
//    public Client() {}
//
//    public UUID getClientId() {
//        return clientId;
//    }
//
//    public void setClientId(UUID clientId) {
//        this.clientId = clientId;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }