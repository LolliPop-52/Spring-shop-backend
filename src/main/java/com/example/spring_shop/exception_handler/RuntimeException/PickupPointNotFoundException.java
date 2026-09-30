package com.example.spring_shop.exception_handler.RuntimeException;

public class PickupPointNotFoundException extends ResourceNotFoundException {
    public PickupPointNotFoundException(Long id) {
        super("PickupPoint", "id", id); // "PickupPoint with id: 1 not found"
    }
}
