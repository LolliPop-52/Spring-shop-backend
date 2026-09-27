package com.example.spring_shop.domain;

import com.example.spring_shop.fixture.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class BucketItemTest {

    private BucketItem bucketItem;

    @BeforeEach
    void setUp() {
        bucketItem = TestDataFactory.createBucketItem();
        bucketItem.setAmount(BigDecimal.valueOf(3));
        bucketItem.getProduct().setPrice(BigDecimal.valueOf(12000));
    }

    @Test
    @DisplayName("getTotalPrice: Расчет общей стоимости позиции в корзине")
    void getTotalPrice_Success() {

        BigDecimal result = bucketItem.getTotalPrice();
        assertEquals(BigDecimal.valueOf(36000), result);

    }
}