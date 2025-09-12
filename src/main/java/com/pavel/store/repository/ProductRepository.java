package com.pavel.store.repository;

import com.pavel.store.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {



    Page<Product> findAll(Pageable pageable);

    Optional<Product> findById(Long id);

    Optional<Product> findByName(String name);


    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.name= :categoryName")
    Page<Product> findByCategoryName(@Param(value = "categoryName") String categoryName, Pageable pageable);


}
