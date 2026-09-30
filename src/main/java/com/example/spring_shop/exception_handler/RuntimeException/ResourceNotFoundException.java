package com.example.spring_shop.exception_handler.RuntimeException;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s with %s: %s not found", resourceName, fieldName, fieldValue));
    }
}
