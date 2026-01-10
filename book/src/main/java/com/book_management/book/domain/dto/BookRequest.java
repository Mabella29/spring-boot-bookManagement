package com.book_management.book.domain.dto;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookRequest {

    @NotBlank(message = "The title of the book is required")
    @Size(max = 255, message = "Book name must not exceed 255 characters")
    private String bookName;


    @NotBlank(message = "Category is required")
    @Size(max = 255, message = "category must not exceed 255 characters")
    private String category;

    @NotNull(message = "Price is required")
    @Positive(message = "price must be greater than zero")
    private BigDecimal price;

    private String description;

    private String apiResponse;
}
