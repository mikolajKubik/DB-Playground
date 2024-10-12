package edu.kdmk.repositories;

import edu.kdmk.model.vehicle.Vehicle;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class VehicleRepository implements EntityRepository<Vehicle> {


    private final EntityManager entityManager;

    @Override
    public Vehicle add(Vehicle item) {
        try{
            entityManager.getTransaction().begin();

            entityManager.persist(item);

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public boolean remove(Vehicle item) {
        return entityManager != null;
    }

    @Override
    public Vehicle getById(Long id) {
        return null;
    }

    @Override
    public Vehicle update(Vehicle item) {
        return null;
    }

    @Override
    public List<Vehicle> getAll() {
        return List.of();
    }
}
