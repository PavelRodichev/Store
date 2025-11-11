package com.pavel.store.service;

import com.pavel.store.aop.MethodTime;
import com.pavel.store.dto.request.OrderUpdateDto;
import com.pavel.store.dto.response.*;
import com.pavel.store.events.ChangeAddressEvent;
import com.pavel.store.handler.exeption.EntityNotFoundException;
import com.pavel.store.dto.request.OrderCreateDto;
import com.pavel.store.dto.request.OrderItemRequestDto;
import com.pavel.store.entity.*;

import com.pavel.store.mapper.mapers.OrderMapper;

import com.pavel.store.mapper.mapers.OrderMapperImpl;
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
import java.util.function.Function;
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
    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final IdempotencyService idempotencyService;


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
    public OrderResponseDto createOrder(OrderCreateDto createDto, String key) {
        // валидация ключа идемпотентности
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("key cannot be null");
        }
        // сначала ходим в кеш редис, если ключ уже есть то возвращаем существующий заказ
        if (idempotencyService.hasExistKey(key)) {
            log.info("key idempotency already exist in redis,returning existing order");
            var orderId = idempotencyService.getOrderIdByKey(key);
            return orderRepository.findById(orderId).map(orderMapper::toDto).orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        }
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

        // id товаров которые есть в заказе
        List<Long> lockedProducts = createDto.getItems().stream().map(OrderItemRequestDto::getProductId).toList();

        // блокируем товары и добовляем в мапу с ключом от товара
        Map<Long, Product> productMap = productService.getProductByIdWithLock(lockedProducts)
                .stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        // проходим циклом по List<OrderItemRequestDto> из createDto
        for (OrderItemRequestDto itemRequestDto : createDto.getItems()) {

            // находим залоченный продукт из OrderItemRequestDto в мапе
            Product product = productMap.get(itemRequestDto.getProductId());
            if (product == null) {
                throw new EntityNotFoundException("Product", itemRequestDto.getProductId());
            }
            // билдим OrderItem через метод getBuild
            OrderItem orderItem = getBuild(itemRequestDto, saved, product);

//проверяем кол-во продуктов
            if (product.getAmount() >= itemRequestDto.getQuantity() && itemRequestDto.getQuantity() > 0) {
                product.setAmount(product.getAmount() - itemRequestDto.getQuantity());
            } else {
                throw new IllegalArgumentException("the product quantity must be > 0");
            }

            // добавляем в заказ OrderItem
            saved.getItems().add(orderItem);

        }
        saved.calculateTotalAmount();

        Order savedOrder = orderRepository.save(saved);

        idempotencyService.saveKeyWithOrderId(key, savedOrder.getId());

        log.info("OrderId:{} save in redis cache with key:{}", savedOrder.getId(), key);
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

    @Transactional
    public OrderResponseDto changeAddress(String newAddress, Long orderId) {


        var order = orderRepository.findById(orderId).orElseThrow(() ->
                new EntityNotFoundException("Order", orderId));
        order.setAddress(newAddress);
        log.info("Order {} address changed to: {}", orderId, newAddress);

        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderResponseDto completedOrder(Long orderId) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        order.setOrderStatus(OrderStatus.COMPLETED);
        log.info("order status changed to:{}", order.getOrderStatus());
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderResponseDto cancelledOrder(Long orderId) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        order.setOrderStatus(OrderStatus.CANCELLED);
        log.info("Order {} order status changed to:{}", order.getId(), order.getOrderStatus());
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

