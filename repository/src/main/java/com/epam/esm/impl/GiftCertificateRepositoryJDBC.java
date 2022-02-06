package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import com.epam.esm.GiftCertificateRepository;
import com.epam.esm.Tag;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Profile("prod")
@Repository
public class GiftCertificateRepositoryJDBC implements GiftCertificateRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    @Autowired
    public GiftCertificateRepositoryJDBC(NamedParameterJdbcTemplate namedParameterJdbcTemplate, SimpleJdbcInsert simpleJdbcInsert) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.simpleJdbcInsert = simpleJdbcInsert.withTableName("certificates").usingGeneratedKeyColumns("id");
    }

    private static final String FIND_ONE = "SELECT cert.id, cert.name, cert.description, cert.price, cert.duration, cert.create_date,\n" +
            "cert.last_update_date, array_to_json(array_agg(tags)) as tags\n" +
            "FROM certificates as cert\n" +
            "LEFT JOIN certificates_tags AS ct\n" +
            "ON cert.id = ct.certificate_id\n" +
            "LEFT JOIN tags\n" +
            "ON ct.tag_id = tags.id  WHERE cert.id = ? \n" +
            "GROUP BY cert.id, cert.name,cert.description,cert.price,cert.duration,cert.create_date, cert.last_update_date\n" +
            "ORDER BY cert.name ASC LIMIT 1";

    private static final String DELETE_CERTIFICATE_RELATION = "DELETE FROM certificates_tags WHERE certificate_id = ?";

    private static final String DELETE_CERTIFICATE = " DELETE FROM certificates WHERE id = ? ";


    private static final String UPDATE_CERTIFICATE = "UPDATE certificates \n" +
            "SET name = COALESCE(:name, name), description = COALESCE(:description, description), price = COALESCE(:price, price),\n" +
            "duration = COALESCE(:duration, duration), create_date = COALESCE(:create_date, create_date), last_update_date = COALESCE(:last_update_date,last_update_date) \n" +
            "WHERE id = :id";

    private static final String GET_ALL_CERTIFICATES = "SELECT id, name,description, price, duration, create_date, last_update_date FROM certificates ORDER BY name %s LIMIT ?";

    private static final String DELETE_OBSOLETE_RELATIONS = "DELETE FROM certificates_tags WHERE certificate_id = ?";


    private static final String TEST_AGG = "SELECT cert.id, cert.name, cert.description, cert.price, cert.duration, cert.create_date, cert.last_update_date, array_to_json(array_agg(tags)) as tags\n" +
            "FROM\n" +
            "certificates AS cert\n" +
            "LEFT JOIN certificates_tags AS ct\n" +
            "ON cert.id = ct.certificate_id\n" +
            "LEFT JOIN tags\n" +
            "ON ct.tag_id = tags.id  WHERE  tags.name = COALESCE(:tag, tags.name) AND (cert.name LIKE COALESCE(:pattern, cert.name) OR cert.description LIKE COALESCE(:pattern, cert.description))\n" +
            "GROUP BY cert.id, cert.name,cert.description,cert.price,cert.duration,cert.create_date, cert.last_update_date\n" +
            "ORDER BY cert.name %s LIMIT :max";


    private static final String GET_TAGS_IDS = "SELECT * FROM get_tags_ids(?)";

    private static final String CREATE_NEW_TAGS_CALL = "{call create_new_tags(?)}";

    private static final String CREATE_CERTIFICATE_TAG_RELATION = "{call create_cert_tag_relation(?,?)}";

    private static final RowMapper<GiftCertificate> MAPPER_GIFT_CERTIFICATE =
            (rs, i) -> new GiftCertificate(rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getLong("price"),
                    rs.getLong("duration"),
                    LocalDateTime.parse(rs.getString("create_date").replace(" ", "T"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    LocalDateTime.parse(rs.getString("last_update_date").replace(" ", "T"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    tagsToList(rs.getString("tags"))
            );


    private static final RowMapper<Integer> MAPPER_ID =
            (rs, i) -> rs.getInt(1);


    @Override
    public GiftCertificate getCertificate(Long id) {

        return namedParameterJdbcTemplate.getJdbcOperations().query(FIND_ONE, rs -> rs.next() ? MAPPER_GIFT_CERTIFICATE.mapRow(rs, 1) : null, id);
    }

    @Override
    public List<GiftCertificate> getCertificates(String order, int max) {

        return namedParameterJdbcTemplate.getJdbcOperations().query(String.format(GET_ALL_CERTIFICATES, order), MAPPER_GIFT_CERTIFICATE, max);
    }

    @Override
    public List<GiftCertificate> getAllWithParams(String order, int max, String tag, String pattern) {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("max", max);
        paramMap.put("tag", tag);
        paramMap.put("pattern", pattern);

        return namedParameterJdbcTemplate.query(String.format(TEST_AGG, order), paramMap, MAPPER_GIFT_CERTIFICATE);

    }

    @Transactional
    @Override
    public boolean delete(Long id) {
        namedParameterJdbcTemplate.getJdbcOperations().update(DELETE_CERTIFICATE_RELATION, id);
        return namedParameterJdbcTemplate.getJdbcOperations().update(DELETE_CERTIFICATE, id) > 0;
    }


    @Override
    public boolean update(GiftCertificate giftCertificate, long certificateId) {

        boolean result;
        Map<String, Object> map = getParamsMap(giftCertificate);
        map.put("id", certificateId);
        result = namedParameterJdbcTemplate.update(UPDATE_CERTIFICATE, map) > 0;
        List<Tag> tags = giftCertificate.getTags();

        if (!tags.isEmpty()) {
            List<String> tagNames = new ArrayList<>();
            tags.forEach((t) -> tagNames.add(t.getName()));
            createNewTags(tagNames);
            namedParameterJdbcTemplate.getJdbcOperations().update(DELETE_OBSOLETE_RELATIONS, certificateId);
            List<Integer> list = getTagIdsForNames(tagNames);
            createCertificateTagRelation((int) certificateId, list);
        }
        return result;
    }



    @Transactional
    @Override
    public GiftCertificate create(GiftCertificate giftCertificate) {

        BeanPropertySqlParameterSource source = new BeanPropertySqlParameterSource(giftCertificate);
        long createdGiftCertificateId = (Integer) simpleJdbcInsert.executeAndReturnKey(source);
        List<Tag> tags = giftCertificate.getTags();

        if (!tags.isEmpty()) {
            List<String> tagNames = new ArrayList<>();
            tags.forEach((t) -> tagNames.add(t.getName()));
            createNewTags(tagNames);
            List<Integer> list = getTagIdsForNames(tagNames);
            createCertificateTagRelation((int) createdGiftCertificateId, list);
        }
        return getCertificate(createdGiftCertificateId);
    }


    private void createNewTags(List<String> tagNames) {
        List<SqlParameter> parameters = new ArrayList<>();
        namedParameterJdbcTemplate.getJdbcOperations().call(con -> {
                    CallableStatement cs = con.prepareCall(CREATE_NEW_TAGS_CALL);
                    cs.setArray(1, con.createArrayOf("varchar", tagNames.toArray()));
                    return cs;
                },
                parameters
        );
    }

    private List<Integer> getTagIdsForNames(List<String> tagNames) {
        return namedParameterJdbcTemplate.getJdbcOperations().query(con -> {
                    PreparedStatement ps = con.prepareStatement(GET_TAGS_IDS);
                    ps.setArray(1, con.createArrayOf("varchar", tagNames.toArray()));
                    return ps;
                }, MAPPER_ID
        );
    }

    private void createCertificateTagRelation(int createdGiftId, List<Integer> list) {
        List<SqlParameter> parameters = new ArrayList<>();
        namedParameterJdbcTemplate.getJdbcOperations().call(con -> {
                    CallableStatement cs = con.prepareCall(CREATE_CERTIFICATE_TAG_RELATION);
                    cs.setArray(1, con.createArrayOf("integer", list.toArray()));
                    cs.setInt(2, createdGiftId);
                    return cs;
                }, parameters
        );
    }


    private Map<String, Object> getParamsMap(GiftCertificate giftCertificate) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", giftCertificate.getName());
        map.put("description", giftCertificate.getDescription());
        map.put("price", giftCertificate.getPrice());
        map.put("duration", giftCertificate.getDuration());
        map.put("create_date", giftCertificate.getCreateDate());
        map.put("last_update_date", giftCertificate.getLastUpdateDate());
        return map;
    }

    private static List<Tag> tagsToList(String jsonTags) {
        List<Tag> tagsList = null;
        try {
            ObjectMapper mapper = new ObjectMapper();

            tagsList = mapper.readValue(jsonTags, new TypeReference<List<Tag>>() {
            });
        } catch (JsonProcessingException ex) {
            System.out.println("JsonParsing exception");
        }
        return tagsList;
    }
}
