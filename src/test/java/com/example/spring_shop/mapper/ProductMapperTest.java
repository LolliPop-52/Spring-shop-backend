package com.example.spring_shop.mapper;

import com.example.spring_shop.domain.Product;
import com.example.spring_shop.dto.ProductDTO;
import com.example.spring_shop.dto.SmallProductDTO;
import com.example.spring_shop.fixture.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class ProductMapperTest {

    private ProductMapper productMapper;
    private Product product;


    @BeforeEach
    void setUp() {
        productMapper = new ProductMapper();
        product = TestDataFactory.createProduct();
    }

    @Test
    @DisplayName("toDto: Успешное преобразование Product в ProductDTO")
    void toDto_Success() {
        ProductDTO productDTO = TestDataFactory.createProductDTO();

        ProductDTO result = productMapper.toDto(product);

        assertEquals(productDTO, result);

    }


    @Test
    @DisplayName("toDto: Возврат null при передаче null")
    void toDto_With_Null_Success() {
        ProductDTO result = productMapper.toDto(null);

        assertNull(result);
    }

    @Test
    @DisplayName("toSmallDto: Успешное преобразование Product в SmallProductDTO")
    void toSmallDto_Success() {
        SmallProductDTO smallProductDTO = TestDataFactory.createSmallProductDTO();

        SmallProductDTO result = productMapper.toSmallDto(product);

        assertEquals(smallProductDTO, result);
    }

    @Test
    @DisplayName("toSmallDto: Возврат null при передаче null")
    void toSmallDto_With_Null_Success() {
        SmallProductDTO result = productMapper.toSmallDto(null);

        assertNull(result);
    }
}