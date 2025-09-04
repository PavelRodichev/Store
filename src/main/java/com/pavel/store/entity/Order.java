package com.pavel.store.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Component
public class Order {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "total_amount")
    BigDecimal totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    OrderStatus orderStatus;


    public void setTotalAmount() {
        for (OrderItem item : items) {
            totalAmount = totalAmount.add(item.price());
        }
    }
}
