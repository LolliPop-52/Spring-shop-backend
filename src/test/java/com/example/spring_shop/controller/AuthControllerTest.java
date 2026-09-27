package com.example.spring_shop.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.spring_shop.dto.UserDTO;
import com.example.spring_shop.security.JwtAuthenticationDTO;
import com.example.spring_shop.security.RefreshTokenDTO;
import com.example.spring_shop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;




@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();


    private JwtAuthenticationDTO jwtDTO = new JwtAuthenticationDTO();
    private UserDTO userDTO = new UserDTO();

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        jwtDTO.setToken("token444");
        jwtDTO.setRefreshToken("token123");


        userDTO.setEmail("test@gmail.com");
    }

    @Test
    @DisplayName("POST /api/v1/auth/sign-in: Успешная авторизация пользователя")
    void singIn_Success() throws Exception {
        when(userService.signIn(userDTO)).thenReturn(jwtDTO);

        mockMvc.perform(post("/api/v1/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.token").value("token444"),
                        jsonPath("$.refreshToken").value("token123")
                );
        verify(userService, times(1)).signIn(userDTO);
    }


    @Test
    @DisplayName("POST /api/v1/auth/sign-up: Успешная регистрация нового пользователя")
    void signUp_Success() throws Exception {

        when(userService.signUp(userDTO)).thenReturn(jwtDTO);

        mockMvc.perform(post("/api/v1/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.token").value("token444"),
                        jsonPath("$.refreshToken").value("token123")
                );
        verify(userService, times(1)).signUp(userDTO);
    }

    @Test
    @DisplayName("GET /api/v1/auth/confirm: Успешное подтверждение аккаунта по валидному токену")
    void confirmRegistration_VALID() throws Exception {
        when(userService.confirmUser(jwtDTO.getToken())).thenReturn(true);

        mockMvc.perform(get("/api/v1/auth/confirm")
                        .param("token", jwtDTO.getToken()))
                .andExpectAll(
                        status().isOk(),
                        content().string("Account has been successfully verified!")
                );
        verify(userService, times(1)).confirmUser(jwtDTO.getToken());

    }

    @Test
    @DisplayName("GET /api/v1/auth/confirm: Ошибка подтверждения при истекшем или невалидном токене")
    void confirmRegistration_INVALID() throws Exception {

        when(userService.confirmUser(jwtDTO.getToken())).thenReturn(false);

        mockMvc.perform(get("/api/v1/auth/confirm")
                        .param("token", jwtDTO.getToken()))
                .andExpectAll(
                        status().isBadRequest(),
                        content().string("The validity period has expired")
                );
        verify(userService, times(1)).confirmUser(jwtDTO.getToken());
    }

    @Test
    @DisplayName("POST /api/v1/auth/refresh: Успешное обновление токена")
    void refresh_Success() throws Exception {

        JwtAuthenticationDTO newJwtDTO = new JwtAuthenticationDTO();
        newJwtDTO.setToken("newToken");
        newJwtDTO.setRefreshToken("newRefreshToken");

        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO();
        refreshTokenDTO.setRefreshToken("myRefreshToken");

        when(userService.refreshToken(refreshTokenDTO)).thenReturn(newJwtDTO);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenDTO)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.token").value(newJwtDTO.getToken()),
                        jsonPath("$.refreshToken").value(newJwtDTO.getRefreshToken())
                );
        verify(userService, times(1)).refreshToken(refreshTokenDTO);
    }
}
