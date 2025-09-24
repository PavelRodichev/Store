package com.pavel.store.mapper.mapers;

import com.pavel.store.dto.request.OrderItemRequestDto;
import com.pavel.store.dto.response.OrderItemResponseDto;
import com.pavel.store.dto.response.ProductResponseDto;
import com.pavel.store.entity.OrderItem;
import com.pavel.store.entity.Product;
import com.pavel.store.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface OrderItemMapper {


    OrderItemResponseDto toDto(OrderItem orderItem);

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productPrice", source = "product.price")
    @Mapping(target = "productArticle", source = "product.article")
    @Mapping(target = "quantity", source = "dto.quantity")
    OrderItem toEntity(OrderItemRequestDto dto, ProductResponseDto product);


}
