package com.book_management.book.domain.dto;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartSummary {
    private Long totalitems;
    private java.math.BigDecimal totalprice;
}