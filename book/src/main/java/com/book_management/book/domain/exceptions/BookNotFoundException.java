package com.book_management.book.domain.exceptions;

public class BookNotFoundException extends ResourceNotFoundException{
    public BookNotFoundException(String message){
        super(message);
    }
}
