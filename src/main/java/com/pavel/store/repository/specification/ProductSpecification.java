package com.pavel.store.repository.specification;


import com.pavel.store.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public Specification<Product> withFilter(String name, Integer minAmount, BigDecimal maxPrice, Boolean isAvailable) {

        return (root, query, criteriaBuilder) -> {

            Predicate predicate = criteriaBuilder.conjunction();

            if (name != null && !name.trim().isEmpty()) {

                Predicate newPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%");

                predicate = criteriaBuilder.and(predicate, newPredicate);
            }

            if (maxPrice != null) {
                Predicate newPredicate = criteriaBuilder.lessThanOrEqualTo(
                        root.get("price"),
                        maxPrice
                );
                predicate = criteriaBuilder.and(predicate, newPredicate);
            }

            if (minAmount != null) {
                Predicate newPredicate = criteriaBuilder.greaterThanOrEqualTo(
                        root.get("amount"),
                        minAmount
                );
                predicate = criteriaBuilder.and(predicate, newPredicate);
            }
            if (isAvailable != null) {
                Predicate newPredicate = criteriaBuilder.equal(
                        root.get("is_available"),
                        isAvailable
                );
                predicate = criteriaBuilder.and(predicate, newPredicate);
            }
            return predicate;
        };


    }
}
