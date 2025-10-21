package com.pavel.store.handler.Enhancer;

import com.pavel.store.controller.rest.ProductController;
import com.pavel.store.dto.response.PageResponse;
import com.pavel.store.dto.response.ProductResponseDto;

import com.pavel.store.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice(assignableTypes = ProductController.class)
@Component
@Slf4j
@RequiredArgsConstructor
public class ProductResponseEnhancer implements ResponseBodyAdvice<Object> {


    private final CurrencyService currencyService;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        if (body == null) return null;

        try {
            if (body == null) return null;

            if (body instanceof ProductResponseDto product) {
                return enhanceProduct(product);
            } else if (body instanceof PageResponse<?> page) {
                return enhancePageResponse(page);
            }
            return body;
        } catch (Exception e) {
            log.error("Error in ProductResponseEnhancer", e);
            return body;
        }
    }

    private Object enhancePageResponse(PageResponse<?> pageResponse) {
        BigDecimal rate = currencyService.getRate();

        List<Object> convertedContent = pageResponse.getContent().stream()
                .map(item -> {
                    if (item instanceof ProductResponseDto product) {
                        //  Это продукт - конвертируем
                        return enhanceProduct(product);
                    } else {
                        //  Не продукт - возвращаем как есть
                        return item;
                    }
                })
                .collect(Collectors.toList());


        return PageResponse.builder().content(Arrays.asList(convertedContent))
                .totalPages(pageResponse.getTotalPages()).currentPage(pageResponse.getCurrentPage())
                .pageSize(pageResponse.getPageSize())
                .totalElements(pageResponse.getTotalElements())
                .first(pageResponse.isFirst())
                .last(pageResponse.isLast()).build();
    }

    private ProductResponseDto enhanceProduct(ProductResponseDto product) {
        BigDecimal rate = currencyService.getRate();
        BigDecimal convertedPrice = calculateConvertedPrice(product.getPrice(), rate);
        return buildConvertedProduct(product, convertedPrice);
    }

    private ProductResponseDto buildConvertedProduct(ProductResponseDto original, BigDecimal convertedPrice) {
        return ProductResponseDto.builder()
                .id(original.getId())
                .name(original.getName())
                .article(original.getArticle())
                .description(original.getDescription())
                .price(original.getPrice())
                .exchangeRate(convertedPrice)
                .amount(original.getAmount())
                .image(original.getImage())
                .categoryName(original.getCategoryName())
                .createdAt(original.getCreatedAt())
                .updatedAt(original.getUpdatedAt())
                .available(original.getAvailable())
                .build();
    }

    private BigDecimal calculateConvertedPrice(BigDecimal originalPrice, BigDecimal rate) {
        if (originalPrice == null || rate == null) return null;
        return originalPrice.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
