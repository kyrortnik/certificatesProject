package com.epam.esm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {

    private static final String FIND_ONE = "SELECT id,name, description, price, duration, create_date, last_update_date FROM certificates WHERE id = ? LIMIT 1";

    private static final String INSERT_CERTIFICATE = "INSERT INTO certificates (id, name, description, price, duration, create_date, last_update_date)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String DELETE_CERTIFICATE = " DELETE FROM certificates WHERE id = ? ";

    private static final String UPDATE_CERTIFICATE = "UPDATE certificates " +
            "SET id = ?, name = ?, description = ?, price = ?, duration = ?, create_date = ?, last_update_date = ?" +
            "WHERE id = ?;";
    private final JdbcOperations jdbcOperations;


    private static final RowMapper<GiftCertificate> MAPPER_GIFT_CERTIFICATE =
            (rs, i) -> new GiftCertificate(rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getLong("price"),
                    rs.getLong("duration"),
                    rs.getString("create_date"),
                    rs.getString("last_update_date"));

    @Autowired
    public GiftCertificateRepositoryImpl(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }


    /*
    * check of whether result set will have one row at least so no exception is thrown
    *
    * https://stackoverflow.com/questions/10606229/jdbctemplate-query-for-string-emptyresultdataaccessexception-incorrect-result
    * */
    @Override
    public GiftCertificate getOne(Long id) {
        return jdbcOperations.query(FIND_ONE, rs -> rs.next() ? MAPPER_GIFT_CERTIFICATE.mapRow(rs, 1) : null, id);
    }

    @Override
    public boolean delete(Long id) {

        return jdbcOperations.update(DELETE_CERTIFICATE, id) > 0;

    }

    @Override
    public boolean update(GiftCertificate giftCertificate, Long id) {
        Object[] array = new Object[]{
                giftCertificate.getId(),
                giftCertificate.getName(),
                giftCertificate.getDescription(),
                giftCertificate.getPrice(),
                giftCertificate.getDuration(),
                giftCertificate.getCreateDate(),
                giftCertificate.getLastUpdateDate(),
                id};
        return jdbcOperations.update(UPDATE_CERTIFICATE, array) > 0;

    }

    @Override
    public GiftCertificate create(GiftCertificate giftCertificate) {
        Object[] array = new Object[]{
                giftCertificate.getId(),
                giftCertificate.getName(),
                giftCertificate.getDescription(),
                giftCertificate.getPrice(),
                giftCertificate.getDuration(),
                giftCertificate.getCreateDate(),
                giftCertificate.getLastUpdateDate()};

        jdbcOperations.update(INSERT_CERTIFICATE, array);
        return new GiftCertificate(
                giftCertificate.getId(),
                giftCertificate.getName(),
                giftCertificate.getDescription(),
                giftCertificate.getPrice(),
                giftCertificate.getDuration(),
                giftCertificate.getCreateDate(),
                giftCertificate.getLastUpdateDate());


    }
}
