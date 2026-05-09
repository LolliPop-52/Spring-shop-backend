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
public class SmallProductDTO {

    private Long id;
    private BigDecimal price;
    private String title;
    private String imageUrl;

}
