package edu.kdmk.repositories.implemntations;

import edu.kdmk.model.vehicle.Vehicle;
import edu.kdmk.repositories.EntityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class VehicleRepository implements EntityRepository<Vehicle> {


    private final EntityManagerFactory entityManagerFactory;

    @Override
    public Vehicle add(Vehicle item) {

        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            em.getTransaction().begin();

            em.persist(item);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public boolean remove(Vehicle item) {
        try (EntityManager em = entityManagerFactory.createEntityManager()){
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

            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Vehicle getById(Long id) {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {

            return em.find(Vehicle.class, id);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        }
        finally {
            em.close();
        }

        return null;
    }

    @Override
    public Vehicle update(Vehicle item) {

        try (EntityManager em = entityManagerFactory.createEntityManager()) {
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
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<Vehicle> getAll() {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {

            return em.createQuery("SELECT c FROM Client c", Vehicle.class).getResultList();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        }
        finally {
            em.close();
        }

        return List.of();
    }
}
