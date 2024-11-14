package edu.kdmk.repositories;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.models.Rent;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;

public class InactiveRentRepository {
    private final MongoCollection<Rent> inactiveRentCollection;

    public InactiveRentRepository(MongoDatabase database) {
        // Initialize the collection for Rent documents
        this.inactiveRentCollection = database.getCollection("inactiveRents", Rent.class);
    }

    // Insert a new Rent
    public void insert(ClientSession session, Rent rent) {
        inactiveRentCollection.insertOne(session, rent);
    }

    // Find a Rent by its UUID
    public Rent findById(ClientSession session, UUID id) {
        Document filter = new Document("id", id.toString());
        return inactiveRentCollection.find(session, filter).first();
    }

    // Update a Rent by its UUID, replacing the entire document
//    public void updateById(ClientSession session, Rent updatedRent) {
//        Document filter = new Document("id", updatedRent.getId().toString());
//        inActiveRentCollection.replaceOne(session, filter, updatedRent);
//    }
//
    // Delete a Rent by its UUID
//    public void deleteById(ClientSession session, UUID id) {
//        Document filter = new Document("id", id.toString());
//        inActiveRentCollection.deleteOne(session, filter);
//    }

    public void updateById(ClientSession session, Rent updatedRent) {
        Bson filter = eq("id", updatedRent.getId().toString());
        inactiveRentCollection.replaceOne(session, filter, updatedRent);
    }

    // Delete a Rent by its UUID
    public void deleteById(ClientSession session, UUID id) {
        Bson filter = eq("id", id.toString());
        inactiveRentCollection.deleteOne(session, filter);
    }

    // Retrieve all rents
    public List<Rent> findAll(ClientSession session) {
        return StreamSupport.stream(inactiveRentCollection.find(session).spliterator(), false)
                .collect(Collectors.toList());
    }
}