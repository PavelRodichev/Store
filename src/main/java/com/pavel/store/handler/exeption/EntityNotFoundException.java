package com.pavel.store.handler.exeption;

import com.pavel.store.handler.AppHandler;
import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends AppHandler {

    //404 not found
    public EntityNotFoundException(String entityName, Long id) {
        super(String.format("Entity %s with id %d not found", entityName, id), HttpStatus.NOT_FOUND);
    }

    public EntityNotFoundException(String name) {
        super(String.format("Entity with field %s not found", name), HttpStatus.NOT_FOUND);
    }

}
