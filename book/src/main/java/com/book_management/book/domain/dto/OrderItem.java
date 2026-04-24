package com.book_management.book.domain.dto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orderItems", schema = "book_management")
public class OrderItem {
    @Id
    @Column("OrderItemId")
    private UUID orderItemId;

    @Column("OrderId")
    private UUID orderId;

    @Column("BookId")
    private UUID bookId;

    @Column("BookName")
    private String bookName;

    @Column("Quantity")
    private Integer quantity;

    @Column("PriceSnapshot")
    private BigDecimal priceSnapshot;

    @Column("CreatedAt")
    private OffsetDateTime createdAt;

    @Column("UpdatedAt")
    private OffsetDateTime updatedAt;
}
