package com.pavel.store.service;


import com.pavel.store.dto.request.ProductCreateDto;
import com.pavel.store.dto.request.ProductUpdateDto;
import com.pavel.store.dto.response.ProductResponseDto;
import com.pavel.store.entity.Product;
import com.pavel.store.mapper.implMapper.ProductMapperImpl;
import com.pavel.store.repository.ProductRepository;
import com.pavel.store.handler.exeption.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapperImpl productMapper;
    private final ImageService imageService;

    public Page<ProductResponseDto> getAllProduct(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(productMapper::toDto);
    }


    public ProductResponseDto getProductById(Long id) {
        return productRepository.findById(id).map(productMapper::toDto).orElseThrow(() -> new EntityNotFoundException("Product", id));
    }

    public Product findProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product", id));
    }

    public ProductResponseDto getProductByName(String name) {
        return productRepository.findByName(name).map(productMapper::toDto).orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    public Optional<byte[]> getImage(Long id) {
        return productRepository.findById(id)
                .map(Product::getImage)
                .filter(StringUtils::hasText)
                .flatMap(imageService::get);
    }

    @Transactional

    public ProductResponseDto saveProduct(ProductCreateDto productDto) {
        return Optional.of(productDto).map(dto -> {
            if (dto.getImage() != null) {
                uploadImage(dto.getImage());
            }
            return productMapper.toEntity(productDto);
        }).map(productRepository::save).map(productMapper::toDto).orElseThrow(() -> new RuntimeException("Product not created"));
    }

    @SneakyThrows
    private void uploadImage(MultipartFile image) {
        if (!image.isEmpty()) {
            imageService.upload(image.getOriginalFilename(), image.getInputStream());
        }


    }

    @Transactional
    public void deleteProductById(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Product", id);
        }

    }

    @Transactional
    public ProductResponseDto updateProduct(ProductUpdateDto updateDto, Long id) {

        return productRepository.findById(id)
                .map(product -> {
                    if (updateDto.getImage() != null) {
                        uploadImage(updateDto.getImage());
                    }
                    productMapper.updateEntity(updateDto, product);
                    return productRepository.save(product);
                }).map(productMapper::toDto).orElseThrow();
    }

    @Transactional
    public void increaseAllPrices(BigDecimal percent) {
        log.info("Increasing all product prices by {}%", percent);
        if (percent == null) {
            throw new IllegalArgumentException("Percentage cannot be null");
        }
        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            log.info("No products found for price update");
            return;
        }
        if (percent.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Percentage must be positive: " + percent);
        }

        BigDecimal multiplier = BigDecimal.ONE.add(percent.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));

        productRepository.updateAllPrices(multiplier);

        log.info("the price of the products has been changed");


    }
}
