package com.example.spring_shop.controller;

import com.example.spring_shop.dto.CategoryDTO;
import com.example.spring_shop.dto.ProductDTO;
import com.example.spring_shop.fixture.TestDataFactory;
import com.example.spring_shop.security.JwtFilter;
import com.example.spring_shop.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private ProductDTO productDto;
    private Long id;
    private Page<ProductDTO> productPage;
    private Pageable pageRequest;

    @BeforeEach
    void setUp() {

        objectMapper = new ObjectMapper();
        productDto = TestDataFactory.createProductDTO();
        id = productDto.getId();

        pageRequest = PageRequest.of(0, 10);
        productPage = new PageImpl<>(List.of(productDto), pageRequest, 1);
    }

    @Test
    @DisplayName("GET /api/v1/product/{id}: Получение продукта по ID")
    void getProductById() throws Exception {

        when(productService.getProductById(id)).thenReturn(productDto);

        mockMvc.perform(get("/api/v1/product/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(productDto)));
    }

    @Test
    @DisplayName("GET /api/v1/product/search: Поиск товаров по названию")
    void search() throws Exception {

        String query = "test";

        when(productService.search(eq(query), any(Pageable.class))).thenReturn(productPage);

        mockMvc.perform(get("/api/v1/product/search")
                        .param("query", "test")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(productPage)));

        verify(productService, times(1)).search(eq(query), any(Pageable.class));

    }

    @Test
    @DisplayName("GET /api/v1/product: Получение всех товаров")
    void getAllProducts() throws Exception {

        PagedModel<ProductDTO> productDTOPagedModel = new PagedModel<>(productPage);

        when(productService.findAllProducts(any(Pageable.class))).thenReturn(productPage);

        mockMvc.perform(get("/api/v1/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(productDTOPagedModel)));

        verify(productService, times(1)).findAllProducts(any(Pageable.class));

    }

    @Test
    @DisplayName("GET /api/v1/product/category: Получение всех категорий")
    void getAllCategories() throws Exception {


        CategoryDTO categoryDTO = TestDataFactory.createCategoryDTO();
        Page<CategoryDTO> categoryPage = new PageImpl<>(List.of(categoryDTO), pageRequest, 1);

        when(productService.findAllCategories(any(Pageable.class))).thenReturn(categoryPage);

        mockMvc.perform(get("/api/v1/product/category")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(categoryPage)));

        verify(productService, times(1)).findAllCategories(pageRequest);

    }

}