package com.pavel.store.mapper.mapers;

import com.pavel.store.dto.request.ProductCreateDto;
import com.pavel.store.dto.request.ProductUpdateDto;
import com.pavel.store.dto.response.ProductResponseDto;
import com.pavel.store.entity.Product;
import com.pavel.store.entity.User;

public interface ProductMapper {
    ProductResponseDto toDto(Product product);

    Product toEntity(ProductCreateDto registrationDto);

    void updateEntity(ProductUpdateDto productUpdateDto, Product product);

}
