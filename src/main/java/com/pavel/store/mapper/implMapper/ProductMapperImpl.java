package com.pavel.store.mapper.implMapper;

import com.pavel.store.dto.request.ProductCreateDto;
import com.pavel.store.dto.response.ProductResponseDto;
import com.pavel.store.entity.Product;
import com.pavel.store.entity.User;
import com.pavel.store.mapper.mapers.ProductMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductMapperImpl implements ProductMapper {


    @Override
    public ProductResponseDto toDto(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product  entity is null");
        }

        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .categoryName(product.getCategory().getName())
                .description(product.getDescription())
                .amount(product.getAmount())
                .article(product.getArticle())
                .imageUrl(product.getImageUrl())
                .build();
    }

    @Override
    public Product toEntity(ProductCreateDto productCreateDto) {

        if (productCreateDto == null) {
            throw new IllegalArgumentException("ProductDto is null");
        }

        return Product.builder()
                .id(productCreateDto.getId())
                .name(productCreateDto.getName())
                .price(productCreateDto.getPrice())
                .description(productCreateDto.getDescription())
                .amount(productCreateDto.getAmount())
                .article(productCreateDto.getArticle())
                .imageUrl(productCreateDto.getImageUrl())
                .build();
    }

    @Override
    public void updateEntity(ProductCreateDto productCreateDto, Product product) {

    }

}
