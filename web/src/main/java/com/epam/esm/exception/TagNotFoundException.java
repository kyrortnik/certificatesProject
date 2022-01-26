package com.epam.esm.exception;

public class TagNotFoundException extends RuntimeException{

    private final long tagId;

    public TagNotFoundException(long tagId) {
        this.tagId = tagId;
    }

    public long getTafId() {
        return tagId;
    }


}
