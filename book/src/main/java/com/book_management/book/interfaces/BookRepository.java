package com.book_management.book.interfaces;

import com.book_management.book.domain.BookDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<BookDto,Integer> {
}
