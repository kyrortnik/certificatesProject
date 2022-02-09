package com.epam.esm;

import com.epam.esm.impl.CertificateService;
import com.epam.esm.impl.TagService;
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
    private final TagService tagService = Mockito.mock(TagService.class, withSettings().verboseLogging());

    //class under test
    private final CertificateService giftCertificateService = new CertificateService(giftCertificateRepository,tagService);

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
    private final String tag = "tagName";
    private final String pattern = "pattern";

    private final List<GiftCertificate> giftCertificates = Arrays.asList(
            new GiftCertificate(1L, "first certificate", "first description", 100L, 120L, LocalDateTime.now(), LocalDateTime.now()),
            new GiftCertificate(2L, "second certificate", "second description", 300L, 30L, LocalDateTime.now(), LocalDateTime.now()),
            new GiftCertificate(3L, "third certificate", "third description", 500L, 90L, LocalDateTime.now(), LocalDateTime.now())

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
        when(tagService.getTagsForCertificate(giftCertificateId)).thenReturn(tags);

        GiftCertificate returnGiftCertificate = giftCertificateService.getEntity(giftCertificateId);

        verify(giftCertificateRepository).getCertificate(giftCertificateId);
        verify(tagService).getTagsForCertificate(giftCertificateId);
        assertEquals(giftCertificate, returnGiftCertificate);

    }

    @Test
    void testGetEntities_positive() {

        when(giftCertificateRepository.getCertificates(order, max)).thenReturn(giftCertificates);

        List<GiftCertificate> returnCertificates = giftCertificateService.getEntities(order, max);

        verify(giftCertificateRepository).getCertificates(order, max);
        assertEquals(giftCertificates, returnCertificates);
    }

    @Test
    void getEntitiesWithParams_positive() {
        String processedPattern = '%' + pattern + '%';

        when(giftCertificateRepository.getCertificatesWithParams(order,max, tag, processedPattern)).thenReturn(giftCertificates);
        when(tagService.getTagsForCertificate(giftCertificateId)).thenReturn(tags);

        List<GiftCertificate> returnCertificates = giftCertificateService.getEntitiesWithParams(order,max, tag, pattern);

        verify(giftCertificateRepository).getCertificatesWithParams(order, max, tag, processedPattern);
        verify(tagService).getTagsForCertificate(giftCertificateId);
        assertEquals(giftCertificates,returnCertificates);


    }

    @Test
    void testDelete_positive() {
        when(giftCertificateRepository.delete(giftCertificateId)).thenReturn(true);

        boolean result = giftCertificateService.delete(giftCertificateId);

        verify(giftCertificateRepository).delete(giftCertificateId);
        assertTrue(result);
    }

    @Test
    void testUpdate_positive() {
        GiftCertificate giftCertificate = new GiftCertificate(giftCertificateId, name, description, price, duration, createDate, lastUpdateDate, tags);
        giftCertificate.setLastUpdateDate(LocalDateTime.now());
        when(giftCertificateRepository.update(giftCertificate, giftCertificateId)).thenReturn(true);


        boolean result = giftCertificateService.update(giftCertificate, giftCertificateId);

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