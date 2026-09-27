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
@Builder(toBuilder = true)
public class CreatorNewOrderDTO {
    private String userEmail;
    private Long addressId;
    private String paymentType;
    private List<CreatorNewOrderDetailsDTO> orderDetails;
}
