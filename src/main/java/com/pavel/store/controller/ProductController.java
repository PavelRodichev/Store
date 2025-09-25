package com.pavel.store.controller;


import com.pavel.store.dto.request.ProductCreateDto;
import com.pavel.store.dto.request.ProductUpdateDto;
import com.pavel.store.dto.response.ProductResponseDto;
import com.pavel.store.entity.Product;
import com.pavel.store.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product API", description = "Операции с продуктами")
public class ProductController {

    private final ProductService productService;


    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAllProduct(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size,
                                                                  @RequestParam(defaultValue = "name") String sort) {
        var sortBy = Sort.by(sort);
        Pageable pageable = PageRequest.of(page, size, sortBy);
        return ResponseEntity.ok(productService.getAllProduct(pageable));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getUserById(@PathVariable Long id) {

        return ResponseEntity.ok(productService.getProductById(id));
    }


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
    public ResponseEntity<ProductResponseDto> updateProduct(@RequestBody ProductUpdateDto productUpdateDto, @PathVariable Long id) {
        return ResponseEntity.ok(productService.updateProduct(productUpdateDto, id));
    }
}
