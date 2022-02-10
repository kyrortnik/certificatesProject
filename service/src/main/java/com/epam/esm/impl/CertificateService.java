package com.epam.esm.impl;

import com.epam.esm.CRUDService;
import com.epam.esm.GiftCertificate;
import com.epam.esm.GiftCertificateRepository;
import com.epam.esm.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.isNull;

@Service
public class CertificateService implements CRUDService<GiftCertificate> {

    @Autowired
    private final GiftCertificateRepository giftCertificateRepository;

    @Autowired
    private final TagService tagService;

    public CertificateService(GiftCertificateRepository giftCertificateRepository, TagService tagService) {
        this.giftCertificateRepository = giftCertificateRepository;
        this.tagService = tagService;
    }

    @Override
    public GiftCertificate getEntity(Long id) {
        GiftCertificate giftCertificate = giftCertificateRepository.getCertificate(id);
        List<Tag> tags = tagService.getTagsForCertificate(id);
        if (giftCertificate != null) {
            giftCertificate.setTags(tags);
        }
        return giftCertificate;
    }


    @Override
    public List<GiftCertificate> getEntities(String order, int max) {
        return giftCertificateRepository.getCertificates(order, max);
    }


    public List<GiftCertificate> getEntitiesWithParams(String order, int max, String tag, String pattern) {
       String processedPattern = fromParamToMatchingPattern(pattern);

        List<GiftCertificate> giftCertificates = giftCertificateRepository.getCertificatesWithParams(order, max, tag, processedPattern);
        for (GiftCertificate certificate : giftCertificates) {
            List<Tag> tags = tagService.getTagsForCertificate(certificate.getId());
            certificate.setTags(tags);
        }
        return giftCertificates;
    }


    @Override
    public boolean delete(Long id) {
        return giftCertificateRepository.delete(id);
    }

    @Override
    public boolean update(GiftCertificate giftCertificate, Long id) {
        giftCertificate.setLastUpdateDate(LocalDateTime.now());
        return giftCertificateRepository.update(giftCertificate, id);

    }

    @Override
    public GiftCertificate create(GiftCertificate giftCertificate) {
        giftCertificate.setCreateDate(LocalDateTime.now());
        giftCertificate.setLastUpdateDate(LocalDateTime.now());
        return giftCertificateRepository.create(giftCertificate);
    }

    private String fromParamToMatchingPattern(String pattern) {
        if (!isNull(pattern)){
            pattern = '%' + pattern + '%';
        }
        return pattern;
    }

}
