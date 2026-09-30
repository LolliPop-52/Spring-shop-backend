package com.example.spring_shop.exception_handler.RuntimeException;

public class VerificationTokenNotFoundException extends ResourceNotFoundException {
    public VerificationTokenNotFoundException(String token) {
        super("VerificationToken", "token", token); //VerificationToken with token: hgfvsdjb12 not found;
    }
}
