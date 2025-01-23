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

    public boolean insert(ClientSession session, Rent rent) {
        return rentCollection.insertOne(session, rent).wasAcknowledged();
    }

    public Optional<Rent> findById(UUID id) {
        Document filter = new Document("_id", id.toString());
        return Optional.ofNullable(rentCollection.find(filter).first());
    }

    public Optional<Rent> findById(ClientSession session, UUID id) {
        Document filter = new Document("_id", id.toString());
        return Optional.ofNullable(rentCollection.find(session, filter).first());
    }

    public boolean update(Rent updatedRent) {
        Bson filter = eq("_id", updatedRent.getId().toString());
        return rentCollection.replaceOne(filter, updatedRent).wasAcknowledged();
    }

    public boolean updateById(ClientSession session, Rent updatedRent) {
        Bson filter = eq("_id", updatedRent.getId().toString());
        return rentCollection.replaceOne(session, filter, updatedRent).wasAcknowledged();
    }

    public boolean deleteById(ClientSession session, UUID id) {
        Bson filter = eq("_id", id.toString());
        return rentCollection.deleteOne(filter).wasAcknowledged();
    }

    public List<Rent> findAll() {
        return StreamSupport.stream(rentCollection.find().spliterator(), false)
                .collect(Collectors.toList());
    }
}