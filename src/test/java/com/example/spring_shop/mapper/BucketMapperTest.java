package com.example.spring_shop.mapper;

import com.example.spring_shop.domain.Bucket;
import com.example.spring_shop.domain.BucketItem;
import com.example.spring_shop.dto.BucketDTO;
import com.example.spring_shop.dto.BucketItemDTO;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class BucketMapperTest {

    @Mock
    private BucketItemMapper bucketItemMapper;

    @InjectMocks
    private BucketMapper bucketMapper;

    private Bucket bucket;
    private BucketItem bucketItem;
    private BucketItemDTO bucketItemDTO;
    private BucketDTO bucketDTO;

    @BeforeEach
    void setUp() {
        bucket = TestDataFactory.createBucketWithItems(1);
        bucketItem = bucket.getItems().getFirst();
        bucketItemDTO = TestDataFactory.createBucketItemDTO();
        bucketDTO = TestDataFactory.createBucketDTO(dto -> dto.items(List.of(bucketItemDTO)));
    }

    @Test
    @DisplayName("toDto: Успешное преобразование Bucket в BucketDTO")
    void toDto_Success() {
        when(bucketItemMapper.toDto(bucketItem)).thenReturn(bucketItemDTO);

        BucketDTO result = bucketMapper.toDto(bucket);

        assertEquals(bucketDTO, result);
        verify(bucketItemMapper, times(1)).toDto(bucketItem);
    }

    @Test
    @DisplayName("toDto: Возврат null при передаче null")
    void toDto_With_Null_Success() {
        BucketDTO result = bucketMapper.toDto(null);

        assertNull(result);
        verifyNoInteractions(bucketItemMapper);
    }
}