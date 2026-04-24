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
@Table(name = "orders", schema = "book_management")
public class Order {
    @Id
    @Column("OrderId")
    private UUID orderId;

    @Column("UserId")
    private UUID userId;

    @Column("TotalPrice")
    private BigDecimal totalPrice;

    @Column("Status")
    private String status;

    @Column("CreatedAt")
    private OffsetDateTime createdAt;

    @Column("UpdatedAt")
    private OffsetDateTime updatedAt;
}
