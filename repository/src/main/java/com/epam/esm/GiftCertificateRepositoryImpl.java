package com.epam.esm;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {

//    private final JdbcOperations jdbcOperations;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public GiftCertificateRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private static final String FIND_ONE = "SELECT id,name, description, price, duration, to_char(create_date,'YYYY-MM-DD\"T\"HH24:MI:SS.MS') as create_date, to_char(last_update_date,'YYYY-MM-DD\"T\"HH24:MI:SS.MS') as last_update_date FROM certificates WHERE id = ? LIMIT 1";


    private static final String INSERT_CERTIFICATE = "INSERT INTO certificates (id, name, description, price, duration, create_date, last_update_date)" +
            "VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)";

    private static final String DELETE_CERTIFICATE = " DELETE FROM certificates WHERE id = ? ";

   /* private static final String UPDATE_CERTIFICATE = "UPDATE certificates " +
            "SET name = ?, description = ?, price = ?, duration = ?, create_date = ?, last_update_date = ?" +
            "WHERE id = ?;";*/

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

    private static final RowMapper<GiftCertificate> MAPPER_GIFT_CERTIFICATE =
            (rs, i) -> new GiftCertificate(rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getLong("price"),
                    rs.getLong("duration"),
                    LocalDateTime.parse(rs.getString("create_date"),DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    LocalDateTime.parse(rs.getString("last_update_date"),DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    private static final RowMapper<Tag> MAPPER_TAG =
            (rs, i) -> new Tag(
                    rs.getLong("id"),
                    rs.getString("name"));

    private static final RowMapper<Long> MAPPER_LOG =
            (rs, i) -> rs.getLong(1);


    @Override
    public GiftCertificate getOne(Long id) {

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

        GiftCertificate existingCertificate = getOne(id);
        updateExistingCertificate(giftCertificate, existingCertificate);

        Map<String,Object> map = new HashMap<>();
        map.put("name",existingCertificate.getName());
        map.put("description",existingCertificate.getDescription());
        map.put("price",existingCertificate.getPrice());
        map.put("duration",existingCertificate.getDuration());
        map.put("create_date",existingCertificate.getCreateDate());
        map.put("last_update_date",existingCertificate.getLastUpdateDate());
        map.put("id",id);
        return namedParameterJdbcTemplate.update(UPDATE_CERTIFICATE,map) > 0;
    }

    private void updateExistingCertificate(GiftCertificate updateCertificate, GiftCertificate existingCertificate) {
        existingCertificate.setName(isNull(updateCertificate.getName()) ? existingCertificate.getName() : updateCertificate.getName());
        existingCertificate.setDescription(isNull(updateCertificate.getDescription()) ? existingCertificate.getDescription() : updateCertificate.getDescription());
        existingCertificate.setPrice(isNull(updateCertificate.getPrice())? existingCertificate.getPrice() : updateCertificate.getPrice());
        existingCertificate.setDuration(isNull(updateCertificate.getDuration())? existingCertificate.getDuration() : updateCertificate.getDuration());
        existingCertificate.setCreateDate(isNull(updateCertificate.getCreateDate())? existingCertificate.getCreateDate() : updateCertificate.getCreateDate());
        existingCertificate.setLastUpdateDate(isNull(updateCertificate.getLastUpdateDate())? existingCertificate.getLastUpdateDate() : updateCertificate.getLastUpdateDate());
    }


    @Override
    public GiftCertificate create(GiftCertificate giftCertificate) {
        namedParameterJdbcTemplate.getJdbcOperations().update(INSERT_CERTIFICATE, getParams(giftCertificate));
        Long createdCertificateId = namedParameterJdbcTemplate.query(GET_CREATED_CERTIFICATE_ID, MAPPER_LOG).get(0);
        return getOne(createdCertificateId);
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
