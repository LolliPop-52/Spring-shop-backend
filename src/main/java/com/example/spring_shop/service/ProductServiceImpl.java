package com.example.spring_shop.service;

import com.example.spring_shop.domain.Category;
import com.example.spring_shop.domain.Product;
import com.example.spring_shop.dto.CategoryDTO;
import com.example.spring_shop.dto.ProductDTO;
import com.example.spring_shop.mapper.ProductMapper;
import com.example.spring_shop.repository.CategoryRepository;
import com.example.spring_shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Override
    @Transactional
    public String addProduct(ProductDTO productDTO){

        Set<Category> categories = productDTO.getCategories().stream()
                .map(category -> categoryRepository.findCategoryByTitle(category.getTitle())
                        .orElseGet(() -> Category.builder()
                                    .title(category.getTitle())
                                    .build()))
                .collect(Collectors.toSet());

        Product product = Product
                .builder()
                .title(productDTO.getTitle())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .imageUrl(productDTO.getImageUrl())
                .categories(categories)
                .build();
        productRepository.save(product);
        return "product added";
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getPageOfProduct(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Product> productPage = productRepository.findAll(pageable);

        return productPage.map(productMapper::toDto);
    }

    @Override
    @Transactional
    public Page<ProductDTO> search(String query, Pageable pageable) {
        Page<Product> productPage = productRepository.searchProducts(query, pageable);

        // Превращаем Page<Product> в Page<ProductDTO> через твой Mapper
        return productPage.map(product -> productMapper.toDto(product));
    }

    @Override
    public boolean exists(String title, BigDecimal productPrice, String description) {
        return productRepository.existsByTitleAndPriceAndDescription(title, productPrice, description);
    }

    @Override
    public Page<ProductDTO> findAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(productMapper::toDto);
    }

    @Override
    public Page<CategoryDTO> findAllCategories(Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return categoryPage.map(category -> CategoryDTO.builder()
                .id(category.getId())
                .title(category.getTitle())
                .build());
    }


}
