package com.pavel.store.controller.rest.filter;

import com.pavel.store.controller.rest.session.SessionCurrency;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CurrencyFilter extends OncePerRequestFilter {

    private final SessionCurrency sessionCurrency;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("X-Currency");

        if (header != null && !header.trim().isEmpty()) {
            String currency = header.toUpperCase().trim();
            if (header.equals("USD") || header.equals("EUR") || header.equals("RUB")) {
                sessionCurrency.setCurrency(currency);
            }
        }
        filterChain.doFilter(request, response);
    }

}
