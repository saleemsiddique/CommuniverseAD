package com.example.communiverse.exception;

public class PostNotFoundException extends RuntimeException{
    public PostNotFoundException() {
        super();
    }

    public PostNotFoundException(String message) {
        super(message);
    }

    public PostNotFoundException(long id) {
        super("Post not found: " + id);
    }
}
