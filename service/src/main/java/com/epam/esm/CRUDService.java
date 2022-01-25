package com.epam.esm;



public interface CRUDService<E> {

    E getOne(Long id);

    void delete(Long id);

    boolean update(E element, Long id);

    E create(E element);

}
