package com.book_management.book.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("bookName")
    private String bookName;

    @JsonProperty("category")
    private String category;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("description")
    private String description;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("stock")
    private Integer stock;

    @JsonProperty("imageUrl")
    private String imageUrl;

    @JsonProperty("createdAt")
    private OffsetDateTime createdAt;

    @JsonProperty("updatedAt")
    private OffsetDateTime updatedAt;

    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .id(book.getBookId())
                .bookName(book.getBookName())
                .category(book.getCategory())
                .price(book.getPrice())
                .description(book.getDescription())
                .active(book.getActive())
                .stock(book.getStock())
                .imageUrl(book.getImageUrl())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}