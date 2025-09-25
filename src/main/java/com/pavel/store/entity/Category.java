package com.pavel.store.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "categories")
@Entity
@ToString(exclude = {"products"})
public class Category {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "category_name")
    String name;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    List<Product> products = new ArrayList<>();

}
