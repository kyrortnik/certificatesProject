package com.epam.esm;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService implements CRUDService<Tag> {

    @Override
    public Tag getOne(Long id) {
        return null;
    }

    @Override
    public List<Tag> getAll(String order, int max) {
        return null;
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
