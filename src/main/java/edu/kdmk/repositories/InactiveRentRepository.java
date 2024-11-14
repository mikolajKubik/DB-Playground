package edu.kdmk.repositories;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.models.Rent;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Optional;
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

    public boolean insert(Rent rent) {
        return inactiveRentCollection.insertOne(rent).wasAcknowledged();
    }

    public boolean insert(ClientSession session, Rent rent) {
        return inactiveRentCollection.insertOne(session, rent).wasAcknowledged();
    }

    public Optional<Rent> findById(UUID id) {
        Document filter = new Document("_id", id.toString());
        return Optional.ofNullable(inactiveRentCollection.find(filter).first());
    }

    public Optional<Rent> findById(ClientSession session, UUID id) {
        Document filter = new Document("_id", id.toString());
        return Optional.ofNullable(inactiveRentCollection.find(session, filter).first());
    }

    // Delete a Rent by its UUID
    public void deleteById(UUID id) {
        Bson filter = eq("_id", id.toString());
        inactiveRentCollection.deleteOne(filter);
    }

    // Retrieve all rents
    public List<Rent> findAll() {
        return StreamSupport.stream(inactiveRentCollection.find().spliterator(), false)
                .collect(Collectors.toList());
    }
}