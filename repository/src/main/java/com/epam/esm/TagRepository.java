package com.epam.esm;

public interface TagRepository {


    Tag getOne(Long id);

    public void delete(Long id);


    public void update(Tag element);


    public Long create(Tag element);
}
