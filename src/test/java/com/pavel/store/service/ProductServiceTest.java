package com.pavel.store.service;

import com.pavel.store.dto.request.ProductCreateDto;
import com.pavel.store.dto.request.ProductUpdateDto;
import com.pavel.store.dto.response.ProductResponseDto;
import com.pavel.store.entity.Product;
import com.pavel.store.handler.exeption.EntityNotFoundException;
import com.pavel.store.mapper.implMapper.ProductMapperImpl;
import com.pavel.store.repository.ProductRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapperImpl productMapper;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ProductService productService;

    private Product createProduct(Long id, String name, BigDecimal price) {
        return Product.builder()
                .id(id)
                .name(name)
                .price(price)
                .description("Description " + name)
                .build();
    }

    private ProductResponseDto createProductResponseDto(Long id, String name, BigDecimal price) {
        return ProductResponseDto.builder()
                .id(id)
                .name(name)
                .price(price)
                .description("Description " + name)
                .build();
    }

    private ProductCreateDto createProductCreateDto(String name, BigDecimal price) {
        return ProductCreateDto.builder()
                .name(name)
                .price(price)
                .description("Description " + name)
                .build();
    }

    private ProductUpdateDto createProductUpdateDto(String name, BigDecimal price) {
        return ProductUpdateDto.builder()
                .name(name)
                .price(price)
                .description("Updated " + name)
                .build();
    }

    @Test
    @DisplayName("Should return page of products")
    void getAllProduct_ShouldReturnPageOfProducts() {

        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = List.of(
                createProduct(1L, "Product 1", new BigDecimal("100.00")),
                createProduct(2L, "Product 2", new BigDecimal("200.00"))
        );
        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        List<ProductResponseDto> productDtos = List.of(
                createProductResponseDto(1L, "Product 1", new BigDecimal("100.00")),
                createProductResponseDto(2L, "Product 2", new BigDecimal("200.00"))
        );
        Page<ProductResponseDto> expectedPage = new PageImpl<>(productDtos, pageable, products.size());

        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(productMapper.toDto(any(Product.class)))
                .thenReturn(productDtos.get(0))
                .thenReturn(productDtos.get(1));


        Page<ProductResponseDto> result = productService.getAllProduct(pageable);


        assertThat(result).isEqualTo(expectedPage);
        verify(productRepository).findAll(pageable);
        verify(productMapper, times(2)).toDto(any(Product.class));
    }

    @Test
    @DisplayName("Should return product when exists")
    void getProductById_WhenProductExists_ShouldReturnProduct() {

        Long productId = 1L;
        Product product = createProduct(productId, "Test Product", new BigDecimal("99.99"));
        ProductResponseDto expectedDto = createProductResponseDto(productId, "Test Product", new BigDecimal("99.99"));

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(expectedDto);

        ProductResponseDto result = productService.getProductById(productId);


        assertThat(result).isEqualTo(expectedDto);
        verify(productRepository).findById(productId);
        verify(productMapper).toDto(product);
    }

    @Test
    @DisplayName("Should throw exception when product not found by ID")
    void getProductById_WhenProductNotExists_ShouldThrowException() {
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> productService.getProductById(productId));

        verify(productRepository).findById(productId);
        verifyNoInteractions(productMapper);
    }

    @Test
    @DisplayName("Should return product entity when exists")
    void findProductById_WhenProductExists_ShouldReturnProduct() {
        Long productId = 1L;
        Product expectedProduct = createProduct(productId, "Test Product", new BigDecimal("99.99"));

        when(productRepository.findById(productId)).thenReturn(Optional.of(expectedProduct));


        Product result = productService.findProductById(productId);

        assertThat(result).isEqualTo(expectedProduct);
        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should throw exception when product entity not found")
    void findProductById_WhenProductNotExists_ShouldThrowException() {
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> productService.findProductById(productId));

        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("Should return product by name when exists")
    void getProductByName_WhenProductExists_ShouldReturnProduct() {
        String productName = "Test Product";
        Product product = createProduct(1L, productName, new BigDecimal("99.99"));
        ProductResponseDto expectedDto = createProductResponseDto(1L, productName, new BigDecimal("99.99"));

        when(productRepository.findByName(productName)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(expectedDto);

        ProductResponseDto result = productService.getProductByName(productName);

        assertThat(result).isEqualTo(expectedDto);
        verify(productRepository).findByName(productName);
        verify(productMapper).toDto(product);
    }

    @Test
    @DisplayName("Should throw exception when product not found by name")
    void getProductByName_WhenProductNotExists_ShouldThrowException() {

        String productName = "Non-existent Product";
        when(productRepository.findByName(productName)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> productService.getProductByName(productName));

        verify(productRepository).findByName(productName);
        verifyNoInteractions(productMapper);
    }

    @Test
    @DisplayName("Should return image when product has image")
    void getImage_WhenProductHasImage_ShouldReturnImage() {

        Long productId = 1L;
        Product product = createProduct(productId, "Test Product", new BigDecimal("99.99"));
        product.setImage("image.jpg");
        byte[] imageData = "image data".getBytes();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(imageService.get("image.jpg")).thenReturn(Optional.of(imageData));


        Optional<byte[]> result = productService.getImage(productId);

        assertThat(result).isPresent().contains(imageData);
        verify(productRepository).findById(productId);
        verify(imageService).get("image.jpg");
    }

    @Test
    @DisplayName("Should return empty when product has no image")
    void getImage_WhenProductHasNoImage_ShouldReturnEmpty() {

        Long productId = 1L;
        Product product = createProduct(productId, "Test Product", new BigDecimal("99.99"));


        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Optional<byte[]> result = productService.getImage(productId);

        assertThat(result).isEmpty();
        verify(productRepository).findById(productId);
        verifyNoInteractions(imageService);
    }

    @Test
    @DisplayName("Should return empty when product not found")
    void getImage_WhenProductNotExists_ShouldReturnEmpty() {

        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());


        Optional<byte[]> result = productService.getImage(productId);


        assertThat(result).isEmpty();
        verify(productRepository).findById(productId);
        verifyNoInteractions(imageService);
    }

    @Test
    @DisplayName("Should save product without image")
    void saveProduct_WithoutImage_ShouldSaveProduct() {

        ProductCreateDto createDto = createProductCreateDto("New Product", new BigDecimal("150.00"));
        Product productEntity = createProduct(null, "New Product", new BigDecimal("150.00"));
        Product savedProduct = createProduct(1L, "New Product", new BigDecimal("150.00"));
        ProductResponseDto expectedDto = createProductResponseDto(1L, "New Product", new BigDecimal("150.00"));

        when(productMapper.toEntity(createDto)).thenReturn(productEntity);
        when(productRepository.save(productEntity)).thenReturn(savedProduct);
        when(productMapper.toDto(savedProduct)).thenReturn(expectedDto);


        ProductResponseDto result = productService.saveProduct(createDto);


        assertThat(result).isEqualTo(expectedDto);
        verify(productMapper).toEntity(createDto);
        verify(productRepository).save(productEntity);
        verify(productMapper).toDto(savedProduct);
        verifyNoInteractions(imageService);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should save product with image")
    void saveProduct_WithImage_ShouldSaveProductAndUploadImage() {

        MultipartFile image = mock(MultipartFile.class);
        ProductCreateDto createDto = ProductCreateDto.builder()
                .name("New Product")
                .price(new BigDecimal("150.00"))
                .image(image)
                .build();

        Product productEntity = createProduct(null, "New Product", new BigDecimal("150.00"));
        Product savedProduct = createProduct(1L, "New Product", new BigDecimal("150.00"));
        ProductResponseDto expectedDto = createProductResponseDto(1L, "New Product", new BigDecimal("150.00"));

        when(image.isEmpty()).thenReturn(false);
        when(image.getOriginalFilename()).thenReturn("product.jpg");
        when(image.getInputStream()).thenReturn(new ByteArrayInputStream("image data".getBytes()));
        when(productMapper.toEntity(createDto)).thenReturn(productEntity);
        when(productRepository.save(productEntity)).thenReturn(savedProduct);
        when(productMapper.toDto(savedProduct)).thenReturn(expectedDto);


        ProductResponseDto result = productService.saveProduct(createDto);


        assertThat(result).isEqualTo(expectedDto);
        verify(imageService).upload(eq("product.jpg"), any(InputStream.class));
        verify(productMapper).toEntity(createDto);
        verify(productRepository).save(productEntity);
        verify(productMapper).toDto(savedProduct);
    }

    @Test
    @DisplayName("Should delete product when exists")
    void deleteProductById_WhenProductExists_ShouldDeleteProduct() {

        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(true);


        productService.deleteProductById(productId);


        verify(productRepository).existsById(productId);
        verify(productRepository).deleteById(productId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void deleteProductById_WhenProductNotExists_ShouldThrowException() {

        Long productId = 999L;
        when(productRepository.existsById(productId)).thenReturn(false);


        assertThrows(EntityNotFoundException.class,
                () -> productService.deleteProductById(productId));

        verify(productRepository).existsById(productId);
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should update product without image")
    void updateProduct_WithoutImage_ShouldUpdateProduct() {
        // Arrange
        Long productId = 1L;
        ProductUpdateDto updateDto = createProductUpdateDto("Updated Product", new BigDecimal("200.00"));
        Product existingProduct = createProduct(productId, "Old Product", new BigDecimal("100.00"));
        Product updatedProduct = createProduct(productId, "Updated Product", new BigDecimal("200.00"));
        ProductResponseDto expectedDto = createProductResponseDto(productId, "Updated Product", new BigDecimal("200.00"));

        when(productRepository.findByIdForUpdate(productId)).thenReturn(Optional.of(existingProduct));
        doNothing().when(productMapper).updateEntity(updateDto, existingProduct);
        when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
        when(productMapper.toDto(updatedProduct)).thenReturn(expectedDto);


        ProductResponseDto result = productService.updateProduct(updateDto, productId);


        assertThat(result).isEqualTo(expectedDto);
        verify(productRepository).findByIdForUpdate(productId);
        verify(productMapper).updateEntity(updateDto, existingProduct);
        verify(productRepository).save(existingProduct);
        verify(productMapper).toDto(updatedProduct);
        verifyNoInteractions(imageService);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should update product with image")
    void updateProduct_WithImage_ShouldUpdateProductAndUploadImage() {
        // Arrange
        Long productId = 1L;
        MultipartFile image = mock(MultipartFile.class);
        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .name("Updated Product")
                .price(new BigDecimal("200.00"))
                .image(image)
                .build();

        Product existingProduct = createProduct(productId, "Old Product", new BigDecimal("100.00"));
        Product updatedProduct = createProduct(productId, "Updated Product", new BigDecimal("200.00"));
        ProductResponseDto expectedDto = createProductResponseDto(productId, "Updated Product", new BigDecimal("200.00"));

        when(image.isEmpty()).thenReturn(false);
        when(image.getOriginalFilename()).thenReturn("updated.jpg");
        when(image.getInputStream()).thenReturn(new ByteArrayInputStream("image data".getBytes()));
        when(productRepository.findByIdForUpdate(productId)).thenReturn(Optional.of(existingProduct));
        doNothing().when(productMapper).updateEntity(updateDto, existingProduct);
        when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
        when(productMapper.toDto(updatedProduct)).thenReturn(expectedDto);


        ProductResponseDto result = productService.updateProduct(updateDto, productId);


        assertThat(result).isEqualTo(expectedDto);
        verify(imageService).upload(eq("updated.jpg"), any(InputStream.class));
        verify(productMapper).updateEntity(updateDto, existingProduct);
        verify(productRepository).save(existingProduct);
    }

    @Test
    @DisplayName("Should increase all prices with valid percentage")
    void increaseAllPrices_WithValidPercentage_ShouldUpdatePrices() {

        BigDecimal percent = new BigDecimal("10.00");
        BigDecimal multiplier = new BigDecimal("1.1000");
        List<Product> products = List.of(
                createProduct(1L, "Product 1", new BigDecimal("100.00")),
                createProduct(2L, "Product 2", new BigDecimal("200.00"))
        );
        when(productRepository.findAllWithPessimisticWrite()).thenReturn(products);

        productService.increaseAllPrices(percent);


        verify(productRepository).updateAllPrices(multiplier);
    }

    @Test
    @DisplayName("Should throw exception when percentage is null")
    void increaseAllPrices_WhenPercentageIsNull_ShouldThrowException() {

        assertThrows(IllegalArgumentException.class,
                () -> productService.increaseAllPrices(null));

        verifyNoInteractions(productRepository);
    }

    @Test
    @DisplayName("Should throw exception when percentage is zero or negative")
    void increaseAllPrices_WhenPercentageIsZero_ShouldThrowException() {
        BigDecimal zero = BigDecimal.ZERO;



        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.increaseAllPrices(zero)
        );

        assertThat(exception.getMessage())
                .contains("Percentage must be positive")
                .contains("0");

        // Проверяем что до репозитория не дошли
        verify(productRepository, never()).findAll();
        verify(productRepository, never()).updateAllPrices(any());
    }

    @Test
    @DisplayName("Should handle empty product list gracefully")
    void increaseAllPrices_WhenNoProducts_ShouldNotUpdate() {

        BigDecimal percent = new BigDecimal("10.00");


        when(productRepository.findAllWithPessimisticWrite()).thenReturn(Collections.EMPTY_LIST);


        productService.increaseAllPrices(percent);


        verify(productRepository, never()).updateAllPrices(any(BigDecimal.class));
    }
}

