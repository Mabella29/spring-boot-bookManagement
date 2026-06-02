package com.book_management.book.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    @NotNull(message = "Delivery address is required")
    @Valid
    private DeliveryAddress deliveryAddress;
}
