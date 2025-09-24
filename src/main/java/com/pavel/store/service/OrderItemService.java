package com.pavel.store.service;

import com.pavel.store.controller.handler.exeption.EntityNotFoundException;
import com.pavel.store.dto.request.OrderItemRequestDto;
import com.pavel.store.dto.response.OrderItemResponseDto;
import com.pavel.store.entity.OrderItem;
import com.pavel.store.entity.Product;


import com.pavel.store.mapper.mapers.OrderItemMapper;
import com.pavel.store.repository.OrderItemRepository;
import com.pavel.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderItemService {


}

