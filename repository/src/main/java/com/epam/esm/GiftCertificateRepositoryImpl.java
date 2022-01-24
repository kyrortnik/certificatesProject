package com.epam.esm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {

    private static final String FIND_ONE = "SELECT id,name, description, price, duration, create_date, last_update_date FROM certificates WHERE id = ?";


    private static final String INSERT_CERTIFICATE = "INSERT INTO certificates (id, name, description, price, duration, create_date, last_update_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private final JdbcOperations jdbcOperations;


    @Autowired
    public GiftCertificateRepositoryImpl(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public GiftCertificate getOne(Long id) {

        return jdbcOperations.queryForObject(FIND_ONE, (rs, rowNum) -> new GiftCertificate
                (rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getLong("price"),
                        rs.getLong("duration"),
                        rs.getString("create_date"),
                        rs.getString("last_update_date")
                ), id);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void update(GiftCertificate element) {

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
