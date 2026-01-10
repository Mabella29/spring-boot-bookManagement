package com.book_management.book.domain.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "cartItems", schema = "book_management")
public class CartItemDto {
    @Id
    @JsonProperty("cartItemId")
    @Column("CartItemId")
    private UUID cartItemId;

    @JsonProperty("cartId")
    @Column("CartId")
    private UUID cartId;

    @JsonProperty("bookId")
    @Column("BookId")
    private UUID bookId;

    @JsonProperty("quantity")
    @Column("Quantity")
    private Integer quantity;

    @JsonProperty("priceSnapshot")
    @Column("PriceSnapshot")
    private BigDecimal priceSnapshot;

    @JsonProperty("createdAt")
    @Column("CreatedAt")
    private OffsetDateTime createdAt;

    @JsonProperty("updatedAt")
    @Column("UpdatedAt")
    private OffsetDateTime updatedAt;
}
