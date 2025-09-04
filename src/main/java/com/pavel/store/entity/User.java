package com.pavel.store.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "users")
@Component
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    private String password;

    private String firstName;

    private String lastName;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Order> orders = new ArrayList<>();


}
