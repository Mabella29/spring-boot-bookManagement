package com.book_management.book.domain.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddItemRequest {
    @NotNull(message = "Book Id is required")
    private UUID bookId;

    @NotNull(message = "quantity is required")
    @Positive(message = "quantity must be greater than 0")
    private Integer quantity;
}
