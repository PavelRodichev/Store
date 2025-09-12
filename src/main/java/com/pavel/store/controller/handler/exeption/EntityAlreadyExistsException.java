package com.pavel.store.controller.handler.exeption;

import com.pavel.store.controller.handler.AppHandler;
import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends AppHandler {


    public EntityAlreadyExistsException(String entityName, String field, String value) {
        super(String.format("Entity %s with field %s and value %s already Exist", entityName, field, value),
                HttpStatus.CONFLICT);
    }


}
