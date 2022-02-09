package com.epam.esm;

import java.util.List;

public interface GiftCertificateRepository {

    GiftCertificate getCertificate(Long id);

    List<GiftCertificate> getCertificates(String order, int max);

    List<GiftCertificate> getCertificatesWithParams(String order, int max, String tag, String pattern);

    boolean delete(Long id);

    boolean update(GiftCertificate element, long id);

    GiftCertificate create(GiftCertificate element);

}
