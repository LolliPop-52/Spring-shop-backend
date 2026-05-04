package com.example.spring_shop.service;

import com.example.spring_shop.dto.CategoryDTO;
import com.example.spring_shop.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {
    ProductDTO getProductById(Long id);

    Page<ProductDTO> getPageOfProduct(int page, int size);

    String addProduct(ProductDTO productDTO);

    Page<ProductDTO> search(String query, Pageable pageable);

    boolean exists(String title, BigDecimal productPrice, String description);

    Page<ProductDTO> findAllProducts(Pageable pageable);
    Page<CategoryDTO> findAllCategories(Pageable pageable);
}
