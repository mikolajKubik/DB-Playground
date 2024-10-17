package edu.kdmk.repositories.implemntations;

import edu.kdmk.model.Rent;
import edu.kdmk.repositories.EntityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import java.util.List;

public class RentRepository implements EntityRepository<Rent> {

    @Override
    public Rent add(Rent item, EntityManager em) {
        try {
            em.merge(item.getVehicle());
            em.merge(item.getClient());
            em.persist(item);
        } catch (Exception e) {
            throw e;
        }
        return em.find(Rent.class, item.getId());
    }

    @Override
    public boolean remove(Rent item, EntityManager em) {
        try {
            em.merge(item.getVehicle());
            em.merge(item.getClient());
            em.remove(item);
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    @Override
    public Rent update(Rent item, EntityManager em) {
        try {
            return em.merge(item);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Rent getById(Long id, EntityManager em) {
        try {
            return em.find(Rent.class, id, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        } catch (Exception e) {
            throw e;
        }
    }


    @Override
    public List<Rent> getAll(EntityManager em) {
        try {
            return em.createQuery("SELECT r FROM Rent r", Rent.class).getResultList();
        } catch (Exception e) {
            throw e;
        }
    }
}
