package com.epam.esm;

import java.util.List;

public interface CRUDService<E> {

    E getEntity(Long id);

    List<E> getEntities(String order, int max);

    boolean delete(Long id);

    boolean update(E element, Long id);

    E create(E element);

}
