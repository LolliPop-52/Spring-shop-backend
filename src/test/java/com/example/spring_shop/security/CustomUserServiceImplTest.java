package com.example.spring_shop.security;

import com.example.spring_shop.domain.User;
import com.example.spring_shop.domain.UserRole;
import com.example.spring_shop.fixture.TestDataFactory;
import com.example.spring_shop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserServiceImpl customUserService;

    private User user;

    @BeforeEach
    void setUp() {
        user = TestDataFactory.createUser();
    }

    @Test
    @DisplayName("loadUserByUsername: Успешная загрузка пользователя по email")
    void loadUserByUsername_Success() {

        when(userRepository.findFirstByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserService.loadUserByUsername(user.getEmail());

        assertNotNull(userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT")));
    }

    @Test
    @DisplayName("loadUserByUsername: Пользователь не найден выбрасывает UsernameNotFoundException")
    void loadUserByUsername_NotFound_ThrowsException() {
        when(userRepository.findFirstByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserService.loadUserByUsername(user.getEmail()));
    }
}
