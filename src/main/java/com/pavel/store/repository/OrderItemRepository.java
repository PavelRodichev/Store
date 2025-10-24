package com.pavel.store.repository;

import com.pavel.store.entity.Order;
import com.pavel.store.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    Optional<OrderItem> findByIdAndOrderId(Long id, Long orderId);

    void deleteByOrderId(Long orderId);

    @Query("SELECT SUM(oi.productPrice) FROM OrderItem oi WHERE oi.order.id = :orderId")
    Optional<BigDecimal> calculateOrderTotalAmount(@Param("orderId") Long orderId);

}
