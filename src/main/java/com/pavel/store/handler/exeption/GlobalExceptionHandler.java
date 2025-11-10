package com.pavel.store.handler.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(basePackages = "com.pavel.store.controller")
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Validation failed for request: {}", request.getDescription(false));


        String errorMessage = ex.getBindingResult().getAllErrors() // вместо getFieldErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        FieldError fieldError = (FieldError) error;
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    } else {
                        return error.getDefaultMessage(); // глобальные ошибки
                    }
                })
                .collect(Collectors.joining("; "));

        ErrorDetails errorDetails = new ErrorDetails(ex.getClass().getSimpleName(), errorMessage);

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        e.printStackTrace(); // Смотрите логи в консоли!
        return ResponseEntity.status(500)
                .body("Error: " + e.getMessage() + "\nCheck console for details");
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorDetails> handleMissingRequestHeader(MissingRequestHeaderException ex) {
        log.error("Missing request header: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                ex.getClass().getSimpleName(),
                "Missing required header: " + ex.getHeaderName()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());

        ErrorDetails errorDetails = new ErrorDetails(
                ex.getClass().getSimpleName(),
                ex.getMessage()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

}
