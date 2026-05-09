package com.example.spring_shop.mapper;

import com.example.spring_shop.domain.BucketItem;
import com.example.spring_shop.dto.BucketItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BucketItemMapper {

    private final ProductMapper productMapper;

    public BucketItemDTO toDto(BucketItem bucketItem){
        return BucketItemDTO.builder()
                .id(bucketItem.getId())
                .bucketId(bucketItem.getBucket().getId())
                .smallProductDTO(productMapper.toSmallDto(bucketItem.getProduct()))
                .amount(bucketItem.getAmount())
                .totalPrice(bucketItem.getTotalPrice())
                .build();
    }

}
