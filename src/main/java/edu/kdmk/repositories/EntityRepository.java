package edu.kdmk.repositories;


import jakarta.persistence.EntityManager;

import java.util.List;


public interface EntityRepository<T> {

    T add(T item, EntityManager em);

    boolean remove(T item, EntityManager em);

    T update(T item, EntityManager em);

    T getById(Long id, EntityManager em);

    List<T> getAll(EntityManager em);

}
