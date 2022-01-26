package com.epam.esm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService implements CRUDService<Tag> {

    @Autowired
    private TagRepository repository;

    @Override
    public Tag getOne(Long id) {
        return repository.getOne(id);
    }

    @Override
    public List<Tag> getAll(String order, int max) {

        return repository.getAll(order,max);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public boolean update(Tag element, Long id) {

        return false;
    }

    @Override
    public Tag create(Tag element) {
        return null;
    }

}
