package com.book_management.book.usecases;

import com.book_management.book.domain.BookDto;
import com.book_management.book.interfaces.BookRepository;
import com.book_management.book.interfaces.BookService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepo;

    @Override
    public String createBook(BookDto dto){
      bookRepo.save(dto);
      return "Book added successfullyyyyyy";

    }

    @Override
    public String updateBook(BookDto dto){
        bookRepo.save(dto);
        return "Book updated successfully";
    }

    @Override
    public String deleteBook(Integer bookId){
        bookRepo.deleteById(bookId);
        return "success";
    }

    @Override
    public BookDto getBook(Integer bookId){
      return bookRepo.findById(bookId).get();
    }

    @Override
    public List<BookDto> getBooks(){

        return bookRepo.findAll();

    }


}
