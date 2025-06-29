package com.karmanov.auth.exception.request;

public class RequestFailedException extends RuntimeException{
    public RequestFailedException(String message) {
        super(message);
    }
}
