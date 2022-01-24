package com.epam.esm;



public interface CRUDService<E> {

    E getOne(Long id);

    void delete(Long id);

    void update(E element);

    E create(E element);

}
