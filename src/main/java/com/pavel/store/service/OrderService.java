package com.pavel.store.service;

import com.pavel.store.handler.exeption.EntityNotFoundException;
import com.pavel.store.dto.request.OrderCreateDto;
import com.pavel.store.dto.request.OrderItemRequestDto;
import com.pavel.store.dto.response.OrderResponseDto;
import com.pavel.store.entity.*;
import com.pavel.store.mapper.mapers.OrderItemMapper;
import com.pavel.store.mapper.mapers.OrderMapper;
import com.pavel.store.repository.OrderItemRepository;
import com.pavel.store.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserService userService;
    private final OrderItemMapper orderItemMapper;
    private final ProductService productService;
    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> findAllOrders(Pageable pageable) {

        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto findOrderById(Long id) {

        return orderRepository.findById(id).map(orderMapper::toDto).orElseThrow(() -> new EntityNotFoundException("Order", id));
    }

    @Transactional
    public OrderResponseDto createOrder(OrderCreateDto createDto) {
        log.info("Creating order for user: {}", createDto.getUserId());


        User user = userService.findUserById(createDto.getUserId());
        if (user == null) {
            throw new EntityNotFoundException("User", createDto.getUserId());
        }

        Order saved = Order.builder()
                .address(createDto.getShippingAddress())
                .orderStatus(OrderStatus.PENDING)
                .user(user)
                .build();

        log.info("Order status after building: {}", saved.getOrderStatus());

        for (OrderItemRequestDto items : createDto.getItems()) {
            Product product = productService.findProductById(items.getProductId());
            if (product == null) {
                throw new EntityNotFoundException("Product", items.getProductId());
            }
            OrderItem orderItem = OrderItem.builder()
                    .order(saved)
                    .quantity(items.getQuantity())
                    .productArticle(product.getArticle())
                    .productPrice(product.getPrice())
                    .productName(product.getName())
                    .product(product)
                    .build();

            saved.getItems().add(orderItem);
        }
        saved.calculateTotalAmount();
        OrderResponseDto orderResponseDto = orderMapper.toDto(orderRepository.save(saved));
        return orderResponseDto;
    }

}
