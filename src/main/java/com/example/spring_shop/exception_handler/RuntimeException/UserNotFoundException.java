package com.example.spring_shop.exception_handler.RuntimeException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(Long id) {
        super("User", "id", id); // "User with id: 1 not found"
    }
    public UserNotFoundException(String email) {
        super("User", "email", email); // "User with email: test@gmail.com not found"
    }
}
