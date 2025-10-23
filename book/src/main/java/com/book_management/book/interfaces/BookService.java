package com.book_management.book.interfaces;

import com.book_management.book.domain.BookDto;

import java.util.List;

public interface BookService {
    public String createBook(BookDto dto);
    public String updateBook(BookDto dto);
    public String deleteBook(Integer bookId);
    public BookDto getBook(Integer bookId);
    public List<BookDto> getBooks();
}
