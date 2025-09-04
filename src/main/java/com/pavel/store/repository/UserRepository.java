package com.pavel.store.repository;

import com.pavel.store.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    @Query("select u from User u left join fetch u.orders")
    Page<User> findAllUsers(Pageable pageable);


    Optional<User> findById(Long id);


    List<User> findAllByUsername(String username);


}
