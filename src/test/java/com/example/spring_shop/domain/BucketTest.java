package com.example.spring_shop.domain;

import com.example.spring_shop.fixture.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class BucketTest {


    private Bucket bucket;
    private BucketItem bucketItem;

    @BeforeEach
    void setUp() {
        bucket = TestDataFactory.createBucket();
        bucketItem = TestDataFactory.createBucketItem();

    }

    @Test
    @DisplayName("addItem: Успешное добавление позиции в корзину")
    void addItem_Success() {

        assertEquals(BigDecimal.ZERO, bucket.getItemsAmount());

        bucket.addItem(bucketItem);

        assertEquals(BigDecimal.ONE, bucket.getItemsAmount());
        assertEquals(bucket, bucketItem.getBucket());

    }

    @Test
    @DisplayName("deleteItem: Успешное удаление позиции из корзины")
    void deleteItem_Success() {
        bucket = TestDataFactory.createBucketWithItems(1);
        bucketItem =  bucket.getItems().getFirst();

        assertEquals(bucket, bucketItem.getBucket());
        assertEquals(BigDecimal.ONE, bucket.getItemsAmount());

        bucket.deleteItem(bucketItem);

        assertEquals(BigDecimal.ZERO, bucket.getItemsAmount());
        assertNull(bucketItem.getBucket());

    }

    @Test
    @DisplayName("getItemsAmount: Получение общего количества товаров в корзине")
    void getItemsAmount_Success() {

        bucket = TestDataFactory.createBucketWithItems(3);

        BigDecimal result = bucket.getItemsAmount();

        assertEquals(BigDecimal.valueOf(3), result);

    }

    @Test
    @DisplayName("getTotalPrice: Расчет общей стоимости всех товаров в корзине")
    void getTotalPrice_Success() {

        BucketItem bucketItem1 = TestDataFactory.createBucketItem();
        BucketItem bucketItem2 = TestDataFactory.createBucketItem();
        BucketItem bucketItem3 = TestDataFactory.createBucketItem();
        bucketItem1.getProduct().setPrice(BigDecimal.valueOf(1000));
        bucketItem2.getProduct().setPrice(BigDecimal.valueOf(2000));
        bucketItem3.getProduct().setPrice(BigDecimal.valueOf(3000));
        bucketItem3.setAmount(BigDecimal.TWO);

        bucket.setItems(List.of(bucketItem1, bucketItem2, bucketItem3));

        BigDecimal result = bucket.getTotalPrice();

        // 1000 * 1 + 2000 * 1 + 3000 * 2
        assertEquals(BigDecimal.valueOf(9000), result);

    }
}