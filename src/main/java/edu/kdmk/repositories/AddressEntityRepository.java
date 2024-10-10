package edu.kdmk.repositories;

import edu.kdmk.model.Address;

import java.util.List;

public class AddressEntityRepository implements EntityRepository<Address> {
    @Override
    public Address add(Address item) {
        return null;
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
