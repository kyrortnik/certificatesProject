package com.epam.esm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService implements CRUDService<Tag> {


    private final TagRepository tagRepository;

    @Autowired
    public TagService (TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag getEntity(Long id) {
        return tagRepository.getTag(id);
    }

    @Override
    public List<Tag> getEntities(String order, int max) {

        return tagRepository.getTags(order, max);
    }

    @Override
    public Tag create(Tag element) {
        return tagRepository.create(element);
    }

    @Override
    public boolean delete(Long id) {
        return tagRepository.delete(id);
    }

    @Override
    public boolean update(Tag element, Long id) {
        throw new UnsupportedOperationException();
    }

}
