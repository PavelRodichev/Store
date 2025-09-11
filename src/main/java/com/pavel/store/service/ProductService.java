package com.pavel.store.service;


import com.pavel.store.dto.request.ProductCreateDto;
import com.pavel.store.dto.response.ProductResponseDto;
import com.pavel.store.entity.Product;
import com.pavel.store.mapper.implMapper.ProductMapperImpl;
import com.pavel.store.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapperImpl productMapper;


    public Page<ProductResponseDto> getAllProduct(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(productMapper::toDto);
    }


    public ProductResponseDto getProductById(Long id) {
        return productRepository.findById(id).map(productMapper::toDto).orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }


    public ProductResponseDto getProductByName(String name) {
        return productRepository.findByName(name).map(productMapper::toDto).orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    @Transactional
    public Product saveProduct(ProductCreateDto productDto) {
        Product product = productMapper.toEntity(productDto);
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProductById(Long id) {
       productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product with that id not found"));
        productRepository.deleteById(id);
    }

}
