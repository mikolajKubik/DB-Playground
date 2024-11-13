package edu.kdmk.repositories;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.models.Client;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Optional;
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

    public boolean insert(Client client) {
        return clientCollection.insertOne(client).wasAcknowledged();
    }

    public boolean insert(ClientSession session, Client client) {
        return clientCollection.insertOne(session, client).wasAcknowledged();
    }

    public Optional<Client> findById(UUID id) {
        Document filter = new Document("id", id.toString());
        return Optional.ofNullable(clientCollection.find(filter).first());
    }

    // Find a Client by its UUID
    public Optional<Client> findById(ClientSession session, UUID id) {
        Document filter = new Document("id", id.toString());
        return Optional.ofNullable(clientCollection.find(session, filter).first());
    }

    public boolean update(Client updatedClient) {
        Document filter = new Document("id", updatedClient.getId().toString());
        return clientCollection.replaceOne(filter, updatedClient).wasAcknowledged();
    }

    // Update a Client by its UUID
    public boolean update(ClientSession session, Client updatedClient) {
        Document filter = new Document("id", updatedClient.getId().toString());
        return clientCollection.replaceOne(session, filter, updatedClient).wasAcknowledged();
    }

    // Delete a Client by its UUID
    public boolean deleteById(UUID id) {
        Document filter = new Document("id", id.toString());
        return clientCollection.deleteOne(filter).wasAcknowledged();
    }

    // Delete a Client by its UUID
    public boolean deleteById(ClientSession session, UUID id) {
        Document filter = new Document("id", id.toString());
        return clientCollection.deleteOne(session, filter).wasAcknowledged();
    }

    // Retrieve all clients
    public List<Client> findAll() {
        return StreamSupport.stream(clientCollection.find().spliterator(), false)
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