package com.example.User.exceptions;

public class MobileNumberAlreadyExistException extends RuntimeException {
    public MobileNumberAlreadyExistException(String message) {
        super(message);
    }
}
