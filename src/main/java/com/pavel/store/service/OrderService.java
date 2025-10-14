package com.pavel.store.service;

import com.pavel.store.dto.request.OrderUpdateDto;
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
                .orderStatus(OrderStatus.CREATED)
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

//проверяем кол-во продуктов
            if (product.getAmount() > 0 && product.getAmount() >= orderItem.getQuantity()) {
                product.setAmount(product.getAmount() - orderItem.getQuantity());
            } else {
                throw new IllegalArgumentException("the product quantity must be > 0");
            }

            saved.getItems().add(orderItem);

        }
        saved.calculateTotalAmount();
        Order savedOrder = orderRepository.save(saved);
        OrderResponseDto orderResponseDto = orderMapper.toDto(savedOrder);
        return orderResponseDto;
    }

    @Transactional(readOnly = true)
    public OrderResponseDto findOrderByUserId(Long userId) {

        return orderRepository.findByUser_Id(userId).map(orderMapper::toDto).orElseThrow(() -> new EntityNotFoundException("Order"));
    }

    @Transactional
    public void deleteById(Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Order", id);
        }
    }

    @Transactional
    public OrderResponseDto updateOrderById(OrderUpdateDto orderUpdateDto, Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Order", id));
        order.setAddress(orderUpdateDto.getShippingAddress());
        order.setOrderStatus(OrderStatus.valueOf(orderUpdateDto.getStatus()));
        return orderMapper.toDto(order);
    }
}
