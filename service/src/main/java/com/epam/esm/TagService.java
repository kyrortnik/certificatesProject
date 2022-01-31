package com.epam.esm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService implements CRUDService<Tag> {

    @Autowired
    private TagRepository repository;

    @Override
    public Tag getCertificate(Long id) {
        return repository.getTag(id);
    }

    @Override
    public List<Tag> getAll(String order, int max) {

        return repository.getTags(order, max);
    }

    @Override
    public Tag create(Tag element) {
        return repository.create(element);
    }

    @Override
    public boolean delete(Long id) {
        return repository.delete(id);
    }

    @Override
    public boolean update(Tag element, Long id) {
        throw new UnsupportedOperationException();
    }


}
