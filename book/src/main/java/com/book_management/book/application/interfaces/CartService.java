package com.book_management.book.application.interfaces;

import com.book_management.book.domain.dto.CartItemResponse;
import com.book_management.book.domain.dto.CartResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CartService {


    Mono<CartResponse> getCart(UUID userId);


    Mono<CartItemResponse> addItemToCart(UUID userId, UUID bookId, Integer quantity);


    Mono<CartItemResponse> updateItemQuantity(UUID userId, UUID cartItemId, Integer quantity);


    Mono<Void> removeItem(UUID userId, UUID cartItemId);


    Mono<Void> clearCart(UUID userId);
}