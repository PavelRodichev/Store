package com.pavel.store.controller.handler;

import com.pavel.store.controller.handler.exeption.EntityAlreadyExistsException;
import com.pavel.store.controller.handler.exeption.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDetails> EntityNotFoundException(EntityNotFoundException ex) {

        log.error("Entity not found: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(ex.getClass().getSimpleName(), ex.getMessage());

        return new ResponseEntity<>(errorDetails, ex.getHttpStatus());
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> EntityAlreadyExistsException(EntityAlreadyExistsException ex) {

        log.error("Entity already exists : {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(ex.getClass().getSimpleName(), ex.getMessage());

        return new ResponseEntity<>(errorDetails, ex.getHttpStatus());

    }
}
