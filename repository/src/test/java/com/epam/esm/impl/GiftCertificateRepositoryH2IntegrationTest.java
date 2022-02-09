package com.epam.esm.impl;

import com.epam.esm.GiftCertificate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig
@ActiveProfiles("dev")
class GiftCertificateRepositoryH2IntegrationTest {

    @Configuration
    static class Config {

        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .addScript("classpath:schema.sql")
                    .addScript("classpath:test-data.sql")
                    .build();
        }

        @Bean
        public GiftCertificateRepositoryH2 giftCertificateRepositoryH2(NamedParameterJdbcTemplate namedParameterJdbcTemplate, SimpleJdbcInsert simpleJdbcInsert) {
            return new GiftCertificateRepositoryH2(namedParameterJdbcTemplate, simpleJdbcInsert);
        }

        @Bean
        public NamedParameterJdbcTemplate namedParameterjdbcTemplate(DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }

        @Bean
        public SimpleJdbcInsert simpleJdbcInsert(DataSource dataSource) {
            return new SimpleJdbcInsert(dataSource);
        }
    }


    @Autowired
    private GiftCertificateRepositoryH2 giftCertificateRepositoryH2;


    //params
    private final long giftCertificateId = 1L;
    private final String order = "ASC";
    private final int max = 20;

    @Test
    public void testGetCertificate_integration_positive() {
        GiftCertificate found = giftCertificateRepositoryH2.getCertificate(giftCertificateId);

        assertNotNull(found);
    }

    @Test
    void testGetCertificates_integration_positive() {
        List<GiftCertificate> giftCertificates = giftCertificateRepositoryH2.getCertificates(order, max);

        assertNotNull(giftCertificates);
        assertFalse(giftCertificates.isEmpty());

    }

    @Test
    void testGetAllWithParams_integration_positive() {
        List<GiftCertificate> giftCertificates = giftCertificateRepositoryH2.getCertificatesWithParams(order, max, null, null);

        assertNotNull(giftCertificates);
        assertFalse(giftCertificates.isEmpty());
    }


    @Test
    void testDelete_integration_positive() {
        GiftCertificate giftCertificateToDelete = new GiftCertificate("name to delete", "description to delete", 1500L, 90L, LocalDateTime.now(), LocalDateTime.now());
        GiftCertificate createdGiftCertificate = giftCertificateRepositoryH2.create(giftCertificateToDelete);

        boolean result = giftCertificateRepositoryH2.delete(createdGiftCertificate.getId());
        assertTrue(result);
    }

    @Test
    void testUpdate_integration_positive() {
        GiftCertificate updatedGiftCertificate = new GiftCertificate( "updated certificate name", " updated description", 1500L, 90L, LocalDateTime.now(), LocalDateTime.now());

        boolean result = giftCertificateRepositoryH2.update(updatedGiftCertificate, giftCertificateId);

        assertTrue(result);

    }

    @Test
    void testCreate_integration_positive() {
        GiftCertificate newGiftCertificate = new GiftCertificate( "new certificate", " new description", 1500L, 40L, LocalDateTime.now(), LocalDateTime.now());
        GiftCertificate createdGiftCertificate = giftCertificateRepositoryH2.create(newGiftCertificate);
        newGiftCertificate.setId(createdGiftCertificate.getId());

        assertNotNull(createdGiftCertificate);
        assertEquals(newGiftCertificate, createdGiftCertificate);
    }


}