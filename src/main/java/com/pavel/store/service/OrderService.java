package com.pavel.store.service;

import com.pavel.store.aop.MethodTime;
import com.pavel.store.dto.request.OrderUpdateDto;
import com.pavel.store.dto.response.*;
import com.pavel.store.handler.exeption.EntityNotFoundException;
import com.pavel.store.dto.request.OrderCreateDto;
import com.pavel.store.dto.request.OrderItemRequestDto;
import com.pavel.store.entity.*;

import com.pavel.store.mapper.mapers.OrderMapper;

import com.pavel.store.mapper.mapers.ProductMapper;
import com.pavel.store.mapper.mapers.UserMapper;
import com.pavel.store.repository.OrderRepository;
import com.pavel.store.repository.ProductRepository;
import com.pavel.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserService userService;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;

    @Transactional
    public List<ProductWithOrdersResponse> getGroupingProductsByOrder() {

        List<Order> orderList = orderRepository.findAllWithItemsAndUsers();
        Map<ProductResponseDto, List<OrderWithUserDto>> productOrderMap = new HashMap<>();
        List<String> email = orderList.stream().map(order -> order.getUser().getEmail()).collect(Collectors.toList());


        for (Order order : orderList) {

            for (OrderItem orderItem : order.getItems()) {
                OrderWithUserDto orderWithUserDto = new OrderWithUserDto();
                orderWithUserDto.setUserResponseDto(userMapper.toDto(order.getUser()));
                orderWithUserDto.setOrderResponseDto(orderMapper.toDto(order));

                productOrderMap
                        .computeIfAbsent(productMapper.toDto(orderItem.getProduct()), k -> new ArrayList<>())
                        .add(orderWithUserDto);
            }
        }

        try {
            ResponseEntity response = restTemplate.postForEntity("http://localhost:8082/api/inn", email, Map.class);
            Map<String, String> mapEmails;
            if (response.getStatusCode().is2xxSuccessful()) {
                mapEmails = (Map<String, String>) response.getBody();

                for (Map.Entry<ProductResponseDto, List<OrderWithUserDto>> entry : productOrderMap.entrySet()) {
                    entry.getValue().stream().forEach(a -> {
                        UserResponseDto userResponseDto = a.getUserResponseDto();
                        userResponseDto.setInn(mapEmails.get(userResponseDto.getEmail()));
                    });
                }
            }

        } catch (Exception ex) {
            log.warn("Failed to get INN, using default values");
        }
        return productOrderMap.entrySet().stream()
                .map(entry ->
                        ProductWithOrdersResponse.builder()
                                .orders(entry.getValue())
                                .product(entry.getKey()).build()
                ).toList();
    }

    @Transactional
    public List<Order> getOrderByProductName(String productName) {
        return orderRepository.findAllByProductName(productName);
    }

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
            OrderItem orderItem = getBuild(items, saved, product);

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


    @MethodTime
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("Product", productId);
        }
        List<Order> orders = orderRepository.getOrdersByProductId(productId);
        return orders.stream().map(orderMapper::toDto).toList();
    }

    @MethodTime
    @Transactional
    public OrderResponseDto addItemsToOrder(OrderItemRequestDto orderItemRequestDto, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order", orderId));

        Product product = productRepository.findById(orderItemRequestDto.getProductId()).orElseThrow(() -> new EntityNotFoundException("Product", orderItemRequestDto.getProductId()));

        OrderItem orderItem = getBuild(orderItemRequestDto, order, product);

        order.getItems().add(orderItem);


        if (product.getAmount() > 0 && product.getAmount() >= orderItem.getQuantity()) {
            product.setAmount(product.getAmount() - orderItem.getQuantity());
        } else {
            throw new IllegalArgumentException("the product quantity must be > 0");
        }

        order.calculateTotalAmount();
        return orderMapper.toDto(order);
    }

    // Билдер для OrderItem
    private static OrderItem getBuild(OrderItemRequestDto dto, Order order, Product product) {
        return OrderItem.builder()
                .order(order)
                .quantity(dto.getQuantity())
                .productArticle(product.getArticle())
                .productPrice(product.getPrice())
                .productName(product.getName())
                .product(product)
                .build();
    }

}

