package com.pavel.store.service;

import com.pavel.store.dto.request.OrderCreateDto;
import com.pavel.store.dto.request.OrderItemRequestDto;
import com.pavel.store.dto.request.OrderUpdateDto;
import com.pavel.store.dto.response.OrderItemResponseDto;
import com.pavel.store.dto.response.OrderResponseDto;
import com.pavel.store.entity.*;
import com.pavel.store.handler.exeption.EntityNotFoundException;
import com.pavel.store.mapper.mapers.OrderItemMapper;
import com.pavel.store.mapper.mapers.OrderMapper;
import com.pavel.store.repository.OrderItemRepository;
import com.pavel.store.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.pavel.store.entity.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserService userService;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private ProductService productService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;


    @Test
    void testFindAllOrders() {
        Pageable pageable = mock(Pageable.class);
        Order order = new Order();
        List<Order> orders = Collections.singletonList(order);
        Page<Order> page = new PageImpl<>(orders);

        when(orderRepository.findAll(pageable)).thenReturn(page);
        when(orderMapper.toDto(order)).thenReturn(new OrderResponseDto());

        Page<OrderResponseDto> result = orderService.findAllOrders(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(orderRepository).findAll(pageable);
        verify(orderMapper).toDto(order);

    }

    @Test
    void testFindOrderById_ExistingOrder() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        OrderResponseDto dto = new OrderResponseDto();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(dto);

        OrderResponseDto result = orderService.findOrderById(orderId);

        assertNotNull(result);
        assertEquals(dto, result);
        verify(orderRepository).findById(orderId);
        verify(orderMapper).toDto(order);
    }

    @Test
    void testFindOrderById_OrderNotFound() {
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.findOrderById(orderId));
        verify(orderRepository).findById(orderId);
    }

    @Test
    void testCreateOrder_SuccessfulCreation() {
        OrderCreateDto createDto = new OrderCreateDto();
        createDto.setUserId(1L);
        createDto.setShippingAddress("Some address");
        createDto.setItems(Collections.singletonList(new OrderItemRequestDto(1L, 2)));

        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100));
        product.setAmount(10);
        product.setArticle("ART-123");

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderStatus(OrderStatus.CREATED);
        order.setAddress("Some address");

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);

        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(1L);

        when(userService.findUserById(1L)).thenReturn(user);
        when(productService.findProductById(1L)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(any(Order.class))).thenReturn(dto);


        OrderResponseDto result = orderService.createOrder(createDto);

        assertNotNull(result);
        assertEquals(dto, result);

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testCreateOrder_UserNotFound() {
        OrderCreateDto createDto = new OrderCreateDto();
        createDto.setUserId(1L);

        when(userService.findUserById(1L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(createDto));
    }

    @Test
    void testCreateOrder_ProductNotFound() {
        OrderCreateDto createDto = new OrderCreateDto();
        createDto.setUserId(1L);
        createDto.setItems(Collections.singletonList(new OrderItemRequestDto(1L, 2)));

        User user = new User();
        user.setId(1L);

        when(userService.findUserById(1L)).thenReturn(user);
        when(productService.findProductById(1L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(createDto));
    }

    @Test
    void testCreateOrder_InsufficientStock() {
        OrderCreateDto createDto = new OrderCreateDto();
        createDto.setUserId(1L);
        createDto.setItems(Collections.singletonList(new OrderItemRequestDto(1L, 15)));

        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setAmount(10);

        when(userService.findUserById(1L)).thenReturn(user);
        when(productService.findProductById(1L)).thenReturn(product);

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(createDto));
    }

    @Test
    void testFindOrderByUserId_OrderFound() {
        Long userId = 1L;
        Order order = new Order();
        order.setId(1L);
        order.setUser(new User());
        OrderResponseDto dto = new OrderResponseDto();

        when(orderRepository.findByUser_Id(userId)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(dto);

        OrderResponseDto result = orderService.findOrderByUserId(userId);

        assertNotNull(result);
        assertEquals(dto, result);
    }

    @Test
    void testFindOrderByUserId_OrderNotFound() {
        Long userId = 1L;

        when(orderRepository.findByUser_Id(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.findOrderByUserId(userId));
    }

    @Test
    void testDeleteById_OrderExists() {
        Long id = 1L;

        when(orderRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> orderService.deleteById(id));
        verify(orderRepository).deleteById(id);
    }

    @Test
    void testDeleteById_OrderDoesNotExist() {
        Long id = 1L;

        when(orderRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> orderService.deleteById(id));
    }

    @Test
    void testUpdateOrderById_SuccessfulUpdate() {
        Long id = 1L;
        OrderUpdateDto updateDto = new OrderUpdateDto();
        updateDto.setShippingAddress("New Address");
        updateDto.setStatus("COMPLETED");

        Order order = new Order();
        order.setId(id);
        order.setOrderStatus(OrderStatus.CREATED);

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(new OrderResponseDto());

        OrderResponseDto result = orderService.updateOrderById(updateDto, id);

        assertNotNull(result);
        assertEquals("COMPLETED", order.getOrderStatus().name());
        verify(orderRepository).findById(id);
        verify(orderMapper).toDto(order);
    }

    @Test
    void testUpdateOrderById_OrderNotFound() {
        Long id = 1L;
        OrderUpdateDto updateDto = new OrderUpdateDto();

        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.updateOrderById(updateDto, id));
    }
}