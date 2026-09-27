package com.example.spring_shop.exception_handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import javax.naming.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandleTest {

    private GlobalExceptionHandle exceptionHandle;

    @BeforeEach
    void setUp() {
        exceptionHandle = new GlobalExceptionHandle();
    }

    @Test
    @DisplayName("Обработка AuthenticationException -> 401 UNAUTHORIZED со строкой ошибки")
    void handlerException_AuthenticationException() {
        AuthenticationException ex = new AuthenticationException("Bad credentials");
        ResponseEntity<String> response = exceptionHandle.handlerException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Authentication Failed: Bad credentials", response.getBody());
    }

    @Test
    @DisplayName("Обработка AccessDeniedException -> 403 FORBIDDEN со строкой ошибки")
    void handleAccessDenied_AccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Access is denied");
        ResponseEntity<String> response = exceptionHandle.handleAccessDenied(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An error occurred: Access is denied", response.getBody());
    }

    @Test
    @DisplayName("Обработка DataIntegrityViolationException -> 409 CONFLICT")
    void handleDataException_DataIntegrityViolationException() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Mismatch");
        ResponseEntity<String> response = exceptionHandle.handleDataException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Price mismatch for product", response.getBody());
    }

    @Test
    @DisplayName("Обработка общего Exception -> 500 INTERNAL_SERVER_ERROR")
    void handleGenericException_GenericException() {
        RuntimeException ex = new RuntimeException("Unexpected runtime error");
        ResponseEntity<String> response = exceptionHandle.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An internal server error occurred", response.getBody());
    }
}
