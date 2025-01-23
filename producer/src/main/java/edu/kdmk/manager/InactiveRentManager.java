package edu.kdmk.manager;

import edu.kdmk.model.Rent;
import edu.kdmk.repository.InactiveRentRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InactiveRentManager {
    private final InactiveRentRepository inactiveRentRepository;

    public InactiveRentManager(InactiveRentRepository inactiveRentRepository) {
        this.inactiveRentRepository = inactiveRentRepository;
    }

    public boolean createInactiveRent(Rent rent) {
        return inactiveRentRepository.insert(rent);
    }

    public Optional<Rent> findInactiveRentById(UUID id) {
        try {
            return inactiveRentRepository.findById(id);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Rent> getAllInactiveRents() {
        try {
            return inactiveRentRepository.findAll();
        } catch (Exception e) {
            throw e;
        }
    }

    public void deleteInactiveRent(Rent rent) {
        try {
            inactiveRentRepository.deleteById(rent.getId());
        } catch (Exception e) {
            throw e;
        }
    }
}
