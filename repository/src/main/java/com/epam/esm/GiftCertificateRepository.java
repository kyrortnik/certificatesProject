package com.epam.esm;


import java.util.List;

public interface GiftCertificateRepository {


    GiftCertificate getOne(Long id);

    List<GiftCertificate> getCertificates(String order, int max);

    List<GiftCertificate> getAllWithParams(String order, int max,String tag, String pattern);


     boolean delete(Long id);


     boolean update(GiftCertificate element, Long id);


    GiftCertificate create(GiftCertificate element);

}
