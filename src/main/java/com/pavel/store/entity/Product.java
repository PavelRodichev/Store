package com.pavel.store.entity;


import jakarta.persistence.*;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = {"category"})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @Column(name = "product_price", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @Column(name = "product_description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "product_article", unique = true, nullable = false)
    private String article;

    @Column(name = "product_amount")
    @Min(0)
    private Integer amount;

    private String image;

    @Column(name = "is_available", nullable = false)
    private Boolean available = true;

    @CreatedDate // ← Будет работать только с @EnableJpaAuditing
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // ← Будет работать только с @EnableJpaAuditing
    private LocalDateTime updatedAt;

}
