package com.epam.esm;

import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile("dev")
public class GiftCertificateRepositoryH2 implements GiftCertificateRepository{


    @Override
    public GiftCertificate getCertificate(Long id) {
        return null;
    }

    @Override
    public List<GiftCertificate> getCertificates(String order, int max) {
        return null;
    }

    @Override
    public List<GiftCertificate> getAllWithParams(String order, int max, String tag, String pattern) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public boolean update(GiftCertificate element, Long id) {
        return false;
    }

    @Override
    public GiftCertificate create(GiftCertificate element) {
        return null;
    }
}
