package com.pavel.store.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName; // Название на момент покупки

    @Column(name = "product_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal productPrice = BigDecimal.ZERO; // Цена на момент покупки

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // Количество купленного товара

    @Column(name = "product_article", length = 100)
    private String productArticle; // Артикул на момент покупки


}
