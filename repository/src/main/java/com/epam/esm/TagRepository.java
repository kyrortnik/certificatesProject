package com.epam.esm;

import java.util.List;

public interface TagRepository {


    Tag getOne(Long id);

    List<Tag> getAll(String order, int max);

    public void delete(Long id);


    public void update(Tag element,  Long id);


    public Long create(Tag element);
}
