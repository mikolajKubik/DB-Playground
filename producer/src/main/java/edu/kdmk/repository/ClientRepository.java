package edu.kdmk.repository;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.model.Client;
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
        Document filter = new Document("_id", id.toString());
        return Optional.ofNullable(clientCollection.find(filter).first());
    }

    public Optional<Client> findById(ClientSession session, UUID id) {
        Document filter = new Document("_id", id.toString());
        return Optional.ofNullable(clientCollection.find(session, filter).first());
    }

    public boolean update(Client updatedClient) {
        Document filter = new Document("_id", updatedClient.getId().toString());
        return clientCollection.replaceOne(filter, updatedClient).wasAcknowledged();
    }

    public boolean update(ClientSession session, Client updatedClient) {
        Document filter = new Document("_id", updatedClient.getId().toString());
        return clientCollection.replaceOne(session, filter, updatedClient).wasAcknowledged();
    }

    public boolean deleteById(UUID id) {
        Document filter = new Document("_id", id.toString());
        return clientCollection.deleteOne(filter).wasAcknowledged();
    }

    public boolean deleteById(ClientSession session, UUID id) {
        Document filter = new Document("_id", id.toString());
        return clientCollection.deleteOne(session, filter).wasAcknowledged();
    }

    public List<Client> findAll() {
        return StreamSupport.stream(clientCollection.find().spliterator(), false)
                .collect(Collectors.toList());
    }

    // Atomic increment to mark a client as renting another item
    // https://medium.com/@codersauthority/handling-race-conditions-and-concurrent-resource-updates-in-node-and-mongodb-by-performing-atomic-9f1a902bd5fa
    public boolean markAsRented(ClientSession session, UUID clientId) {
        Bson filter = and(eq("_id", clientId.toString()), lt("rentalCount", 5)); // Ensure rentalCount < 5
        Bson update = inc("rentalCount", 1); // Increment rental count by 1

        Document updatedClient = clientCollection.withDocumentClass(Document.class)
                .findOneAndUpdate(session, filter, update);
        return updatedClient != null; // Returns true if update was successful, false if not
    }

    // Atomic decrement to unmark a client as renting an item
    // https://medium.com/@codersauthority/handling-race-conditions-and-concurrent-resource-updates-in-node-and-mongodb-by-performing-atomic-9f1a902bd5fa
    public boolean unmarkAsRented(ClientSession session, UUID clientId) {
        Bson filter = and(eq("_id", clientId.toString()), gt("rentalCount", 0)); // Ensure rentalCount > 0
        Bson update = inc("rentalCount", -1); // Decrement rental count by 1

        Document updatedClient = clientCollection.withDocumentClass(Document.class)
                .findOneAndUpdate(session, filter, update);
        return updatedClient != null; // Returns true if update was successful, false if not
    }
}