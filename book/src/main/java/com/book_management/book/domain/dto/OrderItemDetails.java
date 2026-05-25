package com.book_management.book.domain.dto;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDetails {
    @Column("OrderItemId")
    private UUID orderItemId;

    @Column("OrderId")
    private UUID orderId;

    @Column("BookId")
    private UUID bookId;

    @Column("BookName")
    private String bookName;

    @Column("Category")
    private String category;

    @Column("Quantity")
    private Integer quantity;

    @Column("PriceSnapshot")
    private BigDecimal priceSnapshot;

    @Column("Subtotal")
    private BigDecimal subtotal;

    @Column("ImageUrl")
    private String imageUrl;

    @Column("CreatedAt")
    private OffsetDateTime createdAt;

    @Column("UpdatedAt")
    private OffsetDateTime updatedAt;
}