package com.example.spring_shop.exception_handler;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String name) {
        super("Resource with name: " + name + " not found");
    }

    public ResourceNotFoundException(Long id) {
        super("Resource with id: " + id + " not found");
    }

    public ResourceNotFoundException(Long id, String name) {
        super("Resource with id: " + id + ", " + name + " not found");
    }

}
