package com.pavel.store.repository;

import com.pavel.store.entity.User;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    private User savedUser;


    @BeforeEach
    void setUp() {
        // Подготовка данных для КАЖДОГО теста
        savedUser = User.builder()
                .email("john@example.com")
                .lastName("Smith")
                .firstName("Alice")
                .username("AliceSmith")
                .password("pass12345")
                .build();
        entityManager.persist(savedUser);
        entityManager.flush();
    }

    @Test
    void shouldFindByEmail() {
        // When
        Optional<User> found = userRepository.findByEmail("john@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldExistsByEmail() {
        // When & Then
        assertThat(userRepository.existsByEmail("john@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    void shouldSaveUser() {
        // Given
        User savedUser = User.builder()
                .email("bob@example.com")
                .lastName("Johnson")
                .firstName("Bob")
                .username("BobJohnson")
                .password("11512512")
                .build();

        // When
        User saved = userRepository.save(savedUser);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("bob@example.com");

        // Проверяем что действительно сохранено в БД
        User fromDb = entityManager.find(User.class, saved.getId());
        assertThat(fromDb.getUsername()).isEqualTo("BobJohnson");
    }

    @Test
    void shouldUpdateUser() {
        // Given
        savedUser.setUsername("JohnUpdated");

        // When
        User updated = userRepository.save(savedUser);

        // Then
        assertThat(updated.getUsername()).isEqualTo("JohnUpdated");

        User fromDb = entityManager.find(User.class, savedUser.getId());
        assertThat(fromDb.getUsername()).isEqualTo("JohnUpdated");
    }

    @Test
    void shouldDeleteUser() {
        // When
        userRepository.delete(savedUser);
        entityManager.flush();

        // Then
        User deleted = entityManager.find(User.class, savedUser.getId());
        assertThat(deleted).isNull();
    }

    @Test
    void shouldFindAllUsers() {
        // Given - еще один пользователь

        User savedUser = User.builder()
                .email("bob@example.com")
                .lastName("Johnson")
                .firstName("Bob")
                .username("BobJohnson")
                .password("12315151")
                .build();

        entityManager.persist(savedUser);
        entityManager.flush();

        // When
        List<User> users = userRepository.findAll();

        // Then
        Assertions.assertThat(users).hasSize(2);

    }
}

