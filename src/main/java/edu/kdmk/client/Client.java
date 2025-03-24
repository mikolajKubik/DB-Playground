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