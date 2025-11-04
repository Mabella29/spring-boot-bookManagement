package com.book_management.book.domain.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book{

    @Id
    private UUID id;

    @Column("bookname")
    private String bookName;

    @Column("Category")
    private String category;

    @Column("Price")
    private BigDecimal price;

    @Column("Description")
    private String description;

//    @Column("CreatedBy")
//    private UUID createdBy;

    @Column("Active")
    private boolean active;

    @Column("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime createdAt;

    @Column("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime updatedAt;
}
