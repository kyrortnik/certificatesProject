package com.epam.esm.exception;


public class GiftCertificateNotFoundException extends RuntimeException {
    private final long certificateId;

    public GiftCertificateNotFoundException(long certificateId) {
        this.certificateId = certificateId;
    }

    public long getCertificateId() {
        return certificateId;
    }

}
