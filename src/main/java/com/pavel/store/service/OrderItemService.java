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

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<OrderItemResponseDto> findAllOrderItem(Pageable pageable) {

        return orderItemRepository.findAll(pageable).map(orderItemMapper::toDto);
    }

    @Transactional(readOnly = true)
    public OrderItemResponseDto findOrderById(Long id) {

        return orderItemRepository.findById(id).map(orderItemMapper::toDto).orElseThrow(() -> new EntityNotFoundException("OrderItem", id));
    }

    @Transactional
    public OrderItemResponseDto save(OrderItemRequestDto orderItemRequestDto) {
        Product product = productRepository.findById(orderItemRequestDto.getProductId()).orElseThrow(() -> new EntityNotFoundException("Product", orderItemRequestDto.getProductId()));

        OrderItem orderItem = orderItemMapper.toEntity(orderItemRequestDto);
        orderItem.setProduct(product);
        orderItem.setProductPrice(product.getPrice());
        orderItem.setProductName(product.getName());
        orderItem.setProductArticle(product.getArticle());
        OrderItem saved = orderItemRepository.save(orderItem);

        return orderItemMapper.toDto(saved);
    }

    @Transactional
    public void deleteById(Long id) {

        if (orderItemRepository.existsById(id)) {
            orderItemRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("OrderItem", id);
        }
    }

    @Transactional
    public void update() {


    }
}
