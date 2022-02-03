package com.epam.esm;

import org.json.JSONArray;
import org.json.JSONObject;
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

import static java.util.Objects.isNull;

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

    private static final String FIND_ONE = "SELECT id,name, description, price, duration, to_char(create_date,'YYYY-MM-DD\"T\"HH24:MI:SS.MS') as create_date, to_char(last_update_date,'YYYY-MM-DD\"T\"HH24:MI:SS.MS') as last_update_date FROM certificates WHERE id = ? LIMIT 1";

//    private static final String FIND_ONE = "SELECT id,name, description, price, duration, to_timestamp(create_date,'YYYY-MM-DD HH:MI:SS.MS') as create_date, to_timestamp(last_update_date,'YYYY-MM-DD HH:MI:SS.MS') as last_update_date FROM certificates WHERE id = ? LIMIT 1";



    private static final String INSERT_CERTIFICATE = "INSERT INTO certificates (id, name, description, price, duration, create_date, last_update_date)" +
            "VALUES (DEFAULT, :name, :description, :price, :duration, :create_date, :last_update_date)";

    private static final String DELETE_CERTIFICATE = " DELETE FROM certificates WHERE id = ? ";

    private static final String UPDATE_CERTIFICATE = "UPDATE certificates " +
            "SET name = :name, description = :description, price = :price, duration = :duration, create_date = :create_date, last_update_date = :last_update_date\n" +
            "WHERE id = :id;";

    private static final String GET_ALL_CERTIFICATES = "SELECT id, name,description, price, duration, create_date, last_update_date FROM certificates ORDER BY name %s LIMIT ?";


    private static final String GET_CERTIFICATES_WITH_PARAMS =
            "SELECT  cert.id,  cert.name,  cert.description,  cert.price,  cert.duration,  cert.create_date,  cert.last_update_date, tags.id, tags.name FROM certificates AS cert\n" +
                    "LEFT OUTER JOIN certificates_tags  AS ct ON cert.id =  ct.certificate_id\n" +
                    "LEFT OUTER JOIN tags ON ct.tag_id = tags.id WHERE tags.name = ? AND cert.name LIKE ? OR cert.description LIKE ? ORDER BY cert.name %s LIMIT ?";

    private static final String TAGS_FOR_CERTIFICATE = "SELECT tags.id, tags.name FROM tags\n" +
            "  LEFT OUTER JOIN certificates_tags AS ct ON tags.id = ct.tag_id\n" +
            "  LEFT OUTER JOIN certificates AS cert ON ct.certificate_id = cert.id WHERE cert.id = ?";

    private static final String GET_CREATED_CERTIFICATE_ID = " SELECT currval('certificates_id_seq');";

    private static final String TEST_AGG =
            "SELECT cert.id, cert.name, cert.description, cert.price, cert.duration, cert.create_date, cert.last_update_date,\n" +
                    "array_to_json(array_agg(tags)) as tags\n" +
                    "FROM \n" +
                    "certificates AS cert \n" +
                    "LEFT JOIN certificates_tags AS ct \n" +
                    "ON cert.id = ct.certificate_id\n" +
                    "LEFT JOIN tags \n" +
                    "ON ct.tag_id = tags.id\n" +
                    "GROUP BY cert.id, cert.name,cert.description,cert.price,cert.duration,cert.create_date, cert.last_update_date\n" +
                    "ORDER BY cert.id ASC";

    private static final String TAGS_FOR_CERTIFICATES = "SELECT array_to_json(array_agg(tags)) as tags\n" +
            "FROM \n" +
            "certificates AS cert \n" +
            "LEFT JOIN certificates_tags AS ct \n" +
            "ON cert.id = ct.certificate_id\n" +
            "LEFT JOIN tags \n" +
            "ON ct.tag_id = tags.id \n" +
            "GROUP BY cert.id, tags.id\n" +
            "ORDER BY cert.id ASC";

    private static final String GET_TAGS_IDS = "SELECT * FROM get_tags_ids(?)";

    private static final String CREATE_NEW_TAGS_CALL = "{call create_new_tags(?)}";

    private static final String CREATE_CERTIFICATE_TAG_RELATION = "{call create_cert_tag_relation(?,?)}";

    private static final RowMapper<GiftCertificate> MAPPER_GIFT_CERTIFICATE =
            (rs, i) -> new GiftCertificate(rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getLong("price"),
                    rs.getLong("duration"),
                    LocalDateTime.parse(rs.getString("create_date"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    LocalDateTime.parse(rs.getString("last_update_date"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));



    private static final RowMapper<Tag> MAPPER_TAG =
            (rs, i) -> new Tag(
                    rs.getLong("id"),
                    rs.getString("name"));

    private static final RowMapper<Integer> MAPPER_ID =
            (rs, i) -> rs.getInt(1);


    @Override
    public GiftCertificate getCertificate(Long id) {

        GiftCertificate giftCertificate = namedParameterJdbcTemplate.getJdbcOperations().query(FIND_ONE, rs -> rs.next() ? MAPPER_GIFT_CERTIFICATE.mapRow(rs, 1) : null, id);
        List<Tag> tags = namedParameterJdbcTemplate.getJdbcOperations().query(TAGS_FOR_CERTIFICATE, MAPPER_TAG, id);

        if (giftCertificate != null) {
            giftCertificate.setTags(tags);
        }
        return giftCertificate;
    }

    //TODO Deprecated
    @Override
    public List<GiftCertificate> getCertificates(String order, int max) {

        return namedParameterJdbcTemplate.getJdbcOperations().query(String.format(GET_ALL_CERTIFICATES, order), MAPPER_GIFT_CERTIFICATE, max);
    }

    //TODO use params
    @Override
    public List<GiftCertificate> getAllWithParams(String order, int max, String tag, String pattern) {

        return namedParameterJdbcTemplate.query(TEST_AGG, MAPPER_GIFT_CERTIFICATE);


//        List<GiftCertificate> certificates =  jdbcOperations.query(String.format(GET_CERTIFICATES_WITH_PARAMS, order), MAPPER_GIFT_CERTIFICATE, tag, pattern, pattern, max);
//        List<Tag> tags = jdbcOperations.query(TAGS_FOR_CERTIFICATES,MAPPER_TAG);

//        return null;
    }

    @Override
    public boolean delete(Long id) {
        return namedParameterJdbcTemplate.getJdbcOperations().update(DELETE_CERTIFICATE, id) > 0;
    }

    @Override
    public boolean update(GiftCertificate giftCertificate, Long id) {

        GiftCertificate existingCertificate = getCertificate(id);
        updateExistingCertificate(giftCertificate, existingCertificate);

        Map<String, Object> map = getParamsMap(existingCertificate);
        map.put("id", id);
        return namedParameterJdbcTemplate.update(UPDATE_CERTIFICATE, map) > 0;
    }

    private void updateExistingCertificate(GiftCertificate updateCertificate, GiftCertificate existingCertificate) {
        existingCertificate.setName(isNull(updateCertificate.getName()) ? existingCertificate.getName() : updateCertificate.getName());
        existingCertificate.setDescription(isNull(updateCertificate.getDescription()) ? existingCertificate.getDescription() : updateCertificate.getDescription());
        existingCertificate.setPrice(isNull(updateCertificate.getPrice()) ? existingCertificate.getPrice() : updateCertificate.getPrice());
        existingCertificate.setDuration(isNull(updateCertificate.getDuration()) ? existingCertificate.getDuration() : updateCertificate.getDuration());
        existingCertificate.setCreateDate(isNull(updateCertificate.getCreateDate()) ? existingCertificate.getCreateDate() : updateCertificate.getCreateDate());
        existingCertificate.setLastUpdateDate(isNull(updateCertificate.getLastUpdateDate()) ? existingCertificate.getLastUpdateDate() : updateCertificate.getLastUpdateDate());
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

    private Object[] getParams(GiftCertificate giftCertificate) {
        return new Object[]{
                giftCertificate.getName(),
                giftCertificate.getDescription(),
                giftCertificate.getPrice(),
                giftCertificate.getDuration(),
                giftCertificate.getCreateDate(),
                giftCertificate.getLastUpdateDate()
        };
    }


    private static ArrayList<Tag> jsonToTagList(String json) {
        String processed = String.copyValueOf(json.toCharArray(), 1, json.length() - 2);
        JSONObject jsnobject = new JSONObject(processed);
//        JSONObject jsnobject = new JSONObject(json);
        JSONArray jsonArray = jsnobject.getJSONArray("tags");
        ArrayList<Tag> resultList = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                resultList.add((Tag) jsonArray.get(i));
            }
        }
        return resultList;

    }
}
