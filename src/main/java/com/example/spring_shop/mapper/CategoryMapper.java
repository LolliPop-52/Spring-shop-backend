package com.example.spring_shop.mapper;


import com.example.spring_shop.domain.Category;
import com.example.spring_shop.dto.CategoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    public CategoryDTO toDTO(Category category){
        return CategoryDTO.builder()
                .id(category.getId())
                .title(category.getTitle())
                .build();
    }

}
