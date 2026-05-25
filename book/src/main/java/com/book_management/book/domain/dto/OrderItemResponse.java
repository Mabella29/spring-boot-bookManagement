package com.book_management.book.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;


@Data
@Builder
public class OrderItemResponse {
    @JsonProperty("orderItemId")
    private UUID orderItemId;

    @JsonProperty("bookId")
    private UUID bookId;

    @JsonProperty("bookName")
    private String bookName;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("category")
    private String category;

    @JsonProperty("priceSnapshot")
    private BigDecimal priceSnapshot;

    @JsonProperty("subtotal")
    private BigDecimal subtotal;

    @JsonProperty("imageUrl")
    private String imageUrl;

    @JsonProperty("createdAt")
    private OffsetDateTime createdAt;

    @JsonProperty("updatedAt")
    private OffsetDateTime updatedAt;

    public static OrderItemResponse from(OrderItemDetails item){
        return OrderItemResponse.builder()
                .orderItemId(item.getOrderItemId())
                .bookId(item.getBookId())
                .bookName(item.getBookName())
                .category(item.getCategory())
                .quantity(item.getQuantity())
                .priceSnapshot(item.getPriceSnapshot())
                .subtotal(item.getSubtotal())
                .imageUrl((item.getImageUrl()))
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
