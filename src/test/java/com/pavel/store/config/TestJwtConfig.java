package com.pavel.store.config;

import com.pavel.store.security.jwt.CustomUserDetailsService;
import com.pavel.store.security.jwt.JwtTokenProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

@TestConfiguration
@Profile("test")
public class TestJwtConfig {

    @Bean
    @Profile("test")
    public JwtTokenProvider testJwtTokenProvider() {
        return new JwtTokenProvider() {
            @Override
            public boolean validateJwtToken(String authToken) {
                return true; // Всегда валидный в тестах
            }

            @Override
            public Long getUserIdFromJWT(String token) {
                return 1L; // Возвращаем фиксированный ID для тестов
            }

            @Override
            public String generateToken(org.springframework.security.core.Authentication authentication) {
                return "test-jwt-token";
            }
        };
    }

    @Bean
    @Profile("test")
    public CustomUserDetailsService testCustomUserDetailsService() {
        return new CustomUserDetailsService(null) { // Передаем null, так как в тестах не используем реальный UserService
            @Override
            public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
                // Возвращаем тестового пользователя
                return User.builder()
                        .username("test@example.com")
                        .password("password")
                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                        .build();
            }

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return User.builder()
                        .username(username)
                        .password("password")
                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                        .build();
            }
        };
    }
}