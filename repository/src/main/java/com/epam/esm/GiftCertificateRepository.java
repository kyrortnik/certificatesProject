package com.epam.esm;


public interface GiftCertificateRepository {


    GiftCertificate getOne(Long id);


    public boolean delete(Long id);


    public boolean update(GiftCertificate element, Long id);


    public GiftCertificate create(GiftCertificate element);

}
