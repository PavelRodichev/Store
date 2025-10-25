package com.pavel.store.dto.request;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCreateDto {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "Category name is required")
    private String categoryName;

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount cannot be negative")
    @Max(value = 10000, message = "Amount cannot exceed 10000")
    private Integer amount;

    @NotBlank(message = "Article is required")
    @Size(min = 3, max = 50, message = "Article must be between 3 and 50 characters")
    private String article;

    private MultipartFile image;

    @NotNull(message = "is_available cannot be null")
    private Boolean available = true;
}
