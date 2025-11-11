package com.pavel.store.handler.exeption;

import org.springframework.http.HttpStatus;

public class EmptyListException extends AppHandler {


    public EmptyListException(String object) {
        super("List object is empty", HttpStatus.BAD_REQUEST);
    }
}
