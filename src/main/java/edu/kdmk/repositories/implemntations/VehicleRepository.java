package edu.kdmk.repositories.implemntations;

import edu.kdmk.model.vehicle.Vehicle;
import edu.kdmk.repositories.EntityRepository;
import edu.kdmk.repositories.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;


import java.util.List;


public class VehicleRepository implements EntityRepository<Vehicle> {

    @Override
    public Vehicle add(Vehicle item) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            em.persist(item);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
        return item;
    }

    @Override
    public boolean remove(Vehicle item) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Vehicle vehicleToRemove = em.find(Vehicle.class, item.getId(),
                    LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            if (vehicleToRemove != null) {
                em.remove(vehicleToRemove);
                em.getTransaction().commit();
                return true;
            } else {
                em.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Vehicle getById(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            return em.find(Vehicle.class, id);

        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return null;
    }

    @Override
    public Vehicle update(Vehicle item) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            Vehicle existingVehicle = em.find(Vehicle.class, item.getId(), LockModeType.OPTIMISTIC);
            if (existingVehicle != null) {
                em.merge(item);
                em.getTransaction().commit();
                return item;
            } else {
                em.getTransaction().rollback();
                return null;
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }

    }

    @Override
    public List<Vehicle> getAll() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            return em.createQuery("SELECT c FROM Client c", Vehicle.class).getResultList();

        } catch (Exception e) {
            em.getTransaction().rollback();
        }
        finally {
            em.close();
        }

        return List.of();
    }
}
