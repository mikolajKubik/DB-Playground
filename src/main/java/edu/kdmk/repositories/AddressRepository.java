package edu.kdmk.repositories;

import edu.kdmk.model.Address;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AddressRepository implements EntityRepository<Address> {

    private final EntityManager entityManager;

    @Override
    public Address add(Address item) {
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
    public boolean remove(Address item) {
        return false;
    }

    @Override
    public Address getById(Long id) {
        return null;
    }

    @Override
    public Address update(Address item) {
        return null;
    }

    @Override
    public List<Address> getAll() {
        return List.of();
    }
}
