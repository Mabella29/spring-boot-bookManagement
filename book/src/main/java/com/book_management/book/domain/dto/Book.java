package com.book_management.book.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "books", schema = "book_management")
public class Book {

    @Id
    @JsonProperty("id")
    @Column("BookId")
    private UUID bookId;

    @JsonProperty("bookName")
    @Column("BookName")
    private String bookName;

    @JsonProperty("category")
    @Column("Category")
    private String category;

    @JsonProperty("price")
    @Column("Price")
    private BigDecimal price;

    @JsonProperty("description")
    @Column("Description")
    private String description;

    @JsonProperty("createdBy")
    @Column("CreatedBy")
    private UUID createdBy;

    @JsonProperty("active")
    @Column("Active")
    private Boolean active;

    @JsonProperty("createdAt")
    @Column("CreatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime createdAt;

    @JsonProperty("updatedAt")
    @Column("UpdatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime updatedAt;
}