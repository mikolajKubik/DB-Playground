package edu.kdmk.managers;


import edu.kdmk.model.vehicle.Vehicle;
import edu.kdmk.repositories.EntityRepository;
import edu.kdmk.repositories.implemntations.VehicleRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class VehicleManager {

    private final EntityManagerFactory emf;
    private EntityRepository<Vehicle> vehicleRepository;

    public Vehicle addVehicle(Vehicle vehicle) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            vehicleRepository.add(vehicle, em);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
        return vehicle;
    }

    public boolean removeVehicle(Vehicle vehicle) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Vehicle vehicleToRemove = vehicleRepository.getById(vehicle.getId(), em);
            vehicleRepository.remove(vehicleToRemove, em);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
        return true;
    }

    public Vehicle updateVehicle(Vehicle vehicle) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Vehicle vehicleToUpdate = vehicleRepository.getById(vehicle.getId(), em);
            vehicleRepository.update(vehicleToUpdate, em);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
        return vehicle;
    }

    public void getVehicleById(Long id) {
        var em = emf.createEntityManager();
        try {
            vehicleRepository.getById(id, em);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Vehicle> getAllVehicles() {
        var em = emf.createEntityManager();
        try {
            return vehicleRepository.getAll(em);
        } catch (Exception e) {
            throw e;
        }
    }
}
