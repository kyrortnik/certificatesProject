package com.epam.esm.exception;

public class EntityNotFoundException extends RuntimeException {

    private final long certificateId;

    public EntityNotFoundException(long certificateId) {
        this.certificateId = certificateId;
    }

    public long getEntityId() {
        return certificateId;
    }

}
