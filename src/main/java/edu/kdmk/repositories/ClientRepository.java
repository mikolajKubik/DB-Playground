package edu.kdmk.repositories;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.models.Client;
import org.bson.Document;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ClientRepository {
    private final MongoCollection<Client> clientCollection;

    public ClientRepository(MongoDatabase database) {
        this.clientCollection = database.getCollection("clients", Client.class);
    }

    // Insert a new Client
    public void insert(ClientSession session, Client client) {
        clientCollection.insertOne(session, client);
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
}