package edu.kdmk.repositories.implemntations;

import edu.kdmk.model.Client;
import edu.kdmk.repositories.EntityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

import java.util.List;

public class ClientRepository implements EntityRepository<Client> {


    @Override
    public Client add(Client item, EntityManager em) {
        try {
            em.persist(item);
        } catch (Exception e) {
            throw e;
        }
        return em.find(Client.class, item.getId());
    }

    @Override
    public boolean remove(Client item, EntityManager em) {
        try {
            em.remove(item);
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Client update(Client item, EntityManager em) {

        try {
            if (item != null) {
                em.merge(item);
                return item;
            } else {
                em.getTransaction().rollback();
                return null;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Client getById(Long id, EntityManager em) {

        try {

            return em.find(Client.class, id, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        } catch (Exception e) {
            throw e;
        }
    }


    @Override
    public List<Client> getAll(EntityManager em) {


        try {

            return em.createQuery("SELECT c FROM Client c", Client.class).getResultList();

        } catch (Exception e) {
            throw e;
        }
    }
}
