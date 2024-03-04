package com.example.communiverse.exception;

public class CommunityNotFoundException extends RuntimeException{
    public CommunityNotFoundException() {
        super();
    }

    public CommunityNotFoundException(String message) {
        super(message);
    }

    public CommunityNotFoundException(long id) {
        super("Community not found: " + id);
    }
}
