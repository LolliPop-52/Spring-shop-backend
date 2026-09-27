package com.example.spring_shop.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BucketDTO {
    private Long id;
    private String userEmail;
    private List<BucketItemDTO> items;
    private BigDecimal totalItemsAmount;
    private BigDecimal totalPrice;
}
