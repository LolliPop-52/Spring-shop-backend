package com.example.spring_shop.domain;

import com.example.spring_shop.fixture.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class OrderTest {

    private OrderDetails orderDetails;
    private OrderDetails defaultDetails;
    private Order order;


    @BeforeEach
    void setUp() {
        orderDetails = TestDataFactory.createOrderDetails();
        orderDetails.setOrder(null);
        order = TestDataFactory.createOrder();
        defaultDetails = order.getOrderDetails().getFirst();

    }

    @Test
    @DisplayName("addDetails: Успешное добавление позиции к существующему заказу")
    void addDetails_Success() {

        assertEquals(1, order.getOrderDetails().size());
        List<OrderDetails> curOrderDetails = List.of(defaultDetails, orderDetails);

        orderDetails.setTotalPrice(BigDecimal.valueOf(30000));
        orderDetails.setAmount(BigDecimal.valueOf(3));
        order.setTotalPrice(BigDecimal.valueOf(20000));
        order.setTotalAmount(BigDecimal.valueOf(2));

        order.addDetails(orderDetails);

        assertEquals(BigDecimal.valueOf(50000) ,order.getTotalPrice());
        assertEquals(BigDecimal.valueOf(5) ,order.getTotalAmount());
        assertEquals(order, orderDetails.getOrder());
        assertThat(curOrderDetails).containsExactlyInAnyOrderElementsOf(order.getOrderDetails());

        assertEquals(2, order.getOrderDetails().size());
    }

    @Test
    @DisplayName("addDetails: Успешное добавление позиции в пустой список деталей заказа")
    void addDetails_Success_With_Null() {

        order.setTotalAmount(BigDecimal.ZERO);
        order.setTotalPrice(BigDecimal.ZERO);
        order.setOrderDetails(new ArrayList<>());

        assertEquals(0, order.getOrderDetails().size());
        List<OrderDetails> curOrderDetails = List.of(orderDetails);

        orderDetails.setTotalPrice(BigDecimal.valueOf(30000));
        orderDetails.setAmount(BigDecimal.valueOf(3));
        order.setTotalPrice(BigDecimal.valueOf(20000));
        order.setTotalAmount(BigDecimal.valueOf(2));

        order.addDetails(orderDetails);

        assertEquals(BigDecimal.valueOf(50000) ,order.getTotalPrice());
        assertEquals(BigDecimal.valueOf(5) ,order.getTotalAmount());
        assertEquals(order, orderDetails.getOrder());
        assertThat(curOrderDetails).containsExactlyInAnyOrderElementsOf(order.getOrderDetails());

        assertEquals(1, order.getOrderDetails().size());

    }
}