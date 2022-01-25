package com.epam.esm;

public class CustomError {
    private final String message;
    private final int code;

    public CustomError(int code, String message) {
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
