package com.pavel.store.dto.request;

import com.pavel.store.entity.OrderItem;
import com.pavel.store.entity.OrderStatus;
import com.pavel.store.entity.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateDto {

    @NotBlank(message = "Shipping address is required")
    @Size(max = 500, message = "Shipping address must be less than 500 characters")
    private String shippingAddress;

    private String status;

}
