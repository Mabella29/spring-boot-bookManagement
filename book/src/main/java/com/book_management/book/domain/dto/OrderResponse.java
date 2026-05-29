package com.book_management.book.domain.dto;

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
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static OrderResponse from (Order order,List<OrderItemDetails> items){
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .items(items.stream().map(OrderItemResponse::from).toList())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
