package edu.kdmk.repositories;

import edu.kdmk.model.Client;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ClientRepository implements EntityRepository<Client> {

    private final EntityManager entityManager;

    @Override
    public Client add(Client item) {
        try{
            entityManager.getTransaction().begin();

            entityManager.persist(item);

            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            e.printStackTrace();
        }
        return item;
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
