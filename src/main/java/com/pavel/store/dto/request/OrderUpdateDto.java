package com.pavel.store.dto.request;

import com.pavel.store.entity.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
