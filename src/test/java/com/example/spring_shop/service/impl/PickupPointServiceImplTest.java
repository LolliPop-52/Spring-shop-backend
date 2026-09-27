package com.example.spring_shop.service.impl;

import com.example.spring_shop.domain.PickupPoint;
import com.example.spring_shop.dto.PickupPointDTO;
import com.example.spring_shop.fixture.TestDataFactory;
import com.example.spring_shop.mapper.PickupPointMapper;
import com.example.spring_shop.repository.PickupPointRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class PickupPointServiceImplTest {

    @Mock
    private PickupPointRepository pickupPointRepository;

    @Mock
    private PickupPointMapper pickupPointMapper;

    @InjectMocks
    private PickupPointServiceImpl pickupPointService;

    private PickupPoint pickupPoint;
    private PickupPointDTO pickupPointDTO;
    private Pageable pageRequest;
    private Page<PickupPoint> pickupPointPage;

    @BeforeEach
    void setUp() {
        pickupPointDTO = TestDataFactory.createPickupPointDTO();
        pickupPoint = TestDataFactory.createPickupPoint();
        pageRequest = PageRequest.of(0, 10);
        pickupPointPage = new PageImpl<>(List.of(pickupPoint), pageRequest, 1);
    }

    @Test
    @DisplayName("findAllPickupPoints: Получение списка пунктов выдачи с пагинацией")
    void findAllPickupPoints_Success() {

        Page<PickupPointDTO> pickupPointDTOPage = new PageImpl<>(List.of(pickupPointDTO), pageRequest, 1);

        when(pickupPointRepository.findAll(pageRequest)).thenReturn(pickupPointPage);
        when(pickupPointMapper.toDto(pickupPoint)).thenReturn(pickupPointDTO);

        Page<PickupPointDTO> result = pickupPointService.findAllPickupPoints(pageRequest);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(TestDataFactory.DEFAULT_ADDRESS, result.getContent().get(0).getAddress());
        verify(pickupPointRepository, times(1)).findAll(pageRequest);
        verify(pickupPointMapper, times(1)).toDto(pickupPoint);

    }
}