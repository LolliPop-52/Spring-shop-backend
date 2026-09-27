package com.example.spring_shop.controller;

import com.example.spring_shop.domain.User;
import com.example.spring_shop.dto.UserDTO;
import com.example.spring_shop.dto.UserUpdateDTO;
import com.example.spring_shop.fixture.TestDataFactory;
import com.example.spring_shop.security.CustomUserDetails;
import com.example.spring_shop.security.JwtAuthenticationDTO;
import com.example.spring_shop.security.JwtFilter;
import com.example.spring_shop.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private UserDTO userDTO;
    private JwtAuthenticationDTO jwtAuthenticationDTO;
    private UserUpdateDTO userUpdateDTO;
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {

        User user = TestDataFactory.createUser();
        objectMapper = new ObjectMapper();
        customUserDetails = new CustomUserDetails(user);
        jwtAuthenticationDTO = new JwtAuthenticationDTO();
        jwtAuthenticationDTO.setToken("token");
        jwtAuthenticationDTO.setRefreshToken("refreshToken");
        userDTO = TestDataFactory.createUserDTO();
        userUpdateDTO = TestDataFactory.createUserUpdateDTO();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /api/v1/user/{id}: Получение пользователя по ID")
    void getUserById() throws Exception{

        when(userService.getUserById(userDTO.getId())).thenReturn(userDTO);

        mockMvc.perform(get("/api/v1/user/{id}", userDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDTO)));

        verify(userService, times(1)).getUserById(userDTO.getId());
    }

    @Test
    @DisplayName("POST /api/v1/user/update: Обновление профиля пользователя")
    void updateUser() throws Exception{

        when(userService.userUpdate(userUpdateDTO)).thenReturn(jwtAuthenticationDTO);

        mockMvc.perform(post("/api/v1/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(jwtAuthenticationDTO)));

        verify(userService, times(1)).userUpdate(userUpdateDTO);
    }

    @Test
    @DisplayName("GET /api/v1/user/me: Получение текущего аутентифицированного пользователя")
    void getCurrentUser() throws Exception{

        when(userService.getUserByEmail(customUserDetails.getUsername())).thenReturn(userDTO);

        mockMvc.perform(get("/api/v1/user/me"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDTO)));

        verify(userService, times(1)).getUserByEmail(customUserDetails.getUsername());
    }
}