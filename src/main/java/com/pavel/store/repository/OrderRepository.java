package com.pavel.store.repository;

import com.pavel.store.dto.request.OrderCreateDto;
import com.pavel.store.entity.Order;
import com.pavel.store.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN FETCH o.items JOIN FETCH o.user WHERE o.id= :id ")
    Optional<Order> findById(Long id);

    List<Order> findByUser_Username(@NotBlank String userUsername);

    @EntityGraph(attributePaths = {"user", "items"})
    Page<Order> findAll(Pageable pageable);

    Order save(Order order);

    Optional<Order> findByUser_Id(Long id);

    @Query("SELECT o FROM  Order o JOIN  FETCH o.items it where it.product.id=:productId ")
    List<Order> getOrdersByProductId(Long productId);

    @Query("SELECT o From Order o JOIN FETCH o.items it where it.product.name = :prodcutName")
    List<Order> findAllByProductName(String productName);

    @Query("SELECT o FROM Order o JOIN FETCH o.items it JOIN FETCH it.product JOIN FETCH o.user")
    List<Order> findAllWithItemsAndUsers();

}
