package com.example.spring_shop.exception_handler;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String name) {
        super("Resourse with name: " + name + " not found");
    }

    public ResourceNotFoundException(Long id) {
        super("Resourse with id: " + id + " not found");
    }
}