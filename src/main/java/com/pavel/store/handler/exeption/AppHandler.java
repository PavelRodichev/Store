package com.pavel.store.handler.exeption;

import org.springframework.http.HttpStatus;


public class AppHandler extends RuntimeException {

    private final HttpStatus httpStatus;

    public AppHandler(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
