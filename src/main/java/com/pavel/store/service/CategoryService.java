package com.pavel.store.service;

import com.pavel.store.dto.request.CategoryRequestDto;
import com.pavel.store.handler.exeption.EntityNotFoundException;
import com.pavel.store.dto.response.CategoryResponseDto;
import com.pavel.store.entity.Category;
import com.pavel.store.mapper.mapers.CategoryMapper;
import com.pavel.store.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public Page<CategoryResponseDto> getAll(Pageable pageable) {

        Page<Category> pageCategory = categoryRepository.findAll(pageable);

        return pageCategory.map(categoryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public CategoryResponseDto getById(Long id) {
        return categoryRepository.findById(id).map(categoryMapper::toDto).orElseThrow(() -> new EntityNotFoundException("Category", id));
    }

    @Transactional(readOnly = true)
    public CategoryResponseDto getByName(String name) {
        return categoryRepository.findByName(name).map(categoryMapper::toDto).orElseThrow(() -> new EntityNotFoundException(name));
    }


    @Transactional
    public CategoryResponseDto save(CategoryRequestDto category) {
        Category save = categoryMapper.toEntity(category);
        return categoryMapper.toDto(categoryRepository.save(save));
    }

    @Transactional
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Transactional
    public CategoryResponseDto update(CategoryRequestDto categoryRequestDto, Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category", id));

        categoryMapper.updateEntity(categoryRequestDto, category);

        return categoryMapper.toDto(category);
    }
}
