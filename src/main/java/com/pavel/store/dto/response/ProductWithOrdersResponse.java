package com.pavel.store.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ProductWithOrdersResponse {
    private ProductResponseDto product;
    private List<OrderWithUserDto> orders;
}
