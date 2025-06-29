package com.karmanov.auth.authservice.controller.exception;

import com.karmanov.auth.authservice.dto.response.ErrorResponse;
import com.karmanov.auth.authservice.exception.request.RequestFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Invalid input: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Invalid input: " + ex.getMessage(), "INVALID_INPUT"));
    }

    @ExceptionHandler(RequestFailedException.class)
    public ResponseEntity<ErrorResponse> handleRequestFailedException(RequestFailedException ex){
        log.error("Request failed", ex);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse("Internal server error. Please contact support.", "INTERNAL_SERVER_ERROR"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse("Internal server error. Please contact support.", "INTERNAL_SERVER_ERROR"));
    }
}