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

public class RentRepository implements EntityRepository<Rent> {

    @Override
    public Rent add(Rent item) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
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
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean remove(Rent item) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
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
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            return em.find(Rent.class, id);

        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
        finally {
            em.close();
        }

    }

    @Override
    public Rent update(Rent item) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            // Find the existing Rent entity in the database
            Rent existingRent = em.find(Rent.class, item.getId(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            if (existingRent == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Rent with ID " + item.getId() + " not found.");
            }

            // Get the current associated Vehicle and Client from the existing Rent
            Vehicle oldVehicle = existingRent.getVehicle();
            Client oldClient = existingRent.getClient();

            // Get the new Vehicle and Client from the updated Rent
            Vehicle newVehicle = em.find(Vehicle.class, item.getVehicle().getId(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            Client newClient = em.find(Client.class, item.getClient().getId(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);

            if (newVehicle == null || newClient == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("New vehicle or client not found.");
            }

            // Check if Vehicle or Client has changed
            boolean vehicleChanged = !oldVehicle.equals(newVehicle);
            boolean clientChanged = !oldClient.equals(newClient);

            // If Vehicle has changed, update the associations
            if (vehicleChanged) {
                // Remove the rent from the old vehicle's rent list
                oldVehicle.getRents().remove(existingRent);
                em.merge(oldVehicle);

                // Ensure the new vehicle can accept the rent
                if (newVehicle.getRents().size() > 1) {
                    em.getTransaction().rollback();
                    throw new IllegalArgumentException("New vehicle has too many rents.");
                }

                // Add the rent to the new vehicle's rent list
                newVehicle.getRents().add(item);
                em.merge(newVehicle);
            }

            // If Client has changed, update the associations
            if (clientChanged) {
                // Remove the rent from the old client's rent list
                oldClient.getRents().remove(existingRent);
                em.merge(oldClient);

                // Ensure the new client can accept the rent
                if (newClient.getRents().size() > 5) {
                    em.getTransaction().rollback();
                    throw new IllegalArgumentException("New client has too many rents.");
                }

                // Add the rent to the new client's rent list
                newClient.getRents().add(item);
                em.merge(newClient);
            }

            // Merge the updated Rent entity (including updated vehicle and client)
            Rent updatedRent = em.merge(item);

            // Commit the transaction
            em.getTransaction().commit();

            return updatedRent;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;  // Re-throw the exception to be handled by the caller
        } finally {
            em.close();
        }

    }

    @Override
    public List<Rent> getAll() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            return em.createQuery("SELECT c FROM Rent c", Rent.class).getResultList();

        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
