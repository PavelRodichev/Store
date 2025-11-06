package com.pavel.store.controller.rest;

import com.pavel.store.dto.request.OrderCreateDto;
import com.pavel.store.dto.request.OrderItemRequestDto;
import com.pavel.store.dto.request.OrderUpdateDto;
import com.pavel.store.dto.response.*;
import com.pavel.store.service.OrderService;
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

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderCreateDto request,
                                                        @RequestHeader(value = "Idempotency-Key", required = false) String key) {
        log.info("Received request to create order for user: {}", request.getUserId());
        OrderResponseDto order = orderService.createOrder(request, key);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }


    @GetMapping("/{productId}")
    public ResponseEntity<List<OrderResponseDto>> findOrdersByProductId(@PathVariable @NotNull Long productId) {

        List<OrderResponseDto> list = orderService.getOrdersByProductId(productId);

        return ResponseEntity.ok(list);
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponseDto>> getAllOrders(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("Received request to get all orders - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<OrderResponseDto> orders = orderService.findAllOrders(pageable);
        return ResponseEntity.ok(PageResponse.of(orders));
    }


    @GetMapping("/order")
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
    public void deleteById(@PathVariable Long id) {
        orderService.deleteById(id);
    }


    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(@RequestBody OrderUpdateDto orderUpdateDto, @PathVariable Long id) {

        return ResponseEntity.ok(orderService.updateOrderById(orderUpdateDto, id));
    }

    @PutMapping
    public ResponseEntity<OrderResponseDto> addItemsToOrder(@RequestBody OrderItemRequestDto orderItemRequestDto, @RequestParam Long orderId) {

        OrderResponseDto orderResponseDto = orderService.addItemsToOrder(orderItemRequestDto, orderId);
        return ResponseEntity.ok(orderResponseDto);
    }

    @GetMapping("/groupOfProducts")
    public ResponseEntity<List<ProductWithOrdersResponse>> getProductsGroupingOrders() {
        return ResponseEntity.ok(orderService.getGroupingProductsByOrder());
    }
}
