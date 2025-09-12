package com.pavel.store.mapper.mapers;

import com.pavel.store.dto.response.CategoryResponseDto;
import com.pavel.store.entity.Category;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface CategoryMapper {


    CategoryResponseDto toDto(Category category);


    Category toEntity(CategoryResponseDto categoryResponseDto);

}

