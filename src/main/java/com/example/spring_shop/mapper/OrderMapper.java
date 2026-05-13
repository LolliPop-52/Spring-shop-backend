package com.example.spring_shop.mapper;

import com.example.spring_shop.domain.DeliveryStatus;
import com.example.spring_shop.domain.Order;
import com.example.spring_shop.dto.OrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderMapper {

    private final OrderDetailsMapper orderDetailsMapper;

    public OrderDTO toDto(Order order){
        return OrderDTO.builder()
                .id(order.getId())
                .address(order.getPickupPoint().getAddress())
                .pickupPointId(order.getPickupPoint().getId())
                .details(order.getOrderDetails().stream()
                        .map(orderDetailsMapper::toDTO).toList())
                .totalSum(order.getTotalPrice())
                .deliveryStatus(DeliveryStatus.PROCESSING.name())
                .paymentStatus(DeliveryStatus.PROCESSING.name())
                .createdTime(order.getCreatedTime())
                .updatedTime(order.getUpdatedTime())
                .build();
    }

}
