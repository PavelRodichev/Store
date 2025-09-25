package com.pavel.store.mapper.implMapper;

import com.pavel.store.handler.exeption.EntityNotFoundException;
import com.pavel.store.dto.request.ProductCreateDto;
import com.pavel.store.dto.request.ProductUpdateDto;
import com.pavel.store.dto.response.ProductResponseDto;
import com.pavel.store.entity.Category;
import com.pavel.store.entity.Product;
import com.pavel.store.mapper.mapers.ProductMapper;
import com.pavel.store.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapperImpl implements ProductMapper {


    private final CategoryRepository categoryRepository;

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
        Category category = categoryRepository.findByName(productCreateDto.getCategoryName()).orElseThrow(() -> new EntityNotFoundException("Product"));
        return Product.builder()
                .name(productCreateDto.getName())
                .price(productCreateDto.getPrice())
                .description(productCreateDto.getDescription())
                .amount(productCreateDto.getAmount())
                .article(productCreateDto.getArticle())
                .imageUrl(productCreateDto.getImageUrl())
                .category(category)
                .build();
    }

    @Override
    public void updateEntity(ProductUpdateDto UpdateDto, Product product) {
        if (product == null) {
            throw new IllegalArgumentException("ProductDto is null");
        }

        System.out.println();

        Category category = categoryRepository
                .findByName(UpdateDto.getCategoryName())
                .orElseThrow(() -> new EntityNotFoundException("Category"));


        System.out.println();

        product.setName(UpdateDto.getName());
        product.setArticle(UpdateDto.getArticle());
        product.setPrice(UpdateDto.getPrice());
        product.setImageUrl(UpdateDto.getImageUrl());
        product.setCategory(category);
        product.setDescription(UpdateDto.getDescription());
        product.setAmount(UpdateDto.getAmount());


    }

}
