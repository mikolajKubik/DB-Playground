package edu.kdmk.repositories;

import edu.kdmk.model.Rent;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RentRepository implements EntityRepository<Rent> {

    private final EntityManager entityManager;

    @Override
    public Rent add(Rent item) {
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
