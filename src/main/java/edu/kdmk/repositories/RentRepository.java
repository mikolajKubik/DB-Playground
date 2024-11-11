package edu.kdmk.repositories;

import com.mongodb.client.MongoCollection;
import edu.kdmk.models.Rent;


public class RentRepository {
    private final MongoCollection<Rent> rentCollection;

    public RentRepository(MongoCollection<Rent> rentCollection) {
        this.rentCollection = rentCollection;
    }
}
