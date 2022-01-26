package com.epam.esm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

public class TagRepositoryImpl implements TagRepository {


    private static final String FIND_ONE = "SELECT id,name FROM tags WHERE id = ? LIMIT 1";

    private static final String GET_ALL_TAGS = "SELECT id, name FROM tags ORDER BY name %s LIMIT ?";

    private static final String DELETE_TAGS = " DELETE FROM tags WHERE id = ? ";

    private static final String UPDATE_TAGS = "UPDATE tags " +
            "SET id = ?, name = ?, WHERE id = ?";




    private final JdbcOperations jdbcOperations;

    private static final RowMapper<Tag> MAPPER_TAG =
            (rs, i) -> new Tag(rs.getLong("id"),
                    rs.getString("name"));



    @Autowired
    public TagRepositoryImpl(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public Tag getOne(Long id) {
       return jdbcOperations.query(FIND_ONE, rs -> rs.next() ? MAPPER_TAG.mapRow(rs, 1) : null, id);
    }


    @Override
    public List<Tag> getAll(String order, int max){

        return jdbcOperations.query(String.format(GET_ALL_TAGS, order), MAPPER_TAG, max);

    }

    @Override
    public void delete(Long id) {

        jdbcOperations.update(DELETE_TAGS, id);
    }

    @Override
    public void update(Tag tag, Long id) {
         jdbcOperations.update(UPDATE_TAGS, tag,id);

    }

    @Override
    public Long create(Tag element) {
        return null;
    }
}
