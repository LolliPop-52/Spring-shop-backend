package com.example.spring_shop.controller;

import com.example.spring_shop.domain.User;
import com.example.spring_shop.dto.BucketDTO;
import com.example.spring_shop.dto.ModifyBucketItemDTO;
import com.example.spring_shop.fixture.TestDataFactory;
import com.example.spring_shop.security.CustomUserDetails;
import com.example.spring_shop.security.JwtFilter;
import com.example.spring_shop.service.BucketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BucketController.class)
@AutoConfigureMockMvc(addFilters = false)
class BucketControllerTest {

    @MockitoBean
    private BucketService bucketService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Autowired
    private MockMvc mockMvc;


    private CustomUserDetails customUserDetails;
    private BucketDTO bucketDto;
    private ObjectMapper objectMapper;
    private ModifyBucketItemDTO requestDto;


    @BeforeEach
    void setUp() {
        User user = TestDataFactory.createUser();

        requestDto = TestDataFactory.createModifyBucketItemDTO();
        customUserDetails = new CustomUserDetails(user);
        bucketDto = TestDataFactory.createBucketDTO();
        objectMapper = new ObjectMapper();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    @Test
    @DisplayName("GET /api/v1/bucket: Получение корзины текущего пользователя")
    void getBucket() throws Exception {
        when(bucketService.getBucketByUser(customUserDetails.getUsername())).thenReturn(bucketDto);

        mockMvc.perform(get("/api/v1/bucket"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bucketDto)));
        verify(bucketService, times(1)).getBucketByUser(customUserDetails.getUsername());
    }

    @Test
    @DisplayName("POST /api/v1/bucket: Добавление товара в корзину")
    void addBucketItem() throws Exception {

        ModifyBucketItemDTO requestDtoWithFakeEmail = requestDto.toBuilder().userEmail("fake@gmail.com").build();

        when(bucketService.addItemToBucket(any(ModifyBucketItemDTO.class))).thenReturn(bucketDto);

        mockMvc.perform(post("/api/v1/bucket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDtoWithFakeEmail)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(bucketDto)));

        ArgumentCaptor<ModifyBucketItemDTO> captor = ArgumentCaptor.forClass(ModifyBucketItemDTO.class);

        verify(bucketService, times(1)).addItemToBucket(captor.capture());

        assertEquals(
                customUserDetails.getUsername(),
                captor.getValue().getUserEmail(),
                "Почта не считана с UserDetails"
        );

    }

    @Test
    @DisplayName("PATCH /api/v1/bucket: Удаление/уменьшение позиции в корзине")
    void deleteBucketItem() throws Exception {

        ModifyBucketItemDTO requestDtoWithFakeEmail = requestDto.toBuilder().userEmail("fake@gmail.com").build();

        when(bucketService.deleteItemOnBucket(any(ModifyBucketItemDTO.class))).thenReturn(bucketDto);

        mockMvc.perform(patch("/api/v1/bucket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDtoWithFakeEmail)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bucketDto)));


        ArgumentCaptor<ModifyBucketItemDTO> captor = ArgumentCaptor.forClass(ModifyBucketItemDTO.class);

        verify(bucketService, times(1)).deleteItemOnBucket(captor.capture());

        assertEquals(
                customUserDetails.getUsername(),
                captor.getValue().getUserEmail(),
                "Почта не считана с UserDetails"
        );
    }

    @Test
    @DisplayName("PUT /api/v1/bucket/clear: Очистка корзины")
    void clear() throws Exception {

        when(bucketService.clearBucket(customUserDetails.getUsername())).thenReturn(bucketDto);

        mockMvc.perform(put("/api/v1/bucket/clear"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bucketDto)));

        verify(bucketService, times(1)).clearBucket(customUserDetails.getUsername());
    }

    @Test
    @DisplayName("GET /api/v1/bucket/amount: Получение общего количества товаров в корзине")
    void getAmountOfItems() throws Exception {

        BigDecimal expectedAmount = bucketDto.getTotalItemsAmount();

        when(bucketService.getBucketByUser(customUserDetails.getUsername())).thenReturn(bucketDto);

        mockMvc.perform(get("/api/v1/bucket/amount"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectedAmount)));

        verify(bucketService, times(1)).getBucketByUser(customUserDetails.getUsername());
    }

}