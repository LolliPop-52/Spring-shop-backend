package com.example.spring_shop.service.impl;

import com.example.spring_shop.domain.Bucket;
import com.example.spring_shop.domain.BucketItem;
import com.example.spring_shop.domain.Product;
import com.example.spring_shop.domain.User;
import com.example.spring_shop.dto.BucketDTO;
import com.example.spring_shop.dto.BucketItemDTO;
import com.example.spring_shop.dto.CreatorNewOrderDTO;
import com.example.spring_shop.dto.ModifyBucketItemDTO;
import com.example.spring_shop.exception_handler.ResourceNotFoundException;
import com.example.spring_shop.fixture.TestDataFactory;
import com.example.spring_shop.mapper.BucketItemMapper;
import com.example.spring_shop.mapper.BucketMapper;
import com.example.spring_shop.repository.BucketRepository;
import com.example.spring_shop.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class BucketServiceImplTest {

    @Mock
    private BucketMapper bucketMapper;

    @Mock
    private BucketItemMapper bucketItemMapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BucketRepository bucketRepository;

    @InjectMocks
    private BucketServiceImpl bucketService;

    private ModifyBucketItemDTO modifyBucketItemDTO;
    private User user;
    private Bucket bucket;
    private BucketItem bucketItem;
    private BucketDTO bucketDTO;
    private Product product;

    @BeforeEach
    void setUp() {

        bucket = TestDataFactory.createBucketWithItems(1);
        user = bucket.getUser();
        bucketItem = bucket.getItems().getFirst();
        product = bucketItem.getProduct();
        bucketItem.setProduct(product);
        bucketItem.setBucket(bucket);
        bucket.setItems(new ArrayList<>(List.of(bucketItem)));

        bucketDTO = TestDataFactory.createBucketDTO();
        modifyBucketItemDTO = TestDataFactory.createModifyBucketItemDTO();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("addItemToBucket: Ошибка при отрицательном количестве")
    void addItemToBucket_NegativeAmount_ThrowsException() {
        modifyBucketItemDTO.setAmount(BigDecimal.valueOf(-1));

        assertThatThrownBy(() -> bucketService.addItemToBucket(modifyBucketItemDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Amount must be positive");

        verify(bucketRepository, never()).save(any(Bucket.class));
        verify(bucketMapper, never()).toDto(any(Bucket.class));
    }

    @Test
    @DisplayName("addItemToBucket: Товар не найден при добавлении")
    void addItemToBucket_ProductNotFound_ThrowsException() {
        //  ------------------------> ПЕРЕДЕЛАТЬ <-----------------------
        bucket.setItems(new ArrayList<>());

        when(bucketRepository.findByUserEmail(modifyBucketItemDTO.getUserEmail())).thenReturn(Optional.of(bucket));
        when(productRepository.findById(modifyBucketItemDTO.getProductId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bucketService.addItemToBucket(modifyBucketItemDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource with id: " + modifyBucketItemDTO.getProductId() + " not found");

        verify(bucketRepository, times(1)).findByUserEmail(modifyBucketItemDTO.getUserEmail());
        verify(productRepository, times(1)).findById(modifyBucketItemDTO.getProductId());

        verify(bucketRepository, never()).save(any(Bucket.class));
        verify(bucketMapper, never()).toDto(any(Bucket.class));

    }

    @Test
    @DisplayName("addItemToBucket: Добавление нового товара в корзину")
    void addItemToBucket_NewItem_Success() {

        assertEquals(1, bucket.getItems().size());

        modifyBucketItemDTO = modifyBucketItemDTO.toBuilder().productId(2L).build();
        when(bucketRepository.findByUserEmail(modifyBucketItemDTO.getUserEmail())).thenReturn(Optional.of(bucket));
        when(productRepository.findById(modifyBucketItemDTO.getProductId())).thenReturn(Optional.of(product));
        when(bucketMapper.toDto(bucket)).thenReturn(bucketDTO);

        BucketDTO result = bucketService.addItemToBucket(modifyBucketItemDTO);

        assertNotNull(result);
        assertEquals(2, bucket.getItems().size());
        verify(bucketRepository, times(1)).save(bucket);
    }

    @Test
    @DisplayName("addItemToBucket: Увеличение количества существующего товара")
    void addItemToBucket_ExistingItem_IncrementsAmount() {

        assertEquals(1, bucket.getItems().size());

        when(bucketRepository.findByUserEmail(modifyBucketItemDTO.getUserEmail())).thenReturn(Optional.of(bucket));
        when(bucketMapper.toDto(bucket)).thenReturn(bucketDTO);

        BucketDTO result = bucketService.addItemToBucket(modifyBucketItemDTO);

        assertNotNull(result);
        assertEquals(1, bucket.getItems().size());
        assertEquals(BigDecimal.TWO, bucket.getItems().getFirst().getAmount());
        verify(bucketRepository).save(bucket);
    }

    @Test
    @DisplayName("deleteItemOnBucket: Товар не найден в корзине")
    void deleteItemOnBucket_ProductNotFound_ThrowsException() {

        assertEquals(1, bucket.getItems().size());
        bucket.setItems(new ArrayList<>());

        when(bucketRepository.findByUserEmail(modifyBucketItemDTO.getUserEmail())).thenReturn(Optional.of(bucket));

        assertThatThrownBy(() -> bucketService.deleteItemOnBucket(modifyBucketItemDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource with id: " + modifyBucketItemDTO.getProductId() + " not found");

        verify(bucketRepository, never()).save(any(Bucket.class));
        verify(bucketMapper, never()).toDto(any(Bucket.class));
    }

    @Test
    @DisplayName("deleteItemOnBucket: Удаление товара из корзины при количестве < 1")
    void deleteItemOnBucket_RemovesItemWhenAmountLessThanOne() {

        assertEquals(1, bucket.getItems().size());
        when(bucketRepository.findByUserEmail(modifyBucketItemDTO.getUserEmail())).thenReturn(Optional.of(bucket));
        when(bucketMapper.toDto(bucket)).thenReturn(bucketDTO);

        bucketService.deleteItemOnBucket(modifyBucketItemDTO);

        verify(bucketRepository, times(1)).save(bucket);
        assertEquals(0, bucket.getItems().size());

    }

    @Test
    @DisplayName("deleteItemOnBucket: Уменьшение количества товара (остается >= 1)")
    void deleteItemOnBucket_ReducesAmount() {

        bucketItem.setAmount(BigDecimal.TWO);
        assertEquals(1, bucket.getItems().size());
        assertEquals(BigDecimal.TWO, bucket.getItems().getFirst().getAmount());

        when(bucketRepository.findByUserEmail(modifyBucketItemDTO.getUserEmail())).thenReturn(Optional.of(bucket));
        when(bucketMapper.toDto(bucket)).thenReturn(bucketDTO);

        bucketService.deleteItemOnBucket(modifyBucketItemDTO);

        verify(bucketRepository, times(1)).save(bucket);
        assertEquals(1, bucket.getItems().size());
        assertEquals(BigDecimal.ONE, bucket.getItems().getFirst().getAmount());
    }

    @Test
    @DisplayName("takeAllItem: Получение списка всех позиций в корзине")
    void takeAllItem_Success() {

        BucketItemDTO itemDTO = TestDataFactory.createBucketItemDTO();
        when(bucketRepository.findByUserEmail(modifyBucketItemDTO.getUserEmail())).thenReturn(Optional.of(bucket));
        when(bucketItemMapper.toDto(bucketItem)).thenReturn(itemDTO);

        List<BucketItemDTO> result = bucketService.takeAllItem(modifyBucketItemDTO.getUserEmail());

        assertNotNull(result);

        assertEquals(1, result.size());
        assertEquals(itemDTO.getId(), result.getFirst().getId());

    }

    @Test
    @DisplayName("clearBucket: Очистка корзины")
    void clearBucket_Success() {

        assertEquals(1, bucket.getItems().size());

        when(bucketRepository.findByUserEmail(modifyBucketItemDTO.getUserEmail())).thenReturn(Optional.of(bucket));
        when(bucketMapper.toDto(bucket)).thenReturn(bucketDTO);

        BucketDTO result = bucketService.clearBucket(modifyBucketItemDTO.getUserEmail());

        assertNotNull(result);
        assertEquals(0, bucket.getItems().size());
        verify(bucketRepository, times(1)).save(bucket);
    }

    @Test
    @DisplayName("clearOrderedItems: Удаление заказанных позиций из корзины")
    void clearOrderedItems_Success() {

        assertFalse(bucket.getItems().isEmpty());

        when(bucketRepository.findByUserEmail(modifyBucketItemDTO.getUserEmail())).thenReturn(Optional.of(bucket));

        when(bucketMapper.toDto(bucket)).thenReturn(bucketDTO);

        BucketDTO result = bucketService.clearBucket(modifyBucketItemDTO.getUserEmail());

        assertNotNull(result);
        assertTrue(bucket.getItems().isEmpty());
        verify(bucketRepository).save(bucket);

    }

    @Test
    @DisplayName("clearOrderedItems: Не найден указанный пользователь")
    void clearOrderedItems_ProductNotFound_ThrowsException() {

        CreatorNewOrderDTO creatorNewOrderDTO = TestDataFactory.createCreatorNewOrderDTO();

        when(bucketRepository.findByUserEmail(modifyBucketItemDTO.getUserEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bucketService.clearOrderedItems(creatorNewOrderDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource with name: " + modifyBucketItemDTO.getUserEmail() + " not found");

        verify(bucketMapper, never()).toDto(any(Bucket.class));
    }

    @Test
    @DisplayName("getBucketByUser: Получение корзины по email")
    void getBucketByUser_Success() {

        when(bucketMapper.toDto(bucket)).thenReturn(bucketDTO);
        when(bucketRepository.findByUserEmail(modifyBucketItemDTO.getUserEmail())).thenReturn(Optional.of(bucket));

        bucketService.getBucketByUser(modifyBucketItemDTO.getUserEmail());

        verify(bucketMapper, times(1)).toDto(bucket);

    }

    @Test
    @DisplayName("getBucketByUser: Корзина пользователя не найдена, выброс ResourceNotFoundException")
    void getBucketByUser_UserNotFound_ThrowsException() {

        when(bucketRepository.findByUserEmail(modifyBucketItemDTO.getUserEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bucketService.getBucketByUser(modifyBucketItemDTO.getUserEmail()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource with name: " + modifyBucketItemDTO.getUserEmail() + " not found");

    }
}