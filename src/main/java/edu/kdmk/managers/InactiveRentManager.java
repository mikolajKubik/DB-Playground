package edu.kdmk.managers;

import com.mongodb.client.ClientSession;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.models.Rent;
import edu.kdmk.repositories.InactiveRentRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InactiveRentManager {
    private final InactiveRentRepository inactiveRentRepository;

    public InactiveRentManager(MongoDatabase database) {
        this.inactiveRentRepository = new InactiveRentRepository(database);
    }

    public boolean createInactiveRent(Rent rent) {
        return inactiveRentRepository.insert(rent);
    }

    // Find an inactive Rent by UUID
    public Optional<Rent> findInactiveRentById(UUID id) {
        try {
            return inactiveRentRepository.findById(id);
        } catch (Exception e) {
            throw e;
        }
    }

    // Retrieve all inactive rents
    public List<Rent> getAllInactiveRents() {
        try {
            return inactiveRentRepository.findAll();
        } catch (Exception e) {
            throw e;
        }
    }

    // Delete an inactive Rent
    public void deleteInactiveRent(Rent rent) {
        try {
            inactiveRentRepository.deleteById(rent.getId());
        } catch (Exception e) {
            throw e;
        }
    }
}
