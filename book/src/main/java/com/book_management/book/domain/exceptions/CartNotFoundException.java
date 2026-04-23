package com.book_management.book.domain.exceptions;

public class CartNotFoundException extends ResourceNotFoundException{
    public CartNotFoundException(String message){
        super(message);
    }
}
