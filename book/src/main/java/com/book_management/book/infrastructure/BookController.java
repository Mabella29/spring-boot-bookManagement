package com.book_management.book.infrastructure;

import com.book_management.book.domain.BookDto;
import com.book_management.book.interfaces.BookService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@AllArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public String createBook(@RequestBody BookDto dto){
      return  bookService.createBook(dto);

    }

    @PostMapping("/fetch/{id} ")
    public BookDto getBook(@PathVariable int id){
        return bookService.getBook(id);
    }

    @PutMapping("/{id}")
    public String updateBook(@RequestBody BookDto dto){
        return bookService.updateBook(dto);
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable int id){
        return bookService.deleteBook(id);
    }

    @GetMapping
    public List<BookDto> getBooks(){
        return bookService.getBooks();
    }

}
