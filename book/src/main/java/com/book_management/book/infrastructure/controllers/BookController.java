package com.book_management.book.infrastructure.controllers;

import com.book_management.book.application.interfaces.BookService;
import com.book_management.book.application.usecases.BookServiceImpl;
import com.book_management.book.domain.dto.ApiResponse;
import com.book_management.book.domain.dto.BookRequest;
import com.book_management.book.domain.dto.BookResponse;
import com.book_management.book.domain.dto.PageResponse;
import com.book_management.book.infrastructure.services.CloudinaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookService bookService;
    private final CloudinaryService cloudinaryService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ApiResponse<BookResponse>>> createBook(
            @RequestPart("bookName") String bookName,
            @RequestPart("category") String category,
            @RequestPart("price") String price,
            @RequestPart("description") String description,
            @RequestPart("stock") String stock,
            @RequestPart("image") FilePart image
    ) {

        Mono<String> imageUrlMono = cloudinaryService.uploadImage(image);

        return imageUrlMono.flatMap(imageUrl -> {
            BookRequest bookRequest = BookRequest.builder()
                    .bookName(bookName)
                    .category(category)
                    .price(new BigDecimal(price))
                    .description(description)
                    .stock(Integer.parseInt(stock))
                    .imageUrl(imageUrl)
                    .build();

            return bookService.createBook(bookRequest)
                    .map(response -> ResponseEntity
                            .status(HttpStatus.CREATED)
                            .body(new ApiResponse<>(true, "book created successfully", response)));
        });
    }

    @GetMapping("/{bookId}")
    public  Mono<ResponseEntity<ApiResponse<BookResponse>>> getBookById(@PathVariable UUID bookId){
        return bookService.getBookById(bookId)
                .map(res ->ResponseEntity.ok(new ApiResponse<>(true,"book retrieved",res)));
    }

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<PageResponse<BookResponse>>>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return bookService.getAllBooks(page,size)
                .map(res ->
                        ResponseEntity.ok(new ApiResponse<>(true,"books fetched successfully",res)));
    }

    @PutMapping(value = "/{bookId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ApiResponse<BookResponse>>> updateBook(
            @PathVariable UUID bookId,
            @RequestPart("bookName") String bookName,
            @RequestPart("category") String category,
            @RequestPart("price") String price,
            @RequestPart("description") String description,
            @RequestPart("image") FilePart image
    ) {
        Mono<String> imageUrlMono = cloudinaryService.uploadImage(image);

        return imageUrlMono.flatMap(imageUrl -> {
            BookRequest bookRequest = BookRequest.builder()
                    .bookName(bookName)
                    .category(category)
                    .price(new BigDecimal(price))
                    .description(description)
                    .imageUrl(imageUrl)
                    .build();

            return bookService.updateBook(bookId, bookRequest)
                    .map(updatedBook -> ResponseEntity.ok(
                            new ApiResponse<>(true, "Updated book successfully", updatedBook)));
        });
    }

    @DeleteMapping("/{bookId}")
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteBook(@PathVariable UUID bookId){
        return bookService.deleteBook(bookId)
                .then(Mono.just(ResponseEntity.ok
                        (new ApiResponse<>
                                (true,"Book deleted successfully",null)

                                )
                ));
    }

    @GetMapping("/search")
    public Mono<ResponseEntity<ApiResponse<PageResponse<BookResponse>>>> searchBook(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return bookService.searchBook(searchTerm,page,size)
                .map(result -> ResponseEntity.ok(new ApiResponse<>(true, "Books retrieved", result)));
    }
}
