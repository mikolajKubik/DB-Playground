package edu.kdmk.managers;

import edu.kdmk.model.Client;
import edu.kdmk.model.Rent;
import edu.kdmk.model.vehicle.Vehicle;
import edu.kdmk.repositories.EntityRepository;
import edu.kdmk.repositories.implemntations.ClientRepository;
import edu.kdmk.repositories.implemntations.RentRepository;
import edu.kdmk.repositories.implemntations.VehicleRepository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class RentManager {

    private final EntityManagerFactory emf;
    private EntityRepository<Rent> rentRepository;
    private EntityRepository<Vehicle> vehicleRepository;
    private EntityRepository<Client> clientRepository;

    public Rent addRent(Rent rent) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Vehicle vehicleToRent = vehicleRepository.getById(rent.getVehicle().getId(), em);
            Client clientToRent = clientRepository.getById(rent.getClient().getId(), em);

            if (vehicleToRent.getRents().isEmpty() && clientToRent.getRents().size() <= 5) {
                vehicleToRent.getRents().add(rent);
                clientToRent.getRents().add(rent);

                rentRepository.add(rent, em);

            } else {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Vehicle has too many rents or client has too many rents");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
        return rent;
    }

    public boolean removeRent(Rent rent) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Rent rentToRemove = rentRepository.getById(rent.getId(), em);

            if (rentToRemove == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Rent not found");
            }

            Vehicle vehicle = rentRepository.getById(rent.getId(), em).getVehicle();
            Client client = rentRepository.getById(rent.getId(), em).getClient();

            if (vehicle != null && client != null) {
                vehicle.getRents().remove(rent);
                client.getRents().remove(rent);
                rentRepository.remove(rentToRemove, em);
            } else {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Vehicle or client not found");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
        return true;
    }

    public Rent updateRent(Rent rent) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Rent existingRent = rentRepository.getById(rent.getId(), em);
            if (existingRent == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Rent with ID " + rent.getId() + " not found.");
            }

            // Update the rent
            rentRepository.update(rent, em);

            em.getTransaction().commit();
            return rent;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }


    public Rent getRentById(Long id) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Rent rent = rentRepository.getById(id, em);
            em.getTransaction().commit();
            return rent;
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Rent> getAllRents() {
        var em = emf.createEntityManager();
        try {
            return rentRepository.getAll(em);
        } catch (Exception e) {
            throw e;
        }
    }
}
