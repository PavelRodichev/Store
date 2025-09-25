package com.pavel.store.controller;

import com.pavel.store.dto.request.OrderCreateDto;
import com.pavel.store.dto.response.OrderResponseDto;
import com.pavel.store.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Slf4j
@Tag(name = "Order API", description = "Операции с заказами")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderCreateDto request) {
        log.info("Received request to create order for user: {}", request.getUserId());
        OrderResponseDto order = orderService.createOrder(request);
        return ResponseEntity.ok(order);
    }


    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        log.info("Received request to get all orders - page: {}, size: {}", page, size);


        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponseDto> orders = orderService.findAllOrders(pageable);

        return ResponseEntity.ok(orders);
    }

}
