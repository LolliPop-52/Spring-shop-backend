package com.example.spring_shop.repository;

import com.example.spring_shop.domain.PickupPoint;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PickupPointRepository extends JpaRepository<PickupPoint, Long> {
}
