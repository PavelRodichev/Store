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
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class ProductMapperImpl implements ProductMapper {


    private final CategoryRepository categoryRepository;

    @Override
    public ProductResponseDto toDto(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product  entity is null");
        }

        ProductResponseDto productResponseDto = ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .amount(product.getAmount())
                .article(product.getArticle())
                .image(product.getImage())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .available(product.getAvailable())
                .build();
        // Безопасное извлечение категории
        if (product.getCategory() != null) {
            productResponseDto.setCategoryName(product.getCategory().getName());
        }
        return productResponseDto;
    }

    @Override
    public Product toEntity(ProductCreateDto productCreateDto) {

        if (productCreateDto == null) {
            throw new IllegalArgumentException("ProductDto is null");
        }
        Category category = categoryRepository.findByName(productCreateDto.getCategoryName()).orElseThrow(() -> new EntityNotFoundException("Product"));

        Product newProduct = Product.builder()
                .name(productCreateDto.getName())
                .price(productCreateDto.getPrice())
                .description(productCreateDto.getDescription())
                .amount(productCreateDto.getAmount())
                .article(productCreateDto.getArticle())
                .category(category)
                .build();

        Optional.ofNullable(productCreateDto.getImage())
                .filter(Predicate.not(MultipartFile::isEmpty))
                .ifPresent(image -> newProduct.setImage(image.getOriginalFilename()));


        return newProduct;
    }


    @Override
    public void updateEntity(ProductUpdateDto updateDto, Product product) {
        if (product == null) {
            throw new IllegalArgumentException("ProductDto is null");
        }

        Category category = categoryRepository
                .findByName(updateDto.getCategoryName())
                .orElseThrow(() -> new EntityNotFoundException("Category"));

        product.setName(updateDto.getName());
        product.setArticle(updateDto.getArticle());
        product.setPrice(updateDto.getPrice());
        product.setCategory(category);
        product.setDescription(updateDto.getDescription());
        product.setAmount(updateDto.getAmount());

        Optional.ofNullable(updateDto.getImage())
                .filter(Predicate.not(MultipartFile::isEmpty))
                .ifPresent(image -> product.setImage(image.getOriginalFilename()));

    }
}
