package edu.kdmk.repositories;

import edu.kdmk.model.vehicle.Motorcycle;
import edu.kdmk.model.vehicle.Vehicle;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VehicleEntityRepository implements EntityRepository<Vehicle> {


    private EntityManager entityManager;

    @Autowired
    private Motorcycle motorcycle;

    @Autowired
    public VehicleEntityRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Vehicle add(Vehicle item) {
        return null;
    }

    @Override
    public boolean remove(Vehicle item) {
        System.out.println(motorcycle.toString());
        return false;
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
