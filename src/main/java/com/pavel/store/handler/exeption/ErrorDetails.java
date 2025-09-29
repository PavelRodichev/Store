package com.pavel.store.handler.exeption;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {

    private String exception;

    private String message;



    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ErrorDetails(String exception, String message) {
        this.exception = exception;
        this.message = message;
        this.timestamp = LocalDateTime.now();

    }

}
