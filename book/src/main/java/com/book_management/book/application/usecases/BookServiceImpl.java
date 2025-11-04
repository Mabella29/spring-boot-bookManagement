package com.book_management.book.application.usecases;

import com.book_management.book.application.interfaces.BookRepository;
import com.book_management.book.application.interfaces.BookService;
import com.book_management.book.domain.dto.BookRequest;
import com.book_management.book.domain.dto.BookResponse;
import com.book_management.book.domain.dto.PageResponse;
import com.book_management.book.domain.exceptions.BookNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Transactional
    public Mono<BookResponse> createBook(BookRequest bookRequest){
        log.info("Creating a book: {}",bookRequest.getBookName());

        return bookRepository.createBook(
                bookRequest.getBookName(),
                bookRequest.getCategory(),
                bookRequest.getPrice(),
                bookRequest.getDescription()
//                bookRequest.getCreatedBy()
        )
                .map(BookResponse::from)
                .doOnSuccess(response -> log.info("Book created with id: {}",response.getId()))
                .doOnError(error -> log.error("Error creating book: {}", error.getMessage()));
    };

    @Override
    public Mono<BookResponse> getBookById(UUID bookId){
        log.info("Fetching book with ID: {}",bookId);

        return bookRepository.getBookById(bookId)
                .map(BookResponse::from)
                .switchIfEmpty(Mono.error(new BookNotFoundException("Book not found with the ID: {}" +bookId)));
    }

    @Override
    public Mono<PageResponse<BookResponse>> getAllBooks(int page, int size) {
        log.info("Fetching all Books -page: {} ,size: {} ",page,size );

        int offset = page * size;

        return Mono.zip(
                        bookRepository.getAllBooks(size,offset).map(BookResponse::from).collectList(),
                        bookRepository.getBookCount()
                )
                .map(tuple -> {
                 var content = tuple.getT1();
                 var totalElements = tuple.getT2();
                 var totalPages = (int) Math.ceil((double) totalElements / size);

            return PageResponse.<BookResponse>builder()
                    .content(content)
                    .page(page)
                    .size(size)
                    .totalElements(totalElements)
                    .totalPages(totalPages)
                    .build();
        });
    }

    @Override
    public Mono<BookResponse> updateBook(
             UUID bookId,
             BookRequest bookRequest
    ){
        log.info("Upadating a book -ID:{}, body:{}", bookId,bookRequest);

        return bookRepository.getBookById(bookId)
                .switchIfEmpty(Mono.error(new BookNotFoundException("Book not found")))
                .flatMap(existingBook ->
                        bookRepository.updateBook(
                                bookId,
                                bookRequest.getBookName(),
                                bookRequest.getCategory(),
                                bookRequest.getPrice()
                        )
                                .map(BookResponse::from)
                                .doOnSuccess(res -> log.info("Book has been updated successfully: {}",res)))
                .doOnError(error -> log.error(error.getMessage()));
    }

    @Override
    public Mono<Void> deleteBook(UUID bookId){
        log.info("Deleting a book with the ID: {}",bookId);

        return bookRepository.deleteBook(bookId)
                .flatMap(deletedBook -> {
                    if(Boolean.TRUE.equals(deletedBook)){
                        log.info("Deleted book {}",bookId);
                        return Mono.empty();
                    }
                    return Mono.error(new BookNotFoundException("Book with the ID: {}" +bookId));
                });

    }
}
