package com.epam.esm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class TagRepositoryImpl implements TagRepository {


    private static final String FIND_TAG = "SELECT id,name FROM tags WHERE id = ? LIMIT 1";

    private static final String GET_TAGS = "SELECT id, name FROM tags ORDER BY name %s LIMIT ?";

    private static final String DELETE_TAGS =
            "DELETE FROM certificates_tags WHERE tag_id = ?;\n" +
                    "DELETE FROM tags WHERE id = ?;";

//    private static final String UPDATE_TAGS = "UPDATE tags " +
//            "SET id = ?, name = ?, WHERE id = ?";

//    private static final String INSERT_TAGS = "INSERT INTO tags VALUES (DEFAULT, ?)";

//    private static final String GET_CREATED_TAG_ID = " SELECT currval('tags_id_seq');";


    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;


//    private static final RowMapper<Long> MAPPER_LOG =
//            (rs, i) -> rs.getLong(1);


    private static final RowMapper<Tag> MAPPER_TAG =
            (rs, i) -> new Tag(rs.getLong("id"),
                    rs.getString("name"));


    @Autowired
    public TagRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate, SimpleJdbcInsert simpleJdbcInsert) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleJdbcInsert =  simpleJdbcInsert.withTableName("tags").usingGeneratedKeyColumns("id");;
    }

    @Override
    public Tag getTag(Long id) {
        return namedParameterJdbcTemplate.getJdbcOperations().query(FIND_TAG, rs -> rs.next() ? MAPPER_TAG.mapRow(rs, 1) : null, id);
    }


    @Override
    public List<Tag> getTags(String order, int max) {
        return namedParameterJdbcTemplate.getJdbcOperations().query(String.format(GET_TAGS, order), MAPPER_TAG, max);
    }

    @Override
    public boolean delete(Long id) {
        return namedParameterJdbcTemplate.getJdbcOperations().update(DELETE_TAGS, id, id) > 0;
    }


    @Override
    @Transactional
    public Tag create(Tag tag) {
//        simpleJdbcInsert.withTableName("tags").usingGeneratedKeyColumns("id");
        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(tag);
        long createdTagId = (Integer)simpleJdbcInsert.executeAndReturnKey(source);
        return getTag(createdTagId);
    }

   /* private Object[] getParams(Tag tag) {
        return new Object[]{
                tag.getName()
        };
    }*/
}
