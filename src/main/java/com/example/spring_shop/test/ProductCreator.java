package com.example.spring_shop.test;

import com.example.spring_shop.domain.Product;
import com.example.spring_shop.dto.CategoryDTO;
import com.example.spring_shop.dto.ProductDTO;
import com.example.spring_shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ProductCreator {
    private final ProductService productService;

    public String add(String title, String price, String description, Set<String> categories, String imageUrl){

        BigDecimal productPrice = new BigDecimal(price);

        if (productService.exists(title, productPrice, description)) {
            return "Product '" + title + "' already exists, skipping...";
        }
        Set<CategoryDTO> categoriesDTO = categories.stream()
                        .map(category -> CategoryDTO.builder().title(category).build())
                                .collect(Collectors.toSet());

        productService.addProduct(
                ProductDTO.builder()
                        .title(title)
                        .price(new BigDecimal(price))
                        .description(description)
                        .categories(categoriesDTO)
                        .imageUrl(imageUrl)
                        .build()
        );

        return "product added";
    }
}
