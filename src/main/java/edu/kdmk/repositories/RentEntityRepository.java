package edu.kdmk.repositories;

import edu.kdmk.model.Rent;

import java.util.List;

public class RentEntityRepository implements EntityRepository<Rent> {
    @Override
    public Rent add(Rent item) {
        return null;
    }

    @Override
    public boolean remove(Rent item) {
        return false;
    }

    @Override
    public Rent getById(Long id) {
        return null;
    }

    @Override
    public Rent update(Rent item) {
        return null;
    }

    @Override
    public List<Rent> getAll() {
        return List.of();
    }
}
