package com.pavel.store.controller.rest;


import com.pavel.store.dto.request.ProductFilterDto;
import com.pavel.store.dto.response.PageResponse;
import com.pavel.store.dto.response.ProductResponseDto;
import com.pavel.store.entity.Product;
import com.pavel.store.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product API", description = "Операции с продуктами")
public class ProductController {

    private final ProductService productService;


    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAllProduct(
            @PageableDefault(size = 20) Pageable pageable
            , @RequestHeader(value = "X-Currency", required = false) String currency) {
        Page<ProductResponseDto> result = productService.getAllProduct(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDto>> getFilterProduct(
            ProductFilterDto productFilterDto,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ProductResponseDto> productsList = productService.getProductsWithFilter(productFilterDto, pageable);
        return ResponseEntity.ok(productsList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(
            @PathVariable Long id) {

        return ResponseEntity.ok(productService.getProductById(id));
    }


    @GetMapping("/{id}/image")
    private byte[] findImage(@PathVariable Long id) {
        return productService.getImage(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


}
