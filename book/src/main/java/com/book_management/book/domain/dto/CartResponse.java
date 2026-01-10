package com.book_management.book.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CartResponse {
    @JsonProperty("cartId")
    private UUID cartId;

    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("items")
    private List<CartItemResponse> items;

    @JsonProperty("totalItems")
    private Integer totalItems;

    @JsonProperty("totalPrice")
    private BigDecimal totalPrice;

    @JsonProperty("createdAt")
    private OffsetDateTime createdAt;

    @JsonProperty("updatedAt")
    private OffsetDateTime updatedAt;
}
