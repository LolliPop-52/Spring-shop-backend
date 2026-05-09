package com.example.spring_shop.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BucketItemDTO {
    private Long id;
    private Long bucketId;
    private SmallProductDTO smallProductDTO;
    private BigDecimal amount;
    private BigDecimal totalPrice;
}
