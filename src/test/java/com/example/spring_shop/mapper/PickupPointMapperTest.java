package com.example.spring_shop.mapper;

import com.example.spring_shop.domain.PickupPoint;
import com.example.spring_shop.dto.PickupPointDTO;
import com.example.spring_shop.fixture.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class PickupPointMapperTest {

    private PickupPointMapper pickupPointMapper;
    private PickupPoint pickupPoint;
    private PickupPointDTO pickupPointDTO;

    @BeforeEach
    void setUp() {
        pickupPointMapper = new PickupPointMapper();
        pickupPoint = TestDataFactory.createPickupPoint();
        pickupPointDTO = TestDataFactory.createPickupPointDTO();
    }

    @Test
    @DisplayName("toDto: Успешное преобразование PickupPoint в PickupPointDTO")
    void toDto_Success() {
        PickupPointDTO result = pickupPointMapper.toDto(pickupPoint);

        assertEquals(pickupPointDTO, result);
    }
}
