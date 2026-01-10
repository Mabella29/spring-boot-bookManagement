package com.book_management.book.application.usecases;

import com.book_management.book.application.repository.BookRepository;
import com.book_management.book.application.interfaces.BookService;
import com.book_management.book.domain.dto.BookRequest;
import com.book_management.book.domain.dto.BookResponse;
import com.book_management.book.domain.dto.PageResponse;
import com.book_management.book.domain.exceptions.BookNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

        return bookRepository.updateBook(
                bookId,
                bookRequest.getBookName(),
                bookRequest.getCategory(),
                bookRequest.getPrice()
                )
                .map(BookResponse::from)
                .switchIfEmpty(Mono.error(new BookNotFoundException("Book not found with the ID: {}" +bookId)))
                .doOnSuccess(response -> log.info("Book updated with id: {}",response.getId()));
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

  @Override
    public Mono<PageResponse<BookResponse>> searchBook(
            String searchTerm, int page, int size
    ){
        log.info("Searching a book page: {} ,size: {}",page,size);

        int offset = page * size;

        return bookRepository.searchBooks(searchTerm,size,offset)
                .map(BookResponse::from)
                .collectList()
                .zipWith(bookRepository.getBookCount())
                .map(tuple -> {
                    var content = tuple.getT1();
                    long totalElements = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) totalElements / size);

                    return PageResponse.<BookResponse>builder()
                            .content(content)
                            .page(page)
                            .size(size)
                            .totalElements(totalElements)
                            .totalPages(totalPages)
                            .build();
                });
}
}
