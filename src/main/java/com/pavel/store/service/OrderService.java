package com.pavel.store.service;

import com.pavel.store.controller.handler.exeption.EntityNotFoundException;
import com.pavel.store.dto.response.OrderResponseDto;
import com.pavel.store.entity.Order;
import com.pavel.store.entity.User;
import com.pavel.store.mapper.mapers.OrderMapper;
import com.pavel.store.repository.OrderRepository;
import com.pavel.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;

    public Page<OrderResponseDto> findAllOrder(Pageable pageable) {

        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }

    public OrderResponseDto findOrderById(Long id) {

        return orderRepository.findById(id).map(orderMapper::toDto).orElseThrow(() -> new EntityNotFoundException("Order", id));
    }



}
