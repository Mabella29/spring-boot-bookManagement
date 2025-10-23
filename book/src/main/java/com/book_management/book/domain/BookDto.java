package com.book_management.book.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;


@Entity
@Table(name="book_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "category cannot be empty")
    private String category;

    @Positive(message = "price must be greater than zero")
    private double price;

    private String description;
}
