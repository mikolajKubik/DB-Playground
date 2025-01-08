package edu.kdmk.client;

import com.datastax.oss.driver.api.mapper.annotations.*;

import java.util.List;
import java.util.UUID;

@Dao
public interface ClientDao {

    @StatementAttributes(consistencyLevel = "TWO", pageSize = 100)
    @Insert
    void save(Client client);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @Select
    Client findById(UUID clientId);

    @StatementAttributes(consistencyLevel = "TWO", pageSize = 100)
    @Delete
    void delete(Client client);

    @StatementAttributes(consistencyLevel = "TWO", pageSize = 100)
    @Update
    void update(Client client);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @Query("SELECT * FROM clients WHERE first_name = :firstName ALLOW FILTERING")
    List<Client> findByFirstName(String firstName);



//    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
//    @QueryProvider(providerClass = ClientProvider.class, entityHelpers = {Client.class})
//    Client findByUuid(UUID id);

//    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
//    @QueryProvider(providerClass = ClientProvider.class, entityHelpers = {Client.class})
//    Client findByUuid(UUID id);
//
//    @StatementAttributes(consistencyLevel = "QUORUM")
//    @QueryProvider(providerClass = ClientProvider.class, entityHelpers = {Client.class})
//    void create(Client client);

}
