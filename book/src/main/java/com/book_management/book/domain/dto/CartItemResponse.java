package com.book_management.book.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CartItemResponse {
    @JsonProperty("cartItemId")
    private UUID cartItemId;

    @JsonProperty("bookId")
    private UUID bookId;

    @JsonProperty("bookName")
    private String bookName;

    @JsonProperty("category")
    private String category;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("priceSnapshot")
    private BigDecimal priceSnapshot;

    @JsonProperty("currentPrice")
    private BigDecimal currentPrice;

    @JsonProperty("subtotal")
    private BigDecimal subtotal;

    @JsonProperty("priceChanged")
    private Boolean priceChanged;
}
