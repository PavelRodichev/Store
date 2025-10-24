package com.pavel.store.mapper.mapers;


import com.pavel.store.dto.request.OrderCreateDto;
import com.pavel.store.dto.response.OrderResponseDto;
import com.pavel.store.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface OrderMapper {


    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "orderStatus", source = "orderStatus")
    @Mapping(target = "orderDate", source = "orderDate")
    OrderResponseDto toDto(Order order);

    Order toEntity(OrderCreateDto orderResponseDto);

}
