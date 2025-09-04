package com.pavel.store.entity;


import jakarta.persistence.*;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
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
@Component
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "product_name", nullable = false)
    String name;

    @Column(name = "product_price", precision = 10, scale = 2)
    BigDecimal price;

    @Column(name = "product_description")
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @Column(name = "product_article", unique = true, nullable = false)
    String article;

    @Column(name = "product_amount")
    Integer amount;

    @Column(name = "date_creation")
    @CreationTimestamp
    LocalDate dateOfCreation;

    @Column(name = "changing_quantity")
    LocalDateTime changingTheQuantity;

}
