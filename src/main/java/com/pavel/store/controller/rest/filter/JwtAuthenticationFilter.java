package com.pavel.store.controller.rest.filter;

import com.pavel.store.security.jwt.CustomUserDetailsService;
import com.pavel.store.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;

    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("=== JWT FILTER START: {} {} ===", request.getMethod(), request.getRequestURI());

        String path = request.getServletPath();

        //  Расширяем список публичных endpoints
        if (isPublicPath(path)) {
            log.info("Skipping JWT filter for public path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = getJwtFromRequest(request);
            log.info("JWT token found: {}", jwt != null);

            if (StringUtils.hasText(jwt)) {
                log.info("Validating JWT token...");

                //  Сначала проверяем валидность токена
                if (jwtTokenProvider.validateJwtToken(jwt)) {
                    Long userId = jwtTokenProvider.getUserIdFromJWT(jwt);
                    log.info("JWT token valid for user ID: {}", userId);

                    //  Загружаем пользователя
                    UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                    log.info("User loaded: {}", userDetails.getUsername());


                    // Создаем аутентификацию
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Authentication set in SecurityContext for user: {}", userDetails.getUsername());

                } else {
                    log.warn("JWT token validation failed for path: {}", path);
                }
            } else {
                log.warn("No JWT token found for protected path: {}", path);
            }
        } catch (UsernameNotFoundException e) {
            // ПОЛЬЗОВАТЕЛЬ УДАЛЕН - ОЧИЩАЕМ КОНТЕКСТ
            log.warn("User not found: {}. Clearing security context.", e.getMessage());
            SecurityContextHolder.clearContext();

            // Удаляем невалидный токен из cookie
            clearInvalidTokenCookie(response);

        } catch (Exception e) {
            log.error("JWT Authentication failed: {}", e.getMessage(), e);
        }

        log.info("=== JWT FILTER END ===");
        filterChain.doFilter(request, response);

    }

    private void clearInvalidTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // Удалить cookie
        response.addCookie(cookie);
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/auth/") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/swagger-ui") ||
               path.contains("kafka") ||
               path.startsWith("/api/v1/products/") ||
               path.startsWith("/api/v1/categories/");
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        // 1. Проверяем заголовок Authorization (для REST API)
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 2. Проверяем cookie (для веб-форм)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 3. Проверяем параметр запроса
        String tokenParam = request.getParameter("token");
        if (StringUtils.hasText(tokenParam)) {
            return tokenParam;
        }

        return null;
    }
}
