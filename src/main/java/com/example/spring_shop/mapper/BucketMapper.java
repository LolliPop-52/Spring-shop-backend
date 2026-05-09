package com.example.spring_shop.mapper;


import com.example.spring_shop.domain.Bucket;
import com.example.spring_shop.dto.BucketDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BucketMapper {

    private final BucketItemMapper bucketItemMapper;

    public BucketDTO toDto(Bucket bucket) {
        if (bucket == null)
            return null;
        return BucketDTO.builder()
                .id(bucket.getId())
                .userEmail(bucket.getUser().getEmail())
                .items(bucket.getItems().stream()
                        .map(bucketItemMapper::toDto).collect(Collectors.toList()))
                .totalItemsAmount(bucket.getItemsAmount())
                .totalPrice(bucket.getTotalPrice())
                .build();
    }

}
