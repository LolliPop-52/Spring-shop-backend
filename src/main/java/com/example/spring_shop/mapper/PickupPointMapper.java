package com.example.spring_shop.mapper;

import com.example.spring_shop.domain.PickupPoint;
import com.example.spring_shop.dto.PickupPointDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PickupPointMapper {

    public PickupPointDTO toDto(PickupPoint pickupPoint){
        return PickupPointDTO.builder()
                .id(pickupPoint.getId())
                .address(pickupPoint.getAddress())
                .openingTime(pickupPoint.getOpeningTime())
                .closingTime(pickupPoint.getClosingTime())
                .build();
    }
}
