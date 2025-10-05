package com.pavel.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class CartDto {
    private List<CartItemDto> items;
    private BigDecimal totalPrice;
    private Integer totalItems;

    private LocalDateTime created;

    public CartDto() {
        this.created = LocalDateTime.now();
    }
}
