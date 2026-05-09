package com.example.spring_shop.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailsDTO {
    private Long id;
    private Long orderId;
    private Long productId;
    private BigDecimal amount;
    private BigDecimal totalPrice;
}
