package com.lordbyron.backendByronLibrary.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(ExceptionMessage.class)
    public ResponseEntity<?> NotFoundErrorHandling(ExceptionMessage exception) {
        var errorDetails = new ErrorDetails(
                new Date(),
                "System Library Byron exception!",
                exception.getMessage()
        );
        return ResponseEntity.badRequest().body(errorDetails);
    }
}
