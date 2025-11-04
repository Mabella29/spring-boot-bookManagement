package com.book_management.book.domain.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {
    private UUID id;
    private String bookName;
    private String category;
    private BigDecimal price;
    private String description;
//    private UUID createdBy;
    private Boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .bookName(book.getBookName())
                .category(book.getCategory())
                .price(book.getPrice())
                .description(book.getDescription())
//                .createdBy(book.getCreatedBy())
                .active(book.isActive())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}
