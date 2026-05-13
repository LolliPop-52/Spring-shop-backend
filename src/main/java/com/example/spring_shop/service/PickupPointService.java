package com.example.spring_shop.service;

import com.example.spring_shop.dto.PickupPointDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PickupPointService {
    Page<PickupPointDTO> findAllPickupPoints(Pageable pageable);
}
