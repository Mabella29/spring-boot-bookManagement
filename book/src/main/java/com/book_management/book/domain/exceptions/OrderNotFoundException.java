package com.book_management.book.domain.exceptions;

public class OrderNotFoundException extends ResourceNotFoundException{
    public OrderNotFoundException(String message){
        super(message);
    }
}
