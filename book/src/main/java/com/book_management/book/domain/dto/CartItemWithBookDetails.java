package com.book_management.book.domain.dto;


import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemWithBookDetails {
    private UUID cartItemId;
    private UUID cartId;
    private UUID bookId;
    private String bookName;
    private String category;
    private Integer quantity;
    private java.math.BigDecimal priceSnapshot;
    private java.math.BigDecimal currentPrice;
    private java.math.BigDecimal subtotal;
    private java.time.OffsetDateTime createdAt;
    private java.time.OffsetDateTime updatedAt;
}