package com.example.spring_shop.security;

import com.example.spring_shop.fixture.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    // 256-bit secret key in Base64
    private static final String SECRET = Base64.getEncoder().encodeToString(
            "very_secure_secret_key_for_testing_purposes_at_least_32_bytes!".getBytes()
    );

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", SECRET);
    }

    @Test
    @DisplayName("generateAuthToken: Генерация валидного токена и refresh-токена")
    void generateAuthToken_Success() {
        JwtAuthenticationDTO dto = jwtService.generateAuthToken(TestDataFactory.DEFAULT_EMAIL);

        assertNotNull(dto);
        assertNotNull(dto.getToken());
        assertNotNull(dto.getRefreshToken());

        assertTrue(jwtService.validateJwtToken(dto.getToken()));
        assertTrue(jwtService.validateJwtToken(dto.getRefreshToken()));
        assertEquals(TestDataFactory.DEFAULT_EMAIL, jwtService.getEmailFromToken(dto.getToken()));
        assertEquals(TestDataFactory.DEFAULT_EMAIL, jwtService.getEmailFromToken(dto.getRefreshToken()));
    }

    @Test
    @DisplayName("validateJwtToken: Возвращает false для некорректного токена")
    void validateJwtToken_InvalidToken_ReturnsFalse() {
        assertFalse(jwtService.validateJwtToken("invalid.token.structure"));
        assertFalse(jwtService.validateJwtToken(""));
    }

    @Test
    @DisplayName("refreshBaseToken: Обновление базового токена с сохранением refresh токена")
    void refreshBaseToken_Success() {
        JwtAuthenticationDTO original = jwtService.generateAuthToken(TestDataFactory.DEFAULT_EMAIL);

        JwtAuthenticationDTO refreshed = jwtService.refreshBaseToken(TestDataFactory.DEFAULT_EMAIL, original.getRefreshToken());

        assertNotNull(refreshed);
        assertNotNull(refreshed.getToken());
        assertEquals(original.getRefreshToken(), refreshed.getRefreshToken());
        assertEquals(TestDataFactory.DEFAULT_EMAIL, jwtService.getEmailFromToken(refreshed.getToken()));
    }
}
