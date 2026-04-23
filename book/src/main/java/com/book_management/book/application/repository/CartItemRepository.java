package com.book_management.book.application.repository;

import com.book_management.book.domain.dto.CartItemDetails;
import com.book_management.book.domain.dto.CartItemDto;
import com.book_management.book.domain.dto.CartSummary;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface CartItemRepository extends R2dbcRepository<CartItemDto, UUID> {

    @Query("""
        SELECT * FROM book_management."addItemToCart"(:cartId, :bookId, :quantity)
        """)
    Mono<CartItemDto> addItemToCart(
            @Param("cartId") UUID cartId,
            @Param("bookId") UUID bookId,
            @Param("quantity") Integer quantity
    );


    @Query("""
        SELECT * FROM book_management."getCartItems"(:cartId)
        """)
    Flux<CartItemDetails> getCartItemsWithDetails(@Param("cartId") UUID cartId);


    @Query("""
        SELECT 
            ci."CartItemId",
            ci."CartId",
            ci."BookId",
            ci."Quantity",
            ci."PriceSnapshot",
            ci."CreatedAt",
            ci."UpdatedAt"
        FROM book_management."cart_items" ci
        WHERE ci."CartId" = :cartId
        """)
    Flux<CartItemDto> findByCartId(@Param("cartId") UUID cartId);


    @Query("""
        SELECT * FROM book_management."updateCartItemQuantity"(:cartItemId, :quantity)
        """)
    Mono<CartItemDto> updateQuantity(
            @Param("cartItemId") UUID cartItemId,
            @Param("quantity") Integer quantity
    );


    @Query("""
        SELECT * FROM book_management."removeCartItem"(:cartItemId)
        """)
    Mono<Boolean> removeItem(@Param("cartItemId") UUID cartItemId);


    @Query("""
        SELECT * FROM book_management."getCartSummary"(:cartId)
        """)
    Mono<CartSummary> getCartSummary(@Param("cartId") UUID cartId);
}