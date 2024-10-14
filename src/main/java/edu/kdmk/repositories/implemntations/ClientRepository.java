package edu.kdmk.repositories.implemntations;

import edu.kdmk.model.Client;
import edu.kdmk.repositories.EntityRepository;
import edu.kdmk.repositories.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

import java.util.List;

public class ClientRepository implements EntityRepository<Client> {


    @Override
    public Client add(Client item) {
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
    public boolean remove(Client item) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
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
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Client getById(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            return em.find(Client.class, id);

        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
        finally {
            em.close();
        }
    }

    @Override
    public Client update(Client item) {

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try  {
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
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }

    }

    @Override
    public List<Client> getAll() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {

            return em.createQuery("SELECT c FROM Client c", Client.class).getResultList();

        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return List.of();
    }
}
