package com.example.spring_shop.mapper;

import com.example.spring_shop.domain.Category;
import com.example.spring_shop.dto.CategoryDTO;
import com.example.spring_shop.fixture.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class CategoryMapperTest {

    private CategoryMapper categoryMapper;
    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        categoryMapper = new CategoryMapper();
        category = TestDataFactory.createCategory();
        categoryDTO = TestDataFactory.createCategoryDTO();
    }

    @Test
    @DisplayName("toDTO: Успешное преобразование Category в CategoryDTO")
    void toDTO_Success() {
        CategoryDTO result = categoryMapper.toDTO(category);

        assertEquals(categoryDTO, result);
    }
}