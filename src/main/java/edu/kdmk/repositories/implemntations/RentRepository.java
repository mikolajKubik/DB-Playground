package edu.kdmk.repositories.implemntations;

import edu.kdmk.model.Client;
import edu.kdmk.model.Rent;
import edu.kdmk.model.vehicle.Vehicle;
import edu.kdmk.repositories.EntityRepository;
import edu.kdmk.repositories.JPAUtil;
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


            if (vehicle.getRents().isEmpty() && client.getRents().size() <= 5) {
                vehicle.getRents().add(item);
                client.getRents().add(item);

                em.merge(vehicle);
                em.merge(client);
                em.persist(item);
                em.getTransaction().commit();
                return item;
            } else {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Vehicle has too many rents or client has too many rents");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean remove(Rent item) {
        try (EntityManager em = entityManagerFactory.createEntityManager()){
            em.getTransaction().begin();

            // Find the Rent entity by ID
            Rent rent = em.find(Rent.class, item.getId(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            if (rent == null) {
                em.getTransaction().rollback();
                return false;  // Rent not found
            }

            // Get the associated Vehicle and Client
            Vehicle vehicle = em.find(Vehicle.class, rent.getVehicle().getId(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            Client client = em.find(Client.class, rent.getClient().getId(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            // Remove the rent from vehicle and client relationships
            if (vehicle != null && client != null) {
                vehicle.getRents().remove(rent);
                client.getRents().remove(rent);

                // Merge changes to vehicle and client
                em.merge(vehicle);
                em.merge(client);

                // Now remove the rent entity itself
                em.remove(rent);

                // Commit the transaction
                em.getTransaction().commit();
                return true;
            } else {
                em.getTransaction().rollback();
                return false;  // Either vehicle or client was not found
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;  // Re-throw the exception to be handled elsewhere
        } finally {
            em.close();
        }
    }

    @Override
    public Rent getById(Long id) {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {

            return em.find(Rent.class, id, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
        finally {
            em.close();
        }

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
