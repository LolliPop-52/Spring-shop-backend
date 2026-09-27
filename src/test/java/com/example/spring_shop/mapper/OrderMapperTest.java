package com.example.spring_shop.mapper;

import com.example.spring_shop.domain.Order;
import com.example.spring_shop.domain.OrderDetails;
import com.example.spring_shop.dto.OrderDTO;
import com.example.spring_shop.dto.OrderDetailsDTO;
import com.example.spring_shop.fixture.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class OrderMapperTest {

    @Mock
    private OrderDetailsMapper orderDetailsMapper;

    @InjectMocks
    private OrderMapper orderMapper;

    private Order order;
    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        order = TestDataFactory.createOrder();
        orderDTO = TestDataFactory.createOrderDTO();
    }

    @Test
    @DisplayName("toDto: Успешное преобразование Order в OrderDTO")
    void toDto() {

        String date = LocalDate.of(2026, 12, 30).atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        orderDTO.getDetails().getFirst().setEstimatedDeliveryDate(date);

        when(orderDetailsMapper.toDTO(any(OrderDetails.class))).thenAnswer(invocation -> {
                OrderDetails arg = invocation.getArgument(0);
                return OrderDetailsDTO.builder()
                        .id(arg.getId())
                        .orderId(arg.getOrder().getId())
                        .productId(arg.getProduct().getId())
                        .amount(arg.getAmount())
                        .price(arg.getProduct().getPrice())
                        .totalPrice(arg.getTotalPrice())
                        .deliveryStatus(arg.getDeliveryStatus().name())
                        .paymentType(arg.getPaymentType().name())
                        .paymentStatus(arg.getPaymentStatus().name())
                        .estimatedDeliveryDate(date)
                        .build();
            }
        );

        OrderDTO result = orderMapper.toDto(order);

        assertAll("OrderDTO field comparison",
                () -> assertEquals(orderDTO.getId(), result.getId(), "id"),
                () -> assertEquals(orderDTO.getAddress(), result.getAddress(), "address"),
                () -> assertEquals(orderDTO.getPickupPointId(), result.getPickupPointId(), "pickupPointId"),
                () -> assertEquals(orderDTO.getTotalSum(), result.getTotalSum(), "totalSum"),
                () -> assertEquals(orderDTO.getDeliveryStatus(), result.getDeliveryStatus(), "deliveryStatus"),
                () -> assertEquals(orderDTO.getPaymentStatus(), result.getPaymentStatus(), "paymentStatus"),
                () -> assertEquals(orderDTO.getCreatedTime(), result.getCreatedTime(), "createdTime"),
                () -> assertEquals(orderDTO.getUpdatedTime(), result.getUpdatedTime(), "updatedTime")
        );

        if (orderDTO.getDetails() != null) {
            assertNotNull(result.getDetails(), "details list should not be null");
            assertEquals(orderDTO.getDetails().size(), result.getDetails().size(), "details list size");

            for (int i = 0; i < orderDTO.getDetails().size(); i++) {
                OrderDetailsDTO expectedDetails = orderDTO.getDetails().get(i);
                OrderDetailsDTO actualDetails = result.getDetails().get(i);
                final int index = i;

                assertAll("OrderDetailsDTO index " + index,
                        () -> assertEquals(expectedDetails.getId(), actualDetails.getId(), "details[" + index + "].id"),
                        () -> assertEquals(expectedDetails.getOrderId(), actualDetails.getOrderId(), "details[" + index + "].orderId"),
                        () -> assertEquals(expectedDetails.getProductId(), actualDetails.getProductId(), "details[" + index + "].productId"),
                        () -> assertEquals(expectedDetails.getAmount(), actualDetails.getAmount(), "details[" + index + "].amount"),
                        () -> assertEquals(expectedDetails.getPrice(), actualDetails.getPrice(), "details[" + index + "].price"),
                        () -> assertEquals(expectedDetails.getTotalPrice(), actualDetails.getTotalPrice(), "details[" + index + "].totalPrice"),
                        () -> assertEquals(expectedDetails.getDeliveryStatus(), actualDetails.getDeliveryStatus(), "details[" + index + "].deliveryStatus"),
                        () -> assertEquals(expectedDetails.getPaymentType(), actualDetails.getPaymentType(), "details[" + index + "].paymentType"),
                        () -> assertEquals(expectedDetails.getPaymentStatus(), actualDetails.getPaymentStatus(), "details[" + index + "].paymentStatus"),
                        () -> assertEquals(expectedDetails.getEstimatedDeliveryDate(), actualDetails.getEstimatedDeliveryDate(), "details[" + index + "].estimatedDeliveryDate")
                );
            }
        } else {
            assertEquals(orderDTO.getDetails(), result.getDetails(), "details");
        }
    }
}