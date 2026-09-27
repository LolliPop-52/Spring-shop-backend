package com.example.spring_shop.mapper;

import com.example.spring_shop.domain.OrderDetails;
import com.example.spring_shop.dto.OrderDetailsDTO;
import com.example.spring_shop.fixture.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class OrderDetailsMapperTest {

    private OrderDetailsMapper orderDetailsMapper;
    private OrderDetails orderDetails;
    private OrderDetailsDTO orderDetailsDTO;

    @BeforeEach
    void setUp() {
        orderDetailsMapper = new OrderDetailsMapper();
        orderDetails = TestDataFactory.createOrderDetails();
        orderDetailsDTO = TestDataFactory.createOrderDetailsDTO();
    }

    @Test
    @DisplayName("toDTO: Успешное преобразование OrderDetails в OrderDetailsDTO")
    void toDTO() {
        OrderDetailsDTO result = orderDetailsMapper.toDTO(orderDetails);

        assertEquals(orderDetailsDTO, result);
    }
}