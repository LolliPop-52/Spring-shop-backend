package com.example.spring_shop.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BucketDTO {
    private Long id;
    // private List<BucketItemDTO> items;
    private BigDecimal totalSum;

    // public void agregate(){
    //     BigDecimal sum = new BigDecimal(0);
    //     for(BucketItemDTO bucketItemDTO : items){
    //         sum.add(bucketItemDTO.getSum());
    //     }
    // }
}
