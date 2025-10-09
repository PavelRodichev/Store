package com.pavel.store.repository;

import com.pavel.store.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Page<User> findAll(Pageable pageable);

    Optional<User> findById(Long id);


    Optional<User> findByUsername(String username);


    Optional<User> findByEmail(String mail);

    boolean existsByEmail(String mail);

    boolean existsUserByUsername(@NotBlank(message = "Username is required") String username);

}
