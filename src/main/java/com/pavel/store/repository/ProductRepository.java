package com.pavel.store.repository;

import com.pavel.store.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {


    @Modifying(clearAutomatically = true)
    @Query("UPDATE Product p SET p.price=p.price * :multiply")
    void updateAllPrices(BigDecimal multiply);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category")
    Page<Product> findAll(Pageable pageable);


    Optional<Product> findById(Long id);


    Optional<Product> findByName(String name);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT p FROM Product p")
    List<Product> findAllWithPessimisticWrite();

    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.name= :categoryName")
    Page<Product> findByCategoryName(@Param(value = "categoryName") String categoryName, Pageable pageable);

    @Query(value = "SELECT * FROM products FOR UPDATE", nativeQuery = true)
    List<Product> findAllForUpdate();

    @Query(value = "SELECT * FROM products WHERE products.id = :id FOR UPDATE", nativeQuery = true)
    Optional<Product> findByIdForUpdate(@Param(value = "id") Long id);

    @EntityGraph(attributePaths = {"category"})
    Page<Product> findAll(Specification<Product> specification, Pageable pageable);

}
