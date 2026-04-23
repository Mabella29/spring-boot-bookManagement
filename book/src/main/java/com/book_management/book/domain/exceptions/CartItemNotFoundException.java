package com.book_management.book.domain.exceptions;

public class CartItemNotFoundException extends ResourceNotFoundException{
    public CartItemNotFoundException(String message){
        super(message);
    }
}
