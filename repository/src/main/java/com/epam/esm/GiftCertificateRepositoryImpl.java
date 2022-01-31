package com.epam.esm;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


import java.util.ArrayList;
import java.util.List;

@Repository
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {

    private final JdbcOperations jdbcOperations;

    @Autowired
    public GiftCertificateRepositoryImpl(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    private static final String FIND_ONE = "SELECT id,name, description, price, duration, create_date, last_update_date FROM certificates WHERE id = ? LIMIT 1";


    private static final String INSERT_CERTIFICATE = "INSERT INTO certificates (id, name, description, price, duration, create_date, last_update_date)" +
            "VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)";

    private static final String DELETE_CERTIFICATE = " DELETE FROM certificates WHERE id = ? ";

    private static final String UPDATE_CERTIFICATE = "UPDATE certificates " +
            "SET id = ?, name = ?, description = ?, price = ?, duration = ?, create_date = ?, last_update_date = ?" +
            "WHERE id = ?;";

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
                    rs.getString("create_date"),
                    rs.getString("last_update_date"),
                    jsonToTagList(rs.getString("tags")));

    private static final RowMapper<Tag> MAPPER_TAG =
            (rs, i) -> new Tag(
                    rs.getLong("id"),
                    rs.getString("name"));

    private static final RowMapper<Long> MAPPER_LOG =
            (rs, i) -> rs.getLong(1);


    @Override
    public GiftCertificate getOne(Long id) {

        GiftCertificate giftCertificate = jdbcOperations.query(FIND_ONE, rs -> rs.next() ? MAPPER_GIFT_CERTIFICATE.mapRow(rs, 1) : null, id);
        List<Tag> tags = jdbcOperations.query(TAGS_FOR_CERTIFICATE, MAPPER_TAG, id);

        if (giftCertificate != null) {
            giftCertificate.setTags(tags);
        }
        return giftCertificate;
    }

    //TODO Deprecated
    @Override
    public List<GiftCertificate> getCertificates(String order, int max) {

        return jdbcOperations.query(String.format(GET_ALL_CERTIFICATES, order), MAPPER_GIFT_CERTIFICATE, max);
    }

    @Override
    public List<GiftCertificate> getAllWithParams(String order, int max, String tag, String pattern) {

        return jdbcOperations.query(TEST_AGG, MAPPER_GIFT_CERTIFICATE);


//        List<GiftCertificate> certificates =  jdbcOperations.query(String.format(GET_CERTIFICATES_WITH_PARAMS, order), MAPPER_GIFT_CERTIFICATE, tag, pattern, pattern, max);
//        List<Tag> tags = jdbcOperations.query(TAGS_FOR_CERTIFICATES,MAPPER_TAG);

//        return null;
    }

    @Override
    public boolean delete(Long id) {
        return jdbcOperations.update(DELETE_CERTIFICATE, id) > 0;
    }

    @Override
    public boolean update(GiftCertificate giftCertificate, Long id) {
        return jdbcOperations.update(UPDATE_CERTIFICATE, MAPPER_GIFT_CERTIFICATE) > 0;
    }


    @Override
    public GiftCertificate create(GiftCertificate giftCertificate) {
        jdbcOperations.update(INSERT_CERTIFICATE, getParams(giftCertificate));
        Long createdCertificateId = jdbcOperations.query(GET_CREATED_CERTIFICATE_ID, MAPPER_LOG).get(0);
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
