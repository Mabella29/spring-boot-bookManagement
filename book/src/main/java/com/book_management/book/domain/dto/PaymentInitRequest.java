package com.book_management.book.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInitRequest {
    @NotNull(message = "Order ID is required")
    private UUID orderId;

//    @NotBlank(message = "Email is required")
//    private String email;
}