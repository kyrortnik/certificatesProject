package com.epam.esm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagRepositoryImpl implements TagRepository {


    private static final String FIND_ONE = "SELECT id,name FROM tags WHERE id = ? LIMIT 1";

    private static final String GET_ALL_TAGS = "SELECT id, name FROM tags ORDER BY name %s LIMIT ?";

    private static final String DELETE_TAGS =
            "DELETE FROM certificates_tags WHERE tag_id = ?;\n"+
            "DELETE FROM tags WHERE id = ?;";

    private static final String UPDATE_TAGS = "UPDATE tags " +
            "SET id = ?, name = ?, WHERE id = ?";

    private static final String INSERT_TAGS = "INSERT INTO tags VALUES (DEFAULT, ?)";

    private static final String GET_CREATED_TAG_ID = " SELECT currval('tags_id_seq');";


    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    private static final RowMapper<Long> MAPPER_LOG =
            (rs, i) -> rs.getLong(1);


    private static final RowMapper<Tag> MAPPER_TAG =
            (rs, i) -> new Tag(rs.getLong("id"),
                    rs.getString("name"));


    @Autowired
    public TagRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Tag getTag(Long id) {
        return namedParameterJdbcTemplate.getJdbcOperations().query(FIND_ONE, rs -> rs.next() ? MAPPER_TAG.mapRow(rs, 1) : null, id);
    }


    @Override
    public List<Tag> getTags(String order, int max) {

        return namedParameterJdbcTemplate.getJdbcOperations().query(String.format(GET_ALL_TAGS, order), MAPPER_TAG, max);

    }

    @Override
    public boolean delete(Long id) {

        return namedParameterJdbcTemplate.getJdbcOperations().update(DELETE_TAGS, id,id) > 0;
    }

    @Override
    public void update(Tag tag, Long id) {
        namedParameterJdbcTemplate.getJdbcOperations().update(UPDATE_TAGS, tag, id);

    }

    @Override
    public Tag create(Tag tag) {
        namedParameterJdbcTemplate.getJdbcOperations().update(INSERT_TAGS, getParams(tag));
        Long createdTagId = namedParameterJdbcTemplate.query(GET_CREATED_TAG_ID, MAPPER_LOG).get(0);
        return getTag(createdTagId);
    }

    private Object[] getParams(Tag tag) {
        return new Object[]{
                tag.getName()
        };
    }
}
