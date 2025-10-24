package com.pavel.store.dto.response;


import com.pavel.store.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {

    private Long id;

    private String username;

    private LocalDateTime orderDate;

    private BigDecimal totalAmount;

    private String orderStatus;

    private String address;



}
