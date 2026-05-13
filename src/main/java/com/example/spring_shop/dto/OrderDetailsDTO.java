package com.example.spring_shop.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailsDTO {
    private Long id;
    private Long orderId;
    private Long productId;
    private BigDecimal amount;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private String deliveryStatus;
    private String paymentType;
    private String paymentStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String estimatedDeliveryDate;
}
