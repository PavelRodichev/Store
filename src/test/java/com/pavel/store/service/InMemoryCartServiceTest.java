package com.pavel.store.service;

import com.pavel.store.dto.response.CartDto;
import com.pavel.store.dto.response.CartItemDto;
import com.pavel.store.entity.Product;
import com.pavel.store.handler.exeption.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InMemoryCartServiceTest {

    @Mock
    private ProductService productService;
    @InjectMocks
    private InMemoryCartService inMemoryCartService;

    private Long userId = 1L;
    private Long productId = 1L;
    private int quantity = 2;

    @Test
    void getCartWhenCartIsEmptyShouldReturnEmptyCartDto() {
        CartDto result = inMemoryCartService.getCart(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEmpty();
        assertThat(result.getTotalPrice()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getTotalItems()).isEqualTo(0);
        verifyNoInteractions(productService);


    }

    @Test
    void getCartWithValidProductsShouldReturnCartDtoWithItems() {
        // Given
        ;
        Product product1 = Product.builder()
                .id(productId)
                .name("Product 1")
                .price(BigDecimal.valueOf(100.0))
                .build();

        when(productService.findProductById(productId)).thenReturn(product1);
        inMemoryCartService.addToCart(1L, productId, quantity);
        CartDto result = inMemoryCartService.getCart(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotalItems()).isEqualTo(2);
        assertThat(result.getTotalPrice()).isEqualTo(BigDecimal.valueOf(200.0));

        CartItemDto item = result.getItems().get(0);
        assertThat(item.getProductId()).isEqualTo(productId);
        assertThat(item.getProductName()).isEqualTo("Product 1");
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getProductPrice()).isEqualTo(BigDecimal.valueOf(100.0));
    }

    @Test
    void addToCartShouldBeReturnCartDto() {
        Product product1 = Product.builder()
                .id(productId)
                .name("Product 1")
                .price(BigDecimal.valueOf(100.0))
                .build();
        when(productService.findProductById(productId)).thenReturn(product1);

        var result = inMemoryCartService.addToCart(userId, productId, 2);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(CartDto.class);
    }

    @Test
    void addToCartShouldThrowExceptionIfProductIsNull() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> inMemoryCartService.addToCart(userId, productId, quantity));
    }

    @Test
    void removeFromCartShouldSuccessRemoveUserCart() {
        Long userId = 1L;
        Long productId = 100L;
        int quantity = 2;

        Product product1 = Product.builder()
                .id(productId)
                .name("Product 1")
                .price(BigDecimal.valueOf(100.0))
                .build();

        when(productService.findProductById(productId)).thenReturn(product1);

        // Добавляем товар в корзину
        inMemoryCartService.addToCart(userId, productId, quantity);

        // Проверяем что товар добавился
        CartDto existCart = inMemoryCartService.getCart(userId);
        assertThat(existCart.getItems())
                .isNotNull()
                .hasSize(1)
                .extracting(CartItemDto::getProductId)
                .containsExactly(productId);

        // When
        var resultCartDto = inMemoryCartService.removeFromCart(userId, productId);

        // Then
        assertThat(resultCartDto)
                .isNotNull()
                .isInstanceOf(CartDto.class);

        // Проверяем что корзина пустая
        var result = inMemoryCartService.getCart(userId);
        assertThat(result.getItems())
                .isNotNull()
                .isEmpty();

        // Дополнительные проверки
        assertThat(result.getTotalItems()).isZero();
        assertThat(result.getTotalPrice()).isEqualTo(BigDecimal.ZERO);
        assertThat(resultCartDto.getTotalItems()).isZero();
        assertThat(resultCartDto.getTotalPrice()).isEqualTo(BigDecimal.ZERO);
    }
}
