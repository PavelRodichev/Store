package com.pavel.store.mapper.mapers;


import com.pavel.store.dto.request.OrderItemRequestDto;
import com.pavel.store.dto.response.OrderItemResponseDto;
import com.pavel.store.dto.response.OrderResponseDto;
import com.pavel.store.entity.Order;
import com.pavel.store.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {


    OrderItemResponseDto toDto(OrderItem orderItem);

    OrderItem toEntity(OrderItemRequestDto orderItemResponseDto);

}
