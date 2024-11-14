package edu.kdmk.managers;

import com.mongodb.client.ClientSession;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.models.Rent;
import edu.kdmk.repositories.InactiveRentRepository;

import java.util.List;
import java.util.UUID;

public class inActiveRentManager {
    private final MongoClient mongoClient;
    private final InactiveRentRepository inactiveRentRepository;

    public inActiveRentManager(MongoClient mongoClient, MongoDatabase database) {
        this.mongoClient = mongoClient;
        this.inactiveRentRepository = new InactiveRentRepository(database);
    }

    // Find an inactive Rent by UUID
    public Rent findInactiveRentById(UUID id) {
        try (ClientSession session = mongoClient.startSession()) {
            return inactiveRentRepository.findById(session, id);
        }
    }

    // Retrieve all inactive rents
    public List<Rent> getAllInactiveRents() {
        try (ClientSession session = mongoClient.startSession()) {
            session.startTransaction();

            List<Rent> rents = inactiveRentRepository.findAll(session);

            session.commitTransaction();
            return rents;
        } catch (Exception e) {
            System.err.println("Failed to retrieve inactive rents: " + e.getMessage());
            return null;
        }
    }

    // Delete an inactive Rent
    public void deleteInactiveRent(Rent rent) {
        try (ClientSession session = mongoClient.startSession()) {
            session.startTransaction();

            inactiveRentRepository.deleteById(session, rent.getId());

            session.commitTransaction();
            System.out.println("Inactive rent deleted successfully.");
        } catch (Exception e) {
            System.err.println("Failed to delete inactive rent: " + e.getMessage());
        }
    }// Method to create a new Rent, mark Client and Game as rented

}
