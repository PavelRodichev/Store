package com.pavel.store.controller.rest;

import com.pavel.store.dto.request.OrderCreateDto;
import com.pavel.store.dto.request.OrderItemRequestDto;
import com.pavel.store.dto.request.OrderUpdateDto;
import com.pavel.store.dto.response.*;
import com.pavel.store.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Slf4j
@Tag(name = "Order API", description = "Операции с заказами")
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Создать новый заказ",
            description = "Создание заказа с идемпотентным ключом для предотвращения дублирования. Проверяет наличие товаров на складе."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Заказ успешно создан",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неверные входные данные",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Недостаточно товара на складе",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @Parameter(
                    description = "Данные для создания заказа",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrderCreateDto.class))
            )
            @RequestBody OrderCreateDto request,
            @Parameter(
                    description = "Идемпотентный ключ для предотвращения дублирования запросов",
                    required = true,
                    example = "unique-order-key-12345"
            )
            @RequestHeader(value = "Idempotency-Key") String key) {

        log.info("Received request to create order for user: {}", request.getUserId());
        OrderResponseDto order = orderService.createOrder(request, key);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);

    }


    @GetMapping("/{productId}")
    @Operation(
            summary = "Получить заказы по ID товара",
            description = "Возвращает список всех заказов, содержащих указанный товар"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Заказы успешно найдены",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Товар не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<OrderResponseDto>> findOrdersByProductId(@PathVariable @NotNull Long productId) {

        List<OrderResponseDto> list = orderService.getOrdersByProductId(productId);

        return ResponseEntity.ok(list);
    }

    @GetMapping
    @Operation(
            summary = "Получить все заказы",
            description = "Возвращает страницу заказов с пагинацией и сортировкой"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Страница заказов успешно получена",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    public ResponseEntity<PageResponse<OrderResponseDto>> getAllOrders(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("Received request to get all orders - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<OrderResponseDto> orders = orderService.findAllOrders(pageable);
        return ResponseEntity.ok(PageResponse.of(orders));
    }


    @GetMapping("/order")
    @Operation(
            summary = "Найти заказ по ID заказа или пользователя",
            description = "Поиск заказа по ID заказа или по ID пользователя. Должен быть указан хотя бы один параметр."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Заказ успешно найден"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Не указан ни orderId ни userId",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Заказ не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<?> getOrder(@RequestParam(required = false) Long orderId,
                                      @RequestParam(required = false) Long userId) {

        if (orderId != null) {
            return ResponseEntity.ok(orderService.findOrderById(orderId));
        } else if (userId != null) {
            return ResponseEntity.ok(orderService.findOrderByUserId(userId));
        } else
            return ResponseEntity.badRequest().body("Either orderId or userId must be provided");
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить заказ по ID",
            description = "Удаляет заказ по его идентификатору. Операция необратима."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Заказ успешно удален"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Заказ не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public void deleteById(@PathVariable Long id) {
        orderService.deleteById(id);
    }


    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить заказ",
            description = "Обновляет информацию о заказе по его ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Заказ успешно обновлен",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Заказ не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<OrderResponseDto> updateOrder(@RequestBody OrderUpdateDto orderUpdateDto,
                                                        @Parameter(description = "Unique identifier of the order", example = "25")
                                                        @PathVariable Long id) {

        return ResponseEntity.ok(orderService.updateOrderById(orderUpdateDto, id));
    }

    @PutMapping
    @Operation(
            summary = "Добавить товары в заказ",
            description = "Добавляет новые товары в существующий заказ"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Товары успешно добавлены в заказ",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Заказ не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Недостаточно товара на складе",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<OrderResponseDto> addItemsToOrder(@RequestBody OrderItemRequestDto orderItemRequestDto, @RequestParam Long orderId) {

        OrderResponseDto orderResponseDto = orderService.addItemsToOrder(orderItemRequestDto, orderId);
        return ResponseEntity.ok(orderResponseDto);
    }

    @GetMapping("/groupOfProducts")
    @Operation(
            summary = "Получить товары с группировкой по заказам",
            description = "Возвращает список товаров с информацией о заказах, в которых они присутствуют"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные успешно получены",
                    content = @Content(schema = @Schema(implementation = ProductWithOrdersResponse.class))
            )
    })
    public ResponseEntity<List<ProductWithOrdersResponse>> getProductsGroupingOrders() {
        return ResponseEntity.ok(orderService.getGroupingProductsByOrder());
    }
}
