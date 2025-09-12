package com.pavel.store.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductUpdateDto {

    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private String categoryName;

    @Size(min = 3, max = 50, message = "Article must be between 3 and 50 characters")
    private String article;

    @Min(value = 1, message = "Amount cannot be negative")
    private Integer amount;

    @URL(message = "Image URL must be valid")
    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;

}
