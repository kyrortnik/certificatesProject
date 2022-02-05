package com.epam.esm;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CertificateServiceTest {

    //mock
    private final GiftCertificateRepository giftCertificateRepository = Mockito.mock(GiftCertificateRepository.class, withSettings().verboseLogging());

    //class under test
    private final CertificateService giftCertificateService = new CertificateService(giftCertificateRepository);

    //params
    private final long giftCertificateId = 1L;
    private final String name = "certificate name";
    private final String description = "certificate description";
    private final Long price = 100L;
    private final long duration = 120;
    private final LocalDateTime createDate = LocalDateTime.now();
    private final LocalDateTime lastUpdateDate = LocalDateTime.now();

    private final String order = "ASC";
    private final int max = 20;

    private final List<GiftCertificate> giftCertificates = Arrays.asList(
            new GiftCertificate(1L, "first certificcate", "first description", 100L, 120L, LocalDateTime.now(), LocalDateTime.now()),
            new GiftCertificate(2L, "second certificcate", "second description", 300L, 30L, LocalDateTime.now(), LocalDateTime.now()),
            new GiftCertificate(1L, "second certificcate", "second description", 500L, 90L, LocalDateTime.now(), LocalDateTime.now())

    );

    private final List<Tag> tags = Arrays.asList(
            new Tag(1L, "first tag"),
            new Tag(2L, "second tag"),
            new Tag(3L, "third tag")
    );

    @Test
    void testGetEntity_positive() {
        GiftCertificate giftCertificate = new GiftCertificate(giftCertificateId, name, description, price, duration, createDate, lastUpdateDate, tags);

        when(giftCertificateRepository.getCertificate(giftCertificateId)).thenReturn(giftCertificate);

        GiftCertificate returnGiftCertificate = giftCertificateService.getEntity(giftCertificateId);

        verify(giftCertificateRepository).getCertificate(giftCertificateId);
        assertEquals(giftCertificate, returnGiftCertificate);

    }

    @Test
    void testGetEntities_positive() {

        when(giftCertificateRepository.getCertificates(order, max)).thenReturn(giftCertificates);

        List<GiftCertificate> returnCertificates = giftCertificateService.getEntities(order, max);

        verify(giftCertificateRepository).getCertificates(order, max);
        assertEquals(giftCertificates, returnCertificates);
    }

    //TODO  implement this test after method itself is implemented
    @Test
    void getEntitiesWithParams() {
    }

    @Test
    void testDelete_positive() {
        boolean result;
        when(giftCertificateRepository.delete(giftCertificateId)).thenReturn(true);

        result = giftCertificateService.delete(giftCertificateId);

        verify(giftCertificateRepository).delete(giftCertificateId);
        assertTrue(result);
    }

    @Test
    void testUpdate_positive() {
        boolean result;
        GiftCertificate giftCertificate = new GiftCertificate(giftCertificateId, name, description, price, duration, createDate, lastUpdateDate, tags);
        giftCertificate.setLastUpdateDate(LocalDateTime.now());
        when(giftCertificateRepository.update(giftCertificate, giftCertificateId)).thenReturn(true);


        result = giftCertificateService.update(giftCertificate, giftCertificateId);

        verify(giftCertificateRepository).update(giftCertificate, giftCertificateId);
        assertTrue(result);
    }

    @Test
    void testCreate_positive() {
        GiftCertificate giftCertificate = new GiftCertificate(giftCertificateId, name, description, price, duration, null, null, tags);
        giftCertificate.setCreateDate(LocalDateTime.now());
        giftCertificate.setLastUpdateDate(LocalDateTime.now());
        when(giftCertificateRepository.create(giftCertificate)).thenReturn(giftCertificate);

        GiftCertificate returnGiftCertificate = giftCertificateService.create(giftCertificate);

        verify(giftCertificateRepository).create(giftCertificate);
        assertEquals(giftCertificate, returnGiftCertificate);
    }
}