package com.epam.esm.impl;

import com.epam.esm.Tag;
import com.epam.esm.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class TagRepositoryJDBC implements TagRepository {


    private static final String GET_TAG = "SELECT id,name FROM tags WHERE id = ? LIMIT 1";

    private static final String GET_TAGS = "SELECT id, name FROM tags ORDER BY name %s LIMIT ?";

    private static final String GET_TAGS_FOR_CERTIFICATE =
            "SELECT tags.id, tags.name FROM tags\n" +
                    "LEFT JOIN certificates_tags AS ct\n" +
                    "ON tags.id = ct.tag_id\n" +
                    "LEFT JOIN certificates AS cert\n" +
                    "ON ct.certificate_id = cert.id WHERE cert.id = ?";

    private static final String DELETE_TAGS = "DELETE FROM tags WHERE id = ?";

    private static final String DELETE_TAG_RELATIONS = "DELETE FROM certificates_tags WHERE tag_id = ?";


    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;


    private static final RowMapper<Tag> MAPPER_TAG =
            (rs, i) -> new Tag(rs.getLong("id"),
                    rs.getString("name"));


    @Autowired
    public TagRepositoryJDBC(NamedParameterJdbcTemplate namedParameterJdbcTemplate, SimpleJdbcInsert simpleJdbcInsert) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleJdbcInsert = simpleJdbcInsert.withTableName("tags").usingGeneratedKeyColumns("id");
    }

    @Override
    public Tag getTag(Long id) {
        return namedParameterJdbcTemplate.getJdbcOperations().query(GET_TAG, rs -> rs.next() ? MAPPER_TAG.mapRow(rs, 1) : null, id);
    }


    @Override
    public List<Tag> getTags(String order, int max) {
        return namedParameterJdbcTemplate.getJdbcOperations().query(String.format(GET_TAGS, order), MAPPER_TAG, max);
    }

    @Transactional
    @Override
    public boolean delete(Long id) {
        namedParameterJdbcTemplate.getJdbcOperations().update(DELETE_TAG_RELATIONS, id);
        return namedParameterJdbcTemplate.getJdbcOperations().update(DELETE_TAGS, id) > 0;
    }

    @Override
    @Transactional
    public Tag create(Tag tag) {
        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(tag);
        long createdTagId = (Long) simpleJdbcInsert.executeAndReturnKey(source);
        return getTag(createdTagId);
    }

    @Override
    public List<Tag> getTagsForCertificate(Long id) {

        return namedParameterJdbcTemplate.getJdbcOperations().query(GET_TAGS_FOR_CERTIFICATE, MAPPER_TAG, id);
    }
}
