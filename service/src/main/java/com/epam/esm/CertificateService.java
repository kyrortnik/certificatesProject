package com.epam.esm;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificateService implements CRUDService<GiftCertificate> {

    @Autowired
    private GiftCertificateRepository repository;

    @Override
    public GiftCertificate getCertificate(Long id) {
        return repository.getOne(id);
    }

    @Override
    public List<GiftCertificate> getAll(String order, int max) {
        return repository.getCertificates(order, max);
    }

    /*@Override
    public void delete(Long id) {
        repository.delete(id);
    }*/
    @Override
    public boolean delete(Long id){
        return repository.delete(id);
    }

    @Override
    public boolean update(GiftCertificate element, Long id) {
        return repository.update(element, id);

    }

    @Override
    public GiftCertificate create(GiftCertificate giftCertificate) {
        return repository.create(giftCertificate);
    }


    public List<GiftCertificate> getAllWithParams(String order, int max, String tag, String pattern){
      return  repository.getAllWithParams(order, max, tag, pattern);
    }
}
