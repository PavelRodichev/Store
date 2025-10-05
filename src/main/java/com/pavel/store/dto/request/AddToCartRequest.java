package com.pavel.store.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddToCartRequest {
    private Long productId;
    private int quantity = 1;
}
