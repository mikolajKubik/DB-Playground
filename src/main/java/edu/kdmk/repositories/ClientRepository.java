package edu.kdmk.repositories;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import edu.kdmk.config.MongoDBConnection;
import edu.kdmk.models.Client;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Optional;
import java.util.UUID;

public class ClientRepository {
    private final MongoCollection<Client> clientCollection;

    public ClientRepository(MongoCollection<Client> clientCollection) {
        this.clientCollection = clientCollection;
    }

    public Optional<Client> insertClient(ClientSession session, Client client) {
        /*if (session != null) {
            return clientCollection.insertOne(session, client);
        } else {
            clientCollection.insertOne(client);
        }
        return Optional.of(client);*/
    }

//    private final MongoCollection<Client> collection;
//
//    public ClientRepository(MongoDBConnection connection) {
//        this.collection = connection.getCollection("clients", Client.class);
//    }
//
//    // Create method
//    public void create(Client client) {
//        collection.insertOne(client);
//    }
//
//    // Read method by UUID
//    public Optional<Client> read(UUID uuid) {
//        Bson filter = Filters.eq("_id", uuid.toString());
//        Client client = collection.find(filter).first();
//        return Optional.ofNullable(client);
//    }
//
//    // Update method
//    public void update(UUID uuid, Client updatedClient) {
//        Bson filter = Filters.eq("_id", uuid.toString());
//        collection.replaceOne(filter, updatedClient);
//    }
//
//    // Delete method
//    public void delete(UUID uuid) {
//        Bson filter = Filters.eq("_id", uuid.toString());
//        collection.deleteOne(filter);
//    }
}