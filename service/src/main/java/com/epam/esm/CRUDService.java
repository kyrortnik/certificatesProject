package com.epam.esm;


import java.util.List;

public interface CRUDService<E> {

    E getCertificate(Long id);

    List<E> getAll(String order, int max);

    //    void delete(Long id);
    boolean delete(Long id);

    boolean update(E element, Long id);

    E create(E element);

}
