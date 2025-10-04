package com.pavel.store.controller.rest;

import com.pavel.store.dto.request.ProductCreateDto;
import com.pavel.store.dto.request.ProductUpdateDto;
import com.pavel.store.dto.response.ProductResponseDto;
import com.pavel.store.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody @Valid ProductCreateDto productDto) {
        return ResponseEntity.ok(productService.saveProduct(productDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductById(@PathVariable Long id) {
        productService.deleteProductById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@RequestBody @Valid ProductUpdateDto productUpdateDto, @PathVariable Long id) {
        return ResponseEntity.ok(productService.updateProduct(productUpdateDto, id));
    }


}
