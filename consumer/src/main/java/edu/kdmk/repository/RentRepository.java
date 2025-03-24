package edu.kdmk.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.model.Rent;
import org.bson.Document;

import java.util.Optional;
import java.util.UUID;

public class RentRepository {
    private final MongoCollection<Rent> rentCollection;

    public RentRepository(MongoDatabase database) {
        this.rentCollection = database.getCollection("rents", Rent.class);
    }

    public boolean insert(Rent rent) {
        return rentCollection.insertOne(rent).wasAcknowledged();
    }

    public Optional<Rent> findById(UUID id) {
        Document filter = new Document("_id", id.toString());
        return Optional.ofNullable(rentCollection.find(filter).first());
    }
}