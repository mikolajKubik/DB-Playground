package edu.kdmk.repositories.implemntations;

import edu.kdmk.model.Rent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RentRepository implements EntityRepository<Rent> {

    private final EntityManagerFactory entityManagerFactory;

    @Override
    public Rent add(Rent item) {


        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            em.getTransaction().begin();

            Vehicle vehicle = em.find(Vehicle.class, item.getVehicle().getId(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            Client client = em.find(Client.class, item.getClient().getId(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            em.persist(item);
            em.getTransaction().commit();
            return item;

            /*if (vehicle.getRents().isEmpty() && client.getRents().isEmpty()) {
                em.persist(item);
                em.getTransaction().commit();
                return item;
            } else {
                em.getTransaction().rollback();
                return null;
            }*/

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean remove(Rent item) {
        try (EntityManager em = entityManagerFactory.createEntityManager()){
            em.getTransaction().begin();
            Rent rentToRemove = em.find(Rent.class, item.getId(),
                    LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            if (rentToRemove != null) {
                em.remove(rentToRemove);
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
    public Rent getById(Long id) {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {

            return em.find(Rent.class, id);

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
    public Rent update(Rent item) {

        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            em.getTransaction().begin();

            Rent existingRent = em.find(Rent.class, item.getId(), LockModeType.OPTIMISTIC);
            if (existingRent != null) {
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
    public List<Rent> getAll() {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {

            return em.createQuery("SELECT c FROM Rent c", Rent.class).getResultList();

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
