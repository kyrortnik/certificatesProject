package com.epam.esm;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificateService implements CRUDService<GiftCertificate> {

    @Autowired
    private GiftCertificateRepository repository;

    @Override
    public GiftCertificate getOne(Long id) {
        return repository.getOne(id);
    }

    @Override
    public List<GiftCertificate> getAll(String order, int max) {
       return repository.getCertificates(order,max);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }

    @Override
    public boolean update(GiftCertificate element, Long id) {
        return repository.update(element, id);

    }

    @Override
    public GiftCertificate create(GiftCertificate giftCertificate) {
        return repository.create(giftCertificate);
    }

}
