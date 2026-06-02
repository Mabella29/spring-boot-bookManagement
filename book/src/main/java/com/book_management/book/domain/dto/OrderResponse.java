package com.book_management.book.domain.dto;

import com.book_management.book.domain.enums.PaymentMethod;
import com.book_management.book.domain.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private UUID orderId;
    private UUID userId;
    private List<OrderItemResponse> items;
    private BigDecimal totalPrice;
    private String status;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private String paymentReference;
    private BigDecimal amountPaid;
    private OffsetDateTime paidAt;
    private DeliveryAddress deliveryAddress;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
