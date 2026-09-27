package com.example.spring_shop.service.impl;

import com.example.spring_shop.domain.Category;
import com.example.spring_shop.domain.Product;
import com.example.spring_shop.dto.CategoryDTO;
import com.example.spring_shop.dto.ProductDTO;
import com.example.spring_shop.exception_handler.ResourceNotFoundException;
import com.example.spring_shop.fixture.TestDataFactory;
import com.example.spring_shop.mapper.ProductMapper;
import com.example.spring_shop.repository.CategoryRepository;
import com.example.spring_shop.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.module.ResolutionException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class ProductServiceImplTest {


    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductDTO productDTO;
    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        productDTO = TestDataFactory.createProductDTO();
        category = TestDataFactory.createCategory();
        product = TestDataFactory.createProduct();
    }

    @Test
    @DisplayName("addProduct: Успешное добавление товара с обработкой существующих и новых категорий")
    void addProduct_Success() {

        when(categoryRepository.findCategoryByTitle(anyString()))
                .thenAnswer( invocation -> {
                    String title = invocation.getArgument(0);

                    if(title.length() % 2 == 0){
                        return Optional.of(Category.builder().id(1L).title(title).build());
                    } else {
                        return Optional.empty();
                    }
                });

        CategoryDTO existingCategory = CategoryDTO.builder().id(1L).title("Карандаш").build();
        CategoryDTO nonExistentCategory = CategoryDTO.builder().id(2L).title("Ручка").build();
        Set<CategoryDTO> categoryDTOS = Set.of(existingCategory, nonExistentCategory);

        productDTO.setCategories(categoryDTOS);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

        productService.addProduct(productDTO);

        verify(productRepository, times(1)).save(captor.capture());
        Product newProduct = captor.getValue();

        assertThat(newProduct.getCategories())
                .hasSize(2)
                .extracting(Category::getId, Category::getTitle)
                .containsExactlyInAnyOrder(
                        tuple(1L, "Карандаш"),
                        tuple(null, "Ручка")
                );
    }

    @Test
    @DisplayName("getProductById: Успешное получение DTO товара по ID")
    void getProductById_Success() {

        Long id = TestDataFactory.DEFAULT_ID;
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(productDTO);

        ProductDTO curProductDTO = productService.getProductById(id);

        assertEquals(productDTO, curProductDTO);
        verify(productRepository, times(1)).findById(id);
        verify(productMapper, times(1)).toDto(product);
    }

    @Test
    @DisplayName("getProductById: Товар не найден, выброс ResourceNotFoundException")
    void getProductById_ProductNotFound_ThrowException() {

        Long id = TestDataFactory.DEFAULT_ID;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource with id: " + id + " not found");

        verify(productMapper, never()).toDto(product);

    }

    @Test
    @DisplayName("getProductEntityById: Успешное получение сущности товара по ID")
    void getProductEntityById_Success() {
        Long id = TestDataFactory.DEFAULT_ID;
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        Product result = productService.getProductEntityById(id);

        assertEquals(product, result);
        verify(productRepository, times(1)).findById(id);
        verifyNoInteractions(productMapper);
    }

    @Test
    @DisplayName("getProductEntityById: Товар не найден по ID, выброс ResourceNotFoundException")
    void getProductEntityById_ProductNotFound_ThrowsException() {
        Long id = TestDataFactory.DEFAULT_ID;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductEntityById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource with id: " + id + " not found");

        verify(productRepository, times(1)).findById(id);
        verifyNoInteractions(productMapper);
    }

    @Test
    @DisplayName("search: Успешный поиск товаров по запросу с пагинацией")
    void search_Success() {
        String query = "laptop";
        Pageable pageRequest = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.searchProducts(query, pageRequest)).thenReturn(productPage);
        when(productMapper.toDto(product)).thenReturn(productDTO);

        Page<ProductDTO> resultPage = productService.search(query, pageRequest);

        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(productDTO, resultPage.getContent().getFirst());

        verify(productRepository, times(1)).searchProducts(query, pageRequest);
        verify(productMapper, times(1)).toDto(product);
    }

    @Test
    @DisplayName("exists: Возврат true, если товар с заданными параметрами существует")
    void exists_ReturnsTrue() {
        String title = "Phone";
        BigDecimal price = BigDecimal.valueOf(999.99);
        String description = "Smartphone";

        when(productRepository.existsByTitleAndPriceAndDescription(title, price, description)).thenReturn(true);

        boolean exists = productService.exists(title, price, description);

        assertTrue(exists);
        verify(productRepository, times(1)).existsByTitleAndPriceAndDescription(title, price, description);
    }

    @Test
    @DisplayName("exists: Возврат false, если товар с заданными параметрами не найден")
    void exists_ReturnsFalse() {
        String title = "Phone";
        BigDecimal price = BigDecimal.valueOf(999.99);
        String description = "Smartphone";

        when(productRepository.existsByTitleAndPriceAndDescription(title, price, description)).thenReturn(false);

        boolean exists = productService.exists(title, price, description);

        assertFalse(exists);
        verify(productRepository, times(1)).existsByTitleAndPriceAndDescription(title, price, description);
    }


    @Test
    @DisplayName("findAllProducts: Успешное получение страницы со списком товаров")
    void findAllProducts_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Product product2 = TestDataFactory.createProduct().toBuilder()
                .id(2L)
                .title("TestProduct2")
                .build();
        ProductDTO productDTO2 = TestDataFactory.createProductDTO().toBuilder()
                .id(2L)
                .title("TestProduct2")
                .build();


        Page<Product> productPage = new PageImpl<>(List.of(product, product2));

        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(productMapper.toDto(product)).thenReturn(productDTO);
        when(productMapper.toDto(product2)).thenReturn(productDTO2);

        Page<ProductDTO> resultPage = productService.findAllProducts(pageable);

        assertNotNull(resultPage);
        assertEquals(2, resultPage.getTotalElements());
        assertEquals(productDTO, resultPage.getContent().getFirst());
        assertEquals(productDTO2, resultPage.getContent().getLast());

        verify(productRepository, times(1)).findAll(pageable);
        verify(productMapper, times(1)).toDto(product);
    }


    @Test
    @DisplayName("findAllCategories: Успешное получение страницы со списком категорий")
    void findAllCategories_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Category category2 = TestDataFactory.createCategory().toBuilder()
                .id(2L)
                .title("Category2")
                .build();

        Page<Category> categoryPage = new PageImpl<>(List.of(category, category2));

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        Page<CategoryDTO> resultPage = productService.findAllCategories(pageable);

        assertNotNull(resultPage);
        assertEquals(2, resultPage.getTotalElements());

        CategoryDTO resultCategoryDTO = resultPage.getContent().getFirst();
        CategoryDTO resultCategoryDTO2 = resultPage.getContent().getLast();
        assertEquals(category.getId(), resultCategoryDTO.getId());
        assertEquals(category.getTitle(), resultCategoryDTO.getTitle());
        assertEquals(category2.getId(), resultCategoryDTO2.getId());
        assertEquals(category2.getTitle(), resultCategoryDTO2.getTitle());

        verify(categoryRepository, times(1)).findAll(pageable);
    }
}