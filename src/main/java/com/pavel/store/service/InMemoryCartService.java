package com.pavel.store.service;

import com.pavel.store.dto.response.CartDto;
import com.pavel.store.dto.response.CartItemDto;
import com.pavel.store.entity.Product;
import com.pavel.store.handler.exeption.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class InMemoryCartService {

    private final Map<Long, Map<Long, Integer>> userCarts = new ConcurrentHashMap<>();

    private final ProductService productService;

    /**
     * Получить корзину пользователя
     */
    @Transactional
    public CartDto getCart(Long userId) {
        log.debug("Getting cart for user: {}", userId);

        Map<Long, Integer> cartItems = userCarts.getOrDefault(userId, new HashMap<>());

        List<CartItemDto> items = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalItems = 0;

        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            try {
                Product product = productService.findProductById(entry.getKey());
                Integer quantity = entry.getValue();
                BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));

                items.add(new CartItemDto(product.getId(), product.getName(),
                        product.getPrice(),
                        quantity, itemTotal));

                totalPrice = totalPrice.add(itemTotal);
                totalItems += quantity;
            } catch (Exception e) {
                log.warn("Product {} not found, removing from cart", entry.getKey());
                // Автоматически удаляем несуществующие товары
                removeFromCart(userId, entry.getKey());
            }
        }

        CartDto cartDto = new CartDto();
        cartDto.setItems(items);
        cartDto.setTotalPrice(totalPrice);
        cartDto.setTotalItems(totalItems);

        return cartDto;
    }

    /**
     * Добавить товар в корзину
     */
    @Transactional(readOnly = true)
    public CartDto addToCart(Long userId, Long productId, int quantity) {
        Map<Long, Integer> userCart = userCarts.computeIfAbsent(userId, k -> new HashMap<>());

        Product product = productService.findProductById(productId);
        if (product == null) {
            throw new EntityNotFoundException("Product", productId);
        }

        int currentQty = userCart.getOrDefault(productId, 0);
        userCart.put(productId, currentQty + quantity);

        return getCart(userId);
    }

    /**
     * Удалить товар из корзины
     */
    @Transactional(readOnly = true)
    public CartDto removeFromCart(Long userId, Long productId) {
        Map<Long, Integer> userCart = userCarts.get(userId);
        if (userCart != null) {
            userCart.remove(productId);
        }
        return getCart(userId);
    }

    /**
     * Очистить корзину
     */
    @Transactional(readOnly = true)
    @Scheduled(fixedRate = 3600000)
    public void clearCart() {
        userCarts.clear();
        log.info("The cart has been emptied");
    }

}


