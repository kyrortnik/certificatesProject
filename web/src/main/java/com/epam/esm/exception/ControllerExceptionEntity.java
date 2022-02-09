package com.epam.esm.exception;

public class ControllerExceptionEntity {

    private final String message;
    private final int code;

    public ControllerExceptionEntity(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

}
