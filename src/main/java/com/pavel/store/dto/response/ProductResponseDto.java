package com.pavel.store.dto.response;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Builder
@Value
public class ProductResponseDto {


    Long id;

    String name;

    String description;

    String categoryName;

    BigDecimal price;

    Integer amount;

    String article;

    String imageUrl;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

}
