package com.pavel.store.dto.response;

import lombok.Builder;
import lombok.Data;


@Data
public class OrderWithUserDto {
    private OrderResponseDto orderResponseDto;
    private UserResponseDto userResponseDto;
}
