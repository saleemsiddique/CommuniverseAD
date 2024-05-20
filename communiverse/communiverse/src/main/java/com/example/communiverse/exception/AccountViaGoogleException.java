package com.example.communiverse.exception;

public class AccountViaGoogleException extends RuntimeException {
    public AccountViaGoogleException() {
        super();
    }

    public AccountViaGoogleException(String message) {
        super(message);
    }
}
