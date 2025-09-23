package com.pavel.store.mapper.mapers;


import com.pavel.store.dto.response.OrderResponseDto;
import com.pavel.store.entity.Order;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface OrderMapper {

    OrderResponseDto toDto(Order order);

    Order toEntity(OrderResponseDto orderResponseDto);

}
