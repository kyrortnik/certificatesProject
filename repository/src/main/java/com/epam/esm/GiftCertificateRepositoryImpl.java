package com.epam.esm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {

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

    private final JdbcOperations jdbcOperations;


    private static final RowMapper<GiftCertificate> MAPPER_GIFT_CERTIFICATE =
            (rs, i) -> new GiftCertificate(rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getLong("price"),
                    rs.getLong("duration"),
                    rs.getString("create_date"),
                    rs.getString("last_update_date"),
                    new ArrayList<Tag>());

    private static final RowMapper<Tag> MAPPER_TAG =
            (rs, i) -> new Tag(
                    rs.getLong("id"),
                    rs.getString("name"));

    private static final RowMapper<Long> MAPPER_LOG =
            (rs, i) -> rs.getLong(1);

    @Autowired
    public GiftCertificateRepositoryImpl(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }


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
        return jdbcOperations.query(String.format(GET_CERTIFICATES_WITH_PARAMS, order), MAPPER_GIFT_CERTIFICATE, tag, pattern, pattern, max);
    }

    @Override
    public void delete(Long id) {
        jdbcOperations.update(DELETE_CERTIFICATE, id);
    }

    @Override
    public boolean update(GiftCertificate giftCertificate, Long id) {
        return jdbcOperations.update(UPDATE_CERTIFICATE, MAPPER_GIFT_CERTIFICATE) > 0;
    }


    @Override
    public GiftCertificate create(GiftCertificate giftCertificate) {

        /*Long id = jdbcOperations.queryForObject(INSERT_CERTIFICATE, MAPPER_GIFT_CERTIFICATE, getParams(giftCertificate)).getId();

        if (id != null) {
            return getOne(id);
        } else {
            return null;
        }

*/

        jdbcOperations.update(INSERT_CERTIFICATE,getParams(giftCertificate));
        Long createdCertificateId = jdbcOperations.query(" SELECT currval('certificates_id_seq');",MAPPER_LOG).get(0);
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
}
