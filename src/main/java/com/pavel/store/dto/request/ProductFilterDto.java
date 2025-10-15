package com.pavel.store.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductFilterDto {

    private String name;
    private Integer amount;
    private BigDecimal price;
    private Boolean isAvailable;
}
