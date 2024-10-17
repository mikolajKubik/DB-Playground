package edu.kdmk.repositories.implemntations;

import edu.kdmk.model.vehicle.Vehicle;
import edu.kdmk.repositories.EntityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;


import java.util.List;


public class VehicleRepository implements EntityRepository<Vehicle> {

    @Override
    public Vehicle add(Vehicle item, EntityManager em) {
        try {
            em.persist(item);
        } catch (Exception e) {
            throw e;
        }
        return em.find(Vehicle.class, item.getId());
    }

    @Override
    public boolean remove(Vehicle item, EntityManager em) {
        try {
            em.remove(item);
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Vehicle update(Vehicle item, EntityManager em) {
        try {
            return em.merge(item);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Vehicle getById(Long id, EntityManager em) {
        try {
            return em.find(Vehicle.class, id, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<Vehicle> getAll(EntityManager em) {
        try {
            return em.createQuery("SELECT v FROM Vehicle v", Vehicle.class).getResultList();
        } catch (Exception e) {
            throw e;
        }
    }
}
