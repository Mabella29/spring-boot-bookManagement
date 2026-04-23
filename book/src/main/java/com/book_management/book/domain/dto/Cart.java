package com.book_management.book.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "carts", schema = "book_management")
public class Cart {

    @Id
    @JsonProperty("cartId")
    @Column("CartId")
    private UUID cartId;

    @JsonProperty("userId")
    @Column("UserId")
    private UUID userId;

    @JsonProperty("createdAt")
    @Column("CreatedAt")
    private OffsetDateTime createdAt;

    @JsonProperty("updatedAt")
    @Column("UpdatedAt")
    private OffsetDateTime updatedAt;
}
