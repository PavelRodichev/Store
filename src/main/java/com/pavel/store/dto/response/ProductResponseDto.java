package com.pavel.store.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Builder
@Data
public class ProductResponseDto {

    Long id;

    String name;

    String description;

    String categoryName;

    BigDecimal price;

    Integer amount;

    String article;

    String image;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

}
