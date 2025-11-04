package com.book_management.book.application.interfaces;

import com.book_management.book.domain.dto.BookRequest;
import com.book_management.book.domain.dto.BookResponse;
import com.book_management.book.domain.dto.PageResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BookService {
    public Mono<BookResponse> createBook(BookRequest bookRequest);
    public Mono<BookResponse> getBookById(UUID bookId);
    public Mono<PageResponse<BookResponse>> getAllBooks(int page, int size);
    public Mono<BookResponse> updateBook(UUID bookId, BookRequest bookRequest);
    public Mono<Void> deleteBook(UUID bookId);
}
