package edu.kdmk.repositories;

import edu.kdmk.model.Address;
import edu.kdmk.model.Client;
import edu.kdmk.model.Rent;
import edu.kdmk.model.vehicle.Motorcycle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClientRepository implements EntityRepository<Client> {

    @Autowired
    private EntityManager em;

    @Override
    public Client add(Client item) {
//        try {
//            EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");
//            EntityManager em = emf.createEntityManager();
//            em.getTransaction().begin();
//
//            em.persist(item);
//
//            em.getTransaction().commit();
//        } catch (Exception e) {
//            em.getTransaction().rollback();
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
//        return item;
        return null;
    }

    @Override
    public boolean remove(Client item) {
        return false;
    }

    @Override
    public Client getById(Long id) {
        return null;
    }

    @Override
    public Client update(Client item) {
        return null;
    }

    @Override
    public List<Client> getAll() {
        return List.of();
    }
}
