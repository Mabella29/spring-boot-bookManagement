package com.book_management.book.application.usecases;

import com.book_management.book.application.repository.BookRepository;
import com.book_management.book.domain.dto.Book;
import com.book_management.book.domain.dto.BookRequest;
import com.book_management.book.domain.exceptions.BookNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookServiceImplTests {
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private UUID bookId;

    private Book book;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        bookId = UUID.randomUUID();

        book = new Book();
        book.setBookName("To kill a mocking bird");
        book.setCategory("Fiction");
        book.setPrice(BigDecimal.valueOf(45.6));
        book.setDescription("test");
    }

    @Test
    void createBookTest(){
        BookRequest request = new BookRequest();
        request.setBookName("To kill a mocking bird");
        request.setCategory("Fiction");
        request.setPrice(BigDecimal.valueOf(45.6));
        request.setDescription("test");

        when(bookRepository.createBook(any(),any(),any(),any(),any()))
                .thenReturn(Mono.just(book));

        StepVerifier.create(bookService.createBook(request))
                .expectNextMatches(res -> res.getBookName().equals("To kill a mocking bird"))
                .verifyComplete();
        verify(bookRepository).createBook(any(),any(),any(),any(),any());
    }

    //get book by ID success
    @Test
    void getBookByIdTest(){
        when(bookRepository.getBookById(bookId))
                .thenReturn(Mono.just(book));
        StepVerifier.create(bookService.getBookById(bookId))
                .expectNextMatches(res -> res.getBookName().equals("To kill a mocking bird"))
                .verifyComplete();
    }

    //failure
    @Test
    void getBookByIdNotFoundTest(){
        when(bookRepository.getBookById(bookId))
                .thenReturn(Mono.empty());
        StepVerifier.create(bookService.getBookById(bookId))
                .expectError(BookNotFoundException.class)
                .verify();
    }

    @Test
    void updateBookTest(){
        BookRequest request = new BookRequest();
        request.setBookName("To kill a mocking bird");
        request.setCategory("Fiction");
        request.setPrice(BigDecimal.valueOf(45.6));
        request.setDescription("test");


        when(bookRepository.updateBook(any(),any(),any(),any(),any()))
                .thenReturn(Mono.just(book));

        StepVerifier.create(bookService.updateBook(bookId,request))
                .expectNextMatches(res -> res.getBookName().equals("To kill a mocking bird"))
                .verifyComplete();
    }

    @Test
    void deleteBookTest(){
        when(bookRepository.deleteBook(bookId))
        .thenReturn(Mono.just(true));

        StepVerifier.create(bookService.deleteBook(bookId))
                .verifyComplete();
    }

    @Test
    void deleteBookNotFoundTest(){
        when(bookRepository.deleteBook(bookId))
                .thenReturn(Mono.just(false));

        StepVerifier.create(bookService.deleteBook(bookId))
                .expectError(BookNotFoundException.class)
                .verify();
    }

}

