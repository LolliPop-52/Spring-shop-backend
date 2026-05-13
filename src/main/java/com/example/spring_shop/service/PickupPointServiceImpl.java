package com.example.spring_shop.service;

import com.example.spring_shop.domain.PickupPoint;
import com.example.spring_shop.domain.Product;
import com.example.spring_shop.dto.PickupPointDTO;
import com.example.spring_shop.mapper.PickupPointMapper;
import com.example.spring_shop.repository.PickupPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PickupPointServiceImpl implements PickupPointService {

    private final PickupPointRepository pickupPointRepository;
    private final PickupPointMapper pickupPointMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<PickupPointDTO> findAllPickupPoints(Pageable pageable) {
        Page<PickupPoint> pickupPointPage  = pickupPointRepository.findAll(pageable);
        return pickupPointPage.map(pickupPointMapper::toDto);
    }

}
