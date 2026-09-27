package com.example.spring_shop.service.impl;

import com.example.spring_shop.domain.User;
import com.example.spring_shop.domain.UserRole;
import com.example.spring_shop.dto.UserDTO;
import com.example.spring_shop.exception_handler.ResourceNotFoundException;
import com.example.spring_shop.fixture.TestDataFactory;
import com.example.spring_shop.mail.MailService;
import com.example.spring_shop.mail.VerificationToken;
import com.example.spring_shop.mapper.UserMapper;
import com.example.spring_shop.repository.UserRepository;
import com.example.spring_shop.repository.VerificationTokenRepository;
import com.example.spring_shop.security.JwtAuthenticationDTO;
import com.example.spring_shop.security.JwtService;
import com.example.spring_shop.security.RefreshTokenDTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.AuthenticationException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private MailService mailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;
    private JwtAuthenticationDTO jwtAuthenticationDTO;

    @BeforeEach
    void setUp() {
        user = TestDataFactory.createUser();
        userDTO = TestDataFactory.createUserDTO();
        jwtAuthenticationDTO = new JwtAuthenticationDTO();
        jwtAuthenticationDTO.setToken("token");
        jwtAuthenticationDTO.setRefreshToken("refreshToken");
    }

    @Test
    @DisplayName("signIn: Успешный вход пользователя")
    void signIn_Success() throws Exception {

        when(userRepository.findFirstByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);
        when(jwtService.generateAuthToken(user.getEmail())).thenReturn(jwtAuthenticationDTO);
        when(passwordEncoder.matches(userDTO.getPassword(), user.getPassword())).thenReturn(true);

        JwtAuthenticationDTO result = userService.signIn(userDTO);

        verify(jwtService, times(1)).generateAuthToken(user.getEmail());
        assertEquals(jwtAuthenticationDTO, result);

    }

    @Test
    @DisplayName("signIn: Пользователь не найден выбрасывает ResourceNotFoundException")
    void singIn_UserNotFound_ThrowsException() {

        when(userRepository.findFirstByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.signIn(userDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource with name: " + user.getEmail() + " not found");

        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("signIn: Неверный пароль выбрасывает AuthenticationException")
    void signIn_Incorrect_Password() {

        userDTO.setConfirmPassword("incorrectPassword");
        when(userRepository.findFirstByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userDTO.getPassword(), user.getPassword())).thenReturn(false);


        assertThatThrownBy(() -> userService.signIn(userDTO))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("uncorrected password");

        verifyNoInteractions(jwtService);

    }

    @Test
    @DisplayName("signUp: Успешная регистрация")
    void signUp_Success() throws Exception {

        user.setEnabled(false);

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("12345");
        when(jwtService.generateAuthToken(user.getEmail())).thenReturn(jwtAuthenticationDTO);
        ArgumentCaptor<VerificationToken> captorForToken = ArgumentCaptor.forClass(VerificationToken.class);

        JwtAuthenticationDTO result = userService.signUp(userDTO);

        verify(verificationTokenRepository, times(1)).save(captorForToken.capture());
        User newUser = captorForToken.getValue().getUser();
        verify(userRepository, times(1)).save(user);
        verify(mailService, times(1)).sendVerificationEmail(eq(user.getEmail()), anyString());
        verify(jwtService, times(1)).generateAuthToken(user.getEmail());

        assertNotNull(newUser);
        assertNotNull(newUser.getBucket());
        assertEquals(jwtAuthenticationDTO, result);
        assertEquals(user.getPassword(), newUser.getPassword());
        assertEquals(UserRole.CLIENT, newUser.getRole());


    }

    @Test
    @DisplayName("signUp: Несовпадение пароля и подтверждения выбрасывает AuthenticationException")
    void signUp_PasswordMismatch_ThrowsException() {
        userDTO.setConfirmPassword("incorrectPassword");

        assertThatThrownBy(() -> userService.signUp(userDTO))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("passwords don't match");

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("signUp: Почта уже зарегистрирована выбрасывает AuthenticationException")
    void signUp_EmailAlreadyExists_ThrowsException() {

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.signUp(userDTO))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("email has already been registered");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("refreshToken: Успешное обновление токена")
    void refreshToken_Success() throws Exception {
        RefreshTokenDTO refreshDTO = new RefreshTokenDTO();
        refreshDTO.setRefreshToken("refreshToken");

        when(jwtService.validateJwtToken("refreshToken")).thenReturn(true);
        when(jwtService.getEmailFromToken("refreshToken")).thenReturn(user.getEmail());
        when(userRepository.findFirstByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.refreshBaseToken(user.getEmail(), "refreshToken")).thenReturn(jwtAuthenticationDTO);

        JwtAuthenticationDTO result = userService.refreshToken(refreshDTO);

        assertEquals(jwtAuthenticationDTO, result);

        verify(jwtService, times(1)).validateJwtToken(refreshDTO.getRefreshToken());
        verify(jwtService, times(1)).getEmailFromToken(refreshDTO.getRefreshToken());
        verify(userRepository, times(1)).findFirstByEmail(user.getEmail());
        verify(jwtService, times(1)).refreshBaseToken(user.getEmail(), refreshDTO.getRefreshToken());
    }

    @Test
    @DisplayName("refreshToken: Невалидный refresh токен")
    void refreshToken_InvalidToken_ThrowsException() {
        RefreshTokenDTO refreshDTO = new RefreshTokenDTO();
        refreshDTO.setRefreshToken("invalid_token");

        when(jwtService.validateJwtToken("invalid_token")).thenReturn(false);

        assertThatThrownBy(() -> userService.refreshToken(refreshDTO))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid refresh token");

        verify(jwtService, never()).refreshBaseToken(eq(user.getEmail()), anyString());
    }

    @Test
    @DisplayName("refreshToken: Null токен")
    void refreshToken_NullToken_ThrowsException() {
        RefreshTokenDTO refreshDTO = new RefreshTokenDTO();
        refreshDTO.setRefreshToken(null);

        assertThatThrownBy(() -> userService.refreshToken(refreshDTO))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid refresh token");

        verify(jwtService, never()).refreshBaseToken(eq(user.getEmail()), anyString());
    }



    @Test
    @DisplayName("getUserById: Пользователь найден")
    void getUserById_Success() {
        when(userRepository.findById(TestDataFactory.DEFAULT_ID)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(TestDataFactory.DEFAULT_ID);

        assertNotNull(result);
        assertEquals(TestDataFactory.DEFAULT_ID, result.getId());

        verify(userMapper, times(1)).toDTO(user);
    }

    @Test
    @DisplayName("getUserById: Пользователь не найден")
    void getUserById_NotFound_ThrowsException() {
        Long testId = 99L;
        when(userRepository.findById(testId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(testId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource with id: " + testId + " not found");

        verify(userMapper, never()).toDTO(any(User.class));
    }


    @Test
    @DisplayName("getUserByEmail: Пользователь найден")
    void getUserByEmail_Success() {
        when(userRepository.findFirstByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserByEmail(user.getEmail());

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());

        verify(userMapper, times(1)).toDTO(user);
    }

    @Test
    @DisplayName("getUserByEmail: Пользователь не найден")
    void getUserByEmail_UserNotFound_ThrowsException() {
        when(userRepository.findFirstByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail(user.getEmail()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource with name: " + user.getEmail() + " not found");

        verify(userMapper, never()).toDTO(any(User.class));
    }

    @Test
    @DisplayName("deleteUserById: Успешное удаление")
    void deleteUserById_Success() {
        when(userRepository.existsById(TestDataFactory.DEFAULT_ID)).thenReturn(true);

        String result = userService.deleteUserById(TestDataFactory.DEFAULT_ID);

        assertEquals("User deleted", result);
        verify(userRepository).deleteById(TestDataFactory.DEFAULT_ID);
    }

    @Test
    @DisplayName("deleteUserById: Пользователь не найден")
    void deleteUserById_NotFound_ThrowsException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource with id: " + 99L + " not found");

        verify(userRepository, never()).deleteById(anyLong());
    }


    @Test
    @DisplayName("confirmUser: Успешное подтверждение аккаунта")
    void confirmUser_ValidToken_ReturnsTrue() {
        VerificationToken token = new VerificationToken();
        token.setToken("token");
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusHours(1));

        when(verificationTokenRepository.findByToken("token")).thenReturn(Optional.of(token));

        boolean result = userService.confirmUser("token");

        assertTrue(result);
        assertTrue(user.isEnabled());
        verify(userRepository).save(user);
        verify(verificationTokenRepository).delete(token);
    }

    @Test
    @DisplayName("confirmUser: Просроченный токен возвращает false")
    void confirmUser_ExpiredToken_ReturnsFalse() {
        VerificationToken token = new VerificationToken();
        token.setToken("expired_token");
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().minusHours(1));

        when(verificationTokenRepository.findByToken("expired_token")).thenReturn(Optional.of(token));

        boolean result = userService.confirmUser("expired_token");

        assertFalse(result);
        verify(userRepository, never()).save(any());
        verify(verificationTokenRepository, never()).delete(any());
    }

    @Test
    @DisplayName("confirmUser: Токен не найден")
    void confirmUser_TokenNotFound_ThrowsException() {
        String token = "missing_token";
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.confirmUser(token))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource with name: " + token + " not found");

        verifyNoInteractions(userRepository);
        verify(verificationTokenRepository, never()).delete(any(VerificationToken.class));
    }

}