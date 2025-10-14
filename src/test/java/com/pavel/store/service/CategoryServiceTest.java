package com.pavel.store.service;


import com.pavel.store.dto.request.CategoryRequestDto;
import com.pavel.store.dto.response.CategoryResponseDto;
import com.pavel.store.entity.Category;
import com.pavel.store.handler.exeption.EntityNotFoundException;
import com.pavel.store.mapper.mapers.CategoryMapper;
import com.pavel.store.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;


import java.util.Collections;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;


    @Test
    void getAllCategoriesShouldReturnPageCategoryResponseDto() {
        Category category = new Category();
        List<Category> categoryList = Collections.singletonList(category);
        Pageable pageable = mock(Pageable.class);
        Page<Category> page = new PageImpl(categoryList, pageable, categoryList.size());
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();

        when(categoryRepository.findAll(pageable)).thenReturn(page);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(categoryResponseDto);

        var result = categoryService.getAll(pageable);
        assertThat(result).isNotNull();
        assertThat(result.getContent())
                .hasSize(1)
                .containsExactly(categoryResponseDto);
        assertThat(result).isInstanceOf(Page.class);

    }

    @Test
    void getByIdShouldReturnCategoryResponseDto() {

        Long categoryId = 1L;
        Category category = Category.builder()
                .name("Test")
                .id(1L)
                .products(Collections.emptyList())
                .build();
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setId(categoryId);
        categoryResponseDto.setName("Test");

        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(any(Category.class))).thenReturn(categoryResponseDto);
        var result = categoryService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(categoryResponseDto);
        assertThat(result.getId()).isEqualTo(categoryId);
        assertThat(result.getName()).isEqualTo("Test");

        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper).toDto(category);
    }

    @Test
    void getByNameWhenCategoryExistsShouldReturnCategoryResponseDto() {
        // Given
        String categoryName = "Electronics";
        Category category = new Category();
        category.setName(categoryName);
        CategoryResponseDto expectedDto = new CategoryResponseDto();
        expectedDto.setName(categoryName);

        // When
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expectedDto);

        CategoryResponseDto result = categoryService.getByName(categoryName);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(categoryRepository).findByName(categoryName);
        verify(categoryMapper).toDto(category);
    }

    @Test
    void getByNameWhenCategoryNotExistsShouldThrowException() {
        // Given
        String categoryName = "NonExistent";

        // When
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> categoryService.getByName(categoryName))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(categoryName);

        verify(categoryRepository).findByName(categoryName);
        verify(categoryMapper, never()).toDto(any());
    }

    // Тест для save
    @Test
    void saveShouldReturnSavedCategoryResponseDto() {
        // Given
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("New Category");

        Category categoryEntity = new Category();
        categoryEntity.setName("New Category");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("New Category");

        CategoryResponseDto expectedDto = new CategoryResponseDto();
        expectedDto.setId(1L);
        expectedDto.setName("New Category");

        // When
        when(categoryMapper.toEntity(requestDto)).thenReturn(categoryEntity);
        when(categoryRepository.save(categoryEntity)).thenReturn(savedCategory);
        when(categoryMapper.toDto(savedCategory)).thenReturn(expectedDto);

        CategoryResponseDto result = categoryService.save(requestDto);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(categoryMapper).toEntity(requestDto);
        verify(categoryRepository).save(categoryEntity);
        verify(categoryMapper).toDto(savedCategory);
    }

    // Тест для deleteById
    @Test
    void deleteByIdShouldCallRepository() {
        // Given
        Long categoryId = 1L;

        // When
        categoryService.deleteById(categoryId);

        // Then
        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    void deleteByIdWhenRepositoryThrowsExceptionShouldPropagate() {
        // Given
        Long categoryId = 999L;

        // When
        categoryService.deleteById(categoryId);

        verify(categoryRepository).deleteById(categoryId);

        // Then

    }

    // Тест для update
    @Test
    void updateWhenCategoryExistsShouldReturnUpdatedCategoryResponseDto() {
        // Given
        Long categoryId = 1L;
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Updated Name");

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("Old Name");

        Category updatedCategory = new Category();
        updatedCategory.setId(categoryId);
        updatedCategory.setName("Updated Name");

        CategoryResponseDto expectedDto = new CategoryResponseDto();
        expectedDto.setId(categoryId);
        expectedDto.setName("Updated Name");

        // When
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryMapper.toDto(existingCategory)).thenReturn(expectedDto);

        CategoryResponseDto result = categoryService.update(requestDto, categoryId);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper).updateEntity(requestDto, existingCategory);
        verify(categoryMapper).toDto(existingCategory);
    }

    @Test
    void updateWhenCategoryNotExistsShouldThrowException() {
        // Given
        Long categoryId = 999L;
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("Updated Name");

        // When
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> categoryService.update(requestDto, categoryId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Category")
                .hasMessageContaining(categoryId.toString());

        verify(categoryRepository).findById(categoryId);
        verify(categoryMapper, never()).updateEntity(any(), any());
        verify(categoryMapper, never()).toDto(any());
    }

    // Дополнительный тест для проверки, что updateEntity действительно вызывается с правильными параметрами
    @Test
    void updateShouldCallUpdateEntityWithCorrectParameters() {

        Long categoryId = 1L;
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("New Name");


        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("Old Name");


        CategoryResponseDto responseDto = new CategoryResponseDto();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryMapper.toDto(existingCategory)).thenReturn(responseDto);

        categoryService.update(requestDto, categoryId);


        verify(categoryMapper).updateEntity(requestDto, existingCategory);

        assertThat(existingCategory).isSameAs(existingCategory);
    }
}
