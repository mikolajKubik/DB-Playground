package edu.kdmk.repositories;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.models.Rent;
import org.bson.Document;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RentRepository {
    private final MongoCollection<Rent> rentCollection;

    public RentRepository(MongoDatabase database) {
        // Initialize the collection for Rent documents
        this.rentCollection = database.getCollection("rents", Rent.class);
    }

    // Insert a new Rent
    public void insert(ClientSession session, Rent rent) {
        rentCollection.insertOne(session, rent);
    }

    // Find a Rent by its UUID
    public Rent findById(ClientSession session, UUID id) {
        Document filter = new Document("id", id.toString());
        return rentCollection.find(session, filter).first();
    }

    // Update a Rent by its UUID, replacing the entire document
    public void updateById(ClientSession session, Rent updatedRent) {
        Document filter = new Document("id", updatedRent.getId().toString());
        rentCollection.replaceOne(session, filter, updatedRent);
    }

    // Delete a Rent by its UUID
    public void deleteById(ClientSession session, UUID id) {
        Document filter = new Document("id", id.toString());
        rentCollection.deleteOne(session, filter);
    }

    // Retrieve all rents
    public List<Rent> findAll(ClientSession session) {
        return StreamSupport.stream(rentCollection.find(session).spliterator(), false)
                .collect(Collectors.toList());
    }
}