package com.pavel.store.controller.handler.exeption;

import com.pavel.store.controller.handler.AppHandler;
import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends AppHandler {

    //404 not found
    public EntityNotFoundException(String entityName, Long id) {
        super(String.format("Entity %s with id %d not found", entityName, id), HttpStatus.NOT_FOUND);
    }

    public EntityNotFoundException(String username) {
        super(String.format("Entity with field %s not found", username), HttpStatus.NOT_FOUND);
    }

}
