package com.book_management.book.infrastructure.controllers;

import com.book_management.book.application.interfaces.BookRepository;
import com.book_management.book.application.interfaces.BookService;
import com.book_management.book.domain.dto.ApiResponse;
import com.book_management.book.domain.dto.BookRequest;
import com.book_management.book.domain.dto.BookResponse;
import com.book_management.book.domain.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookService bookService;


    @PostMapping
    public Mono<ResponseEntity<BookResponse>> createBook(
            @Valid @RequestBody BookRequest bookRequest
    ){
        return  bookService.createBook(bookRequest)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));

    }

    @GetMapping("/{bookId}")
    public Mono<ResponseEntity<ApiResponse>> getBookById(@PathVariable UUID bookId){
        return bookService.getBookById(bookId)
                .map(res ->ResponseEntity.ok(new ApiResponse(true,"book retrieved",res)));
    }

    @GetMapping
    public Mono<ResponseEntity<PageResponse<BookResponse>>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return bookService.getAllBooks(page,size)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{bookId}")
    public Mono<ResponseEntity<ApiResponse>> updateBook(
            @PathVariable UUID bookId,
            @RequestBody BookRequest bookRequest
    ){
        return bookService.updateBook(bookId,bookRequest)
                .map(updatedBook ->
                        ResponseEntity.ok(new ApiResponse(true,"Updated book successfully",updatedBook)));
    }

    @DeleteMapping("/{bookId}")
    public Mono<ResponseEntity<ApiResponse>> deleteBook(@PathVariable UUID bookId){
        return bookService.deleteBook(bookId)
                .then(Mono.just(ResponseEntity.ok
                        (new ApiResponse
                                (true,"Book deleted successfully",null)

                                )
                ));
    }
}
