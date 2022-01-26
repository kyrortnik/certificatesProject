package com.epam.esm;


import java.util.List;

public interface GiftCertificateRepository {


    GiftCertificate getOne(Long id);

    List<GiftCertificate> getCertificates(String order, int max);


     void delete(Long id);


     boolean update(GiftCertificate element, Long id);


     GiftCertificate create(GiftCertificate element);

}
