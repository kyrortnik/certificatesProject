package com.epam.esm;


public interface GiftCertificateRepository {


    GiftCertificate getOne(Long id);


    public void delete(Long id);


    public void update(GiftCertificate element);


    public GiftCertificate create(GiftCertificate element);

}
