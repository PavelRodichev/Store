package com.pavel.store.mapper.mapers;

import com.pavel.store.dto.request.CategoryRequestDto;
import com.pavel.store.dto.response.CategoryResponseDto;
import com.pavel.store.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import java.lang.annotation.Target;

@Mapper(componentModel = "spring")
@Component
public interface CategoryMapper {


    CategoryResponseDto toDto(Category category);

    Category toEntity(CategoryRequestDto categoryRequestDto);

    @Mapping(target = "name")
    void updateEntity(CategoryRequestDto categoryRequestDto, @MappingTarget Category category);
}

