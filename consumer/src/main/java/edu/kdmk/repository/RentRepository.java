package edu.kdmk.repository;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.model.Rent;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;

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