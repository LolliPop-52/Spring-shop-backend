package com.example.spring_shop.controller;

import com.example.spring_shop.domain.User;
import com.example.spring_shop.dto.ActiveOrdersDTO;
import com.example.spring_shop.dto.CreatorNewOrderDTO;
import com.example.spring_shop.dto.OrderDTO;
import com.example.spring_shop.dto.PickupPointDTO;
import com.example.spring_shop.fixture.TestDataFactory;
import com.example.spring_shop.security.CustomUserDetails;
import com.example.spring_shop.security.JwtFilter;
import com.example.spring_shop.service.OrderService;
import com.example.spring_shop.service.PickupPointService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private PickupPointService pickupPointService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private CreatorNewOrderDTO creatorNewOrderDTO;
    private OrderDTO orderDTO;

    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        User user = TestDataFactory.createUser();

        customUserDetails = new CustomUserDetails(user);
        orderDTO = TestDataFactory.createOrderDTO();
        creatorNewOrderDTO = TestDataFactory.createCreatorNewOrderDTO();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(customUserDetails, null,  customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("POST /api/v1/order/new: Оформление нового заказа")
    void newOrder() throws Exception {

        CreatorNewOrderDTO fakeCreatorNewOrderDTO = creatorNewOrderDTO.toBuilder().userEmail("fake@gmail.com").build();

        when(orderService.createOrder(any(CreatorNewOrderDTO.class))).thenReturn(orderDTO);

        mockMvc.perform(post("/api/v1/order/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fakeCreatorNewOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(orderDTO)));

        ArgumentCaptor<CreatorNewOrderDTO> captor = ArgumentCaptor.forClass(CreatorNewOrderDTO.class);

        verify(orderService, times(1)).createOrder(captor.capture());

        assertEquals(
                customUserDetails.getUsername(),
                captor.getValue().getUserEmail(),
                "Почта не считана с UserDetails"
        );

    }

    @Test
    @DisplayName("GET /api/v1/order/orders: Получение всех активных заказов пользователя")
    void getMyOrders() throws Exception {

        ActiveOrdersDTO activeOrdersDTO = TestDataFactory.createActiveOrdersDTO();

        when(orderService.getAllActiveOrder(customUserDetails.getUsername())).thenReturn(activeOrdersDTO);

        mockMvc.perform(get("/api/v1/order/orders"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(activeOrdersDTO)));

        verify(orderService, times(1)).getAllActiveOrder(customUserDetails.getUsername());
    }

    @Test
    @DisplayName("GET /api/v1/order/pickup-points: Получение всех пунктов выдачи")
    void getAllPickupPoints() throws Exception{

        PickupPointDTO pickupPointDTO = TestDataFactory.createPickupPointDTO();

        Pageable pageRequest = PageRequest.of(0, 10);
        Page<PickupPointDTO> page = new PageImpl<>(List.of(pickupPointDTO), pageRequest, 1);
        PagedModel<PickupPointDTO> pickupPointDTOPagedModel = new PagedModel<>(page);

        when(pickupPointService.findAllPickupPoints(pageRequest)).thenReturn(page);

        mockMvc.perform(get("/api/v1/order/pickup-points")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(pickupPointDTOPagedModel)));

        verify(pickupPointService, times(1)).findAllPickupPoints(pageRequest);

    }
}