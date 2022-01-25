package com.epam.esm;


public interface GiftCertificateRepository {


    GiftCertificate getOne(Long id);


     void delete(Long id);


     boolean update(GiftCertificate element, Long id);


     GiftCertificate create(GiftCertificate element);

}
