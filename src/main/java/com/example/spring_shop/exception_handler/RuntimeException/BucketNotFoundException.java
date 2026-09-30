package com.example.spring_shop.exception_handler.RuntimeException;

public class BucketNotFoundException extends ResourceNotFoundException {
    public BucketNotFoundException(Long id) {
        super("Bucket", "user ID", id); // "Bucket with user ID: 1 not found"
    }

    public BucketNotFoundException(String email) {
        super("Bucket", "user Email", email); //"Bucket with user Email: test@gmail.com not found"
    }
}
