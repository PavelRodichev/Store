package com.pavel.store.controller.rest;


import com.pavel.store.dto.request.ProductCreateDto;
import com.pavel.store.dto.request.ProductUpdateDto;
import com.pavel.store.dto.response.ProductResponseDto;
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
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("/{id}/image")
    private byte[] findImage(@PathVariable Long id) {
        return productService.getImage(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


}
