package com.example.spring_shop.mapper;

import com.example.spring_shop.domain.BucketItem;
import com.example.spring_shop.dto.BucketItemDTO;
import com.example.spring_shop.dto.SmallProductDTO;
import com.example.spring_shop.fixture.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class BucketItemMapperTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private BucketItemMapper bucketItemMapper;

    private BucketItem bucketItem;
    private BucketItemDTO bucketItemDTO;
    private SmallProductDTO smallProductDTO;

    @BeforeEach
    void setUp() {
        bucketItem = TestDataFactory.createBucketItem();
        bucketItemDTO = TestDataFactory.createBucketItemDTO();
        smallProductDTO = TestDataFactory.createSmallProductDTO();
    }

    @Test
    @DisplayName("toDto: Успешное преобразование BucketItem в BucketItemDTO")
    void toDto_Success() {
        when(productMapper.toSmallDto(bucketItem.getProduct())).thenReturn(smallProductDTO);

        BucketItemDTO result = bucketItemMapper.toDto(bucketItem);

        assertEquals(bucketItemDTO, result);
        verify(productMapper, times(1)).toSmallDto(bucketItem.getProduct());
    }
}
