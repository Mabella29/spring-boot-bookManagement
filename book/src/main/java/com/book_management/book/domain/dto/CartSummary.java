package com.book_management.book.domain.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartSummary {
    private Long totalItems;
    private java.math.BigDecimal totalPrice;
}