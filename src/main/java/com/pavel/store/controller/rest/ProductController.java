package com.pavel.store.controller.rest;


import com.pavel.store.dto.request.ProductFilterDto;
import com.pavel.store.dto.response.PageResponse;
import com.pavel.store.dto.response.ProductResponseDto;
import com.pavel.store.entity.Product;
import com.pavel.store.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product API", description = "Операции с продуктами")
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "Получить все товары",
            description = "Возвращает страницу товаров с пагинацией. Поддерживает выбор валюты через заголовок."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Товары успешно получены",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные параметры пагинации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAllProduct(
            @Parameter(
                    description = "Параметры пагинации и сортировки",
                    example = "{\"page\": 0, \"size\": 20, \"sort\": \"name,asc\"}"
            ) @PageableDefault(size = 20) Pageable pageable,
            @Parameter(
                    description = "Валюта для отображения цен (например: USD, EUR, RUB)",
                    example = "USD"
            ) @RequestHeader(value = "X-Currency", required = false) String currency) {
        Page<ProductResponseDto> result = productService.getAllProduct(pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Поиск и фильтрация товаров",
            description = "Поиск товаров по различным критериям: название, категория, цена, наличие на складе"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Товары успешно найдены",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные параметры фильтрации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDto>> getFilterProduct(
            @Parameter(
                    description = "Параметры фильтрации товаров",
                    content = @Content(schema = @Schema(implementation = ProductFilterDto.class))
            )
            ProductFilterDto productFilterDto,
            @Parameter(
                    description = "Параметры пагинации",
                    example = "{\"page\": 0, \"size\": 20, \"sort\": \"price,desc\"}"
            )
            @PageableDefault(size = 20) Pageable pageable) {

        Page<ProductResponseDto> productsList = productService.getProductsWithFilter(productFilterDto, pageable);
        return ResponseEntity.ok(productsList);
    }

    @Operation(
            summary = "Получить товар по ID",
            description = "Возвращает подробную информацию о товаре по его идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Товар успешно найден",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(
            @PathVariable Long id) {

        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(
            summary = "Получить изображение товара",
            description = "Возвращает изображение товара в виде массива байтов"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Изображение успешно получено",
                    content = @Content(mediaType = MediaType.IMAGE_JPEG_VALUE)
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар или изображение не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}/image")
    private byte[] findImage(@PathVariable Long id) {
        return productService.getImage(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


}
