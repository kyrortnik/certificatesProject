package com.epam.esm;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateService implements CRUDService<GiftCertificate> {

    @Autowired
    private GiftCertificateRepository repository;

    @Override
    public GiftCertificate getOne(Long id) {
        return repository.getOne(id);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void update(GiftCertificate element) {

    }

    @Override
    public GiftCertificate create(GiftCertificate giftCertificate) {
        return  repository.create(giftCertificate);
    }

}
