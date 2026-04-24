package com.book_management.book.application.repository;
import com.book_management.book.domain.dto.Book;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface BookRepository extends R2dbcRepository<Book, UUID>{

    @Query("""
            SELECT * FROM book_management."createBook"($1, $2, $3, $4, $5)
            """)
    Mono<Book> createBook(
            String bookName,
            String category,
            BigDecimal price,
            String description,
            Integer stock
    );

    @Query("""
            SELECT * FROM book_management."getBookById"($1)
            """)
    Mono<Book> getBookById(UUID bookId);

    @Query("""
            SELECT * FROM book_management."getAllBooks"($1,$2)
            """)
    Flux<Book> getAllBooks(int limit, int offset);

@Query("""
        SELECT * FROM book_management."getBookCount"()
        """)
    Mono<Long> getBookCount();

@Query("""
        SELECT * FROM book_management."updateBook"($1, $2, $3, $4, $5)
        """)
    Mono<Book> updateBook(
            UUID bookId,
            String bookName,
            String category,
            BigDecimal price,
            String description
);

@Query("""
        SELECT * FROM book_management."deleteBook"($1)
        """)
    Mono<Boolean> deleteBook(UUID bookId);

@Query("""
        SELECT * FROM book_management."searchBooks"($1,$2,$3)
""")
    Flux<Book> searchBooks(String searchTerm, int page, int size);

    @Query("""
        SELECT * FROM book_management."searchBooksCount"($1)
        """)
    Mono<Long> searchBooksCount(String searchTerm);
}
