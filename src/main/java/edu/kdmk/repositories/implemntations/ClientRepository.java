package edu.kdmk.repositories.implemntations;

import edu.kdmk.model.Client;
import edu.kdmk.repositories.EntityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ClientRepository implements EntityRepository<Client> {

    private final EntityManagerFactory entityManagerFactory;

    @Override
    public Client add(Client item) {


        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            em.getTransaction().begin();

            em.persist(item);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return item;
    }

    @Override
    public boolean remove(Client item) {
        try (EntityManager em = entityManagerFactory.createEntityManager()){
            em.getTransaction().begin();
            Client clientToRemove = em.find(Client.class, item.getId(),
                    LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            if (clientToRemove != null) {
                em.remove(clientToRemove);
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
    public Client getById(Long id) {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {

            return em.find(Client.class, id);

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
    public Client update(Client item) {

        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            em.getTransaction().begin();

            Client existingClient = em.find(Client.class, item.getId(), LockModeType.OPTIMISTIC);
            if (existingClient != null) {
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
    public List<Client> getAll() {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {

            return em.createQuery("SELECT c FROM Client c", Client.class).getResultList();

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
