package com.epam.esm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CertificateService implements CRUDService<GiftCertificate> {

    @Autowired
    private GiftCertificateRepository giftCertificateRepository;

    public CertificateService(GiftCertificateRepository giftCertificateRepository){
        this.giftCertificateRepository = giftCertificateRepository;
    }

    @Override
    public GiftCertificate getEntity(Long id) {
        return giftCertificateRepository.getCertificate(id);
    }


    @Override
    public List<GiftCertificate> getEntities(String order, int max) {
        return giftCertificateRepository.getCertificates(order, max);
    }

    public List<GiftCertificate> getEntitiesWithParams(String order, int max, String tag, String pattern) {
        return giftCertificateRepository.getAllWithParams(order, max, tag, pattern);
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


}
