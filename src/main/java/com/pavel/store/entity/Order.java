package com.pavel.store.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = {"user", "items"})
public class Order {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "total_amount")
    BigDecimal totalAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<OrderItem> items = new ArrayList<>();

    String address;

    @Enumerated(EnumType.STRING)
    OrderStatus orderStatus;

    @CreatedDate
    @Column(name = "order_date")
    LocalDateTime orderDate;


    public void calculateTotalAmount() {
        this.totalAmount = BigDecimal.ZERO; // Всегда сбрасываем в ноль

        if (this.items != null) {
            for (OrderItem item : this.items) {
                if (item != null && item.getProductPrice() != null && item.getQuantity() != null) {
                    BigDecimal itemTotal = item.getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    this.totalAmount = this.totalAmount.add(itemTotal);
                }
            }
        }
    }
}
