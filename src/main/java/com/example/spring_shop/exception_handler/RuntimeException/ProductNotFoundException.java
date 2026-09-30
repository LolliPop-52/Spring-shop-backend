package com.example.spring_shop.exception_handler.RuntimeException;

public class ProductNotFoundException extends ResourceNotFoundException {
    public ProductNotFoundException(Long id) {
        super("Product", "id", id); // "Product with id: 1 not found"
    }
}
