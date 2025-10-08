package com.pavel.store.controller.rest;

import com.pavel.store.dto.request.CategoryRequestDto;
import com.pavel.store.dto.response.CategoryResponseDto;
import com.pavel.store.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category API", description = "Операции с категориями")
public class CategoryController {

    private final CategoryService categoryService;


    @GetMapping()
    public ResponseEntity<Page<CategoryResponseDto>> getAllCategory(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam(defaultValue = "name") String sort) {
        var sortBy = Sort.by(sort);
        Pageable pageable = PageRequest.of(page, size, sortBy);
        return ResponseEntity.ok(categoryService.getAll(pageable));
    }


    @GetMapping("/{name}")
    public ResponseEntity<CategoryResponseDto> getCategoryByName(@PathVariable String name) {
        return ResponseEntity.ok(categoryService.getByName(name));
    }


    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody CategoryRequestDto categoryRequestDto) {

        return ResponseEntity.ok(categoryService.save(categoryRequestDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(@RequestBody CategoryRequestDto categoryRequestDto, @PathVariable Long id) {

        return ResponseEntity.ok(categoryService.update(categoryRequestDto, id));

    }


}
