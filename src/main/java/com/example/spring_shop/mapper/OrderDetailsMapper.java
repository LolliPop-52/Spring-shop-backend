package com.example.spring_shop.mapper;

import com.example.spring_shop.domain.OrderDetails;
import com.example.spring_shop.dto.OrderDetailsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class OrderDetailsMapper {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public OrderDetailsDTO toDTO(OrderDetails orderDetails){
        return OrderDetailsDTO.builder()
                .id(orderDetails.getId())
                .orderId(orderDetails.getOrder().getId())
                .productId(orderDetails.getProduct().getId())
                .amount(orderDetails.getAmount())
                .price(orderDetails.getProduct().getPrice())
                .totalPrice(orderDetails.getTotalPrice())
                .deliveryStatus(orderDetails.getDeliveryStatus().name())
                .paymentType(orderDetails.getPaymentType().name())
                .paymentStatus(orderDetails.getPaymentStatus().name())
                .estimatedDeliveryDate(orderDetails.getCreatedTime().plusDays(3).format(formatter))
                .build();

    }
}
