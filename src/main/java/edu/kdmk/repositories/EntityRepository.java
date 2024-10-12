package edu.kdmk.repositories;


import java.util.List;


public interface EntityRepository<T> {

    T add(T item);

    boolean remove(T item);

    T getById(Long id);

    T update(T item);

    List<T> getAll();

}
