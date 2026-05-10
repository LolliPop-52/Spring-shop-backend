package com.example.spring_shop.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatorNewOrderDTO {
    private String userEmail;
    private BigDecimal totalPrice;
    private BigDecimal totalAmount;
    private Long addressId;
    private List<CreatorNewOrderDetailsDTO> orderDetails;
}
