package edu.kdmk.repositories;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.models.Client;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.inc;

public class ClientRepository {
    private final MongoCollection<Client> clientCollection;

    public ClientRepository(MongoDatabase database) {
        this.clientCollection = database.getCollection("clients", Client.class);
    }

    // Insert a new Client
    /*public void insert(ClientSession session, Client client) {
        clientCollection.insertOne(session, client);
    }*/
    public boolean insert(ClientSession session, Client client) {
        try {
            return clientCollection.insertOne(session, client).wasAcknowledged();
        } catch (Exception e) {
            throw e;
        }
    }

    // Find a Client by its UUID
    public Client findById(ClientSession session, UUID id) {
        Document filter = new Document("id", id.toString());
        return clientCollection.find(session, filter).first();
    }


    // Update a Client by its UUID
    public void updateById(ClientSession session, Client updatedClient) {
        Document filter = new Document("id", updatedClient.getId().toString());
        clientCollection.replaceOne(session, filter, updatedClient);
    }

    // Delete a Client by its UUID
    public void deleteById(ClientSession session, UUID id) {
        Document filter = new Document("id", id.toString());
        clientCollection.deleteOne(session, filter);
    }

    // Retrieve all clients
    public List<Client> findAll(ClientSession session) {
        return StreamSupport.stream(clientCollection.find(session).spliterator(), false)
                .collect(Collectors.toList());
    }

    // Atomic increment to mark a client as renting another item
    public boolean markAsRented(ClientSession session, UUID clientId) {
        Bson filter = and(eq("id", clientId.toString()), lt("rentalCount", 5)); // Ensure rentalCount < 5
        Bson update = inc("rentalCount", 1); // Increment rental count by 1

        // Use Document as the return type to match MongoDB expectations
        Document updatedClient = clientCollection.withDocumentClass(Document.class)
                .findOneAndUpdate(session, filter, update);
        return updatedClient != null; // Returns true if update was successful, false if not
    }

    // Atomic decrement to unmark a client as renting an item
    public boolean unmarkAsRented(ClientSession session, UUID clientId) {
        Bson filter = and(eq("id", clientId.toString()), gt("rentalCount", 0)); // Ensure rentalCount > 0
        Bson update = inc("rentalCount", -1); // Decrement rental count by 1

        // Use Document as the return type to match MongoDB expectations
        Document updatedClient = clientCollection.withDocumentClass(Document.class)
                .findOneAndUpdate(session, filter, update);
        return updatedClient != null; // Returns true if update was successful, false if not
    }
}