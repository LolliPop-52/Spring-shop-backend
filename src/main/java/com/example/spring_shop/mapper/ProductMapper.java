package com.example.spring_shop.mapper;

import com.example.spring_shop.domain.BucketItem;
import com.example.spring_shop.domain.Product;
import com.example.spring_shop.dto.CategoryDTO;
import com.example.spring_shop.dto.ProductDTO;
import com.example.spring_shop.dto.SmallProductDTO;
import com.example.spring_shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    public ProductDTO toDto(Product product){
        if(product == null)
            return null;
        return ProductDTO.builder()
                .id(product.getId())
                .price(product.getPrice())
                .title(product.getTitle())
                .categories(product.getCategories().stream()
                        .map(category -> new CategoryDTO(category.getId(), category.getTitle()))
                        .collect(Collectors.toSet()))
                .imageUrl(product.getImageUrl())
                .description(product.getDescription())
                .build();
    }

    public SmallProductDTO toSmallDto(Product product){
        if(product == null)
            return null;
        return SmallProductDTO.builder()
                .id(product.getId())
                .price(product.getPrice())
                .title(product.getTitle())
                .imageUrl(product.getImageUrl())
                .build();
    }

}
