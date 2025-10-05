package com.pavel.store.controller.rest;

import com.pavel.store.dto.request.AddToCartRequest;
import com.pavel.store.dto.response.CartDto;
import com.pavel.store.service.InMemoryCartService;
import com.pavel.store.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart API",description = "операции с корзиной")
public class CartController {

    private final InMemoryCartService cartService;
    private final UserService userService;

    @GetMapping
    public CartDto getCart(@AuthenticationPrincipal(expression = "id") Long userId) {
        // из SecurityContext
        return cartService.getCart(userId);
    }

    @PostMapping("/items")
    public CartDto addToCart(
            @RequestBody AddToCartRequest request,
            @AuthenticationPrincipal(expression = "id") Long userId) {

        return cartService.addToCart(userId, request.getProductId(), request.getQuantity());
    }

    @DeleteMapping("/items/{productId}")
    public CartDto removeFromCart(@PathVariable Long productId, @AuthenticationPrincipal(expression = "id") Long userId) {

        return cartService.removeFromCart(userId, productId);
    }


}