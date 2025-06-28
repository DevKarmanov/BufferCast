package com.karmanov.auth.authservice.exception.request;

public class RequestFailedException extends RuntimeException{
    public RequestFailedException(String message) {
        super(message);
    }
}
