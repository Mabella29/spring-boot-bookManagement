package com.book_management.book.application.usecases;

import com.book_management.book.application.interfaces.CartService;
import com.book_management.book.application.repository.CartItemRepository;
import com.book_management.book.application.repository.CartRepository;
import com.book_management.book.domain.dto.CartItemResponse;
import com.book_management.book.domain.dto.CartResponse;
import com.book_management.book.domain.dto.CartSummary;
import com.book_management.book.domain.exceptions.CartItemNotFoundException;
import com.book_management.book.domain.exceptions.CartNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public Mono<CartResponse> getCart(UUID userId) {

        log.info("Fetching cart for user: {}", userId);

        return cartRepository.getOrCreateCart(userId)
                .flatMap(cart -> {

                    Mono<List<CartItemResponse>> itemsMono = cartItemRepository
                            .getCartItemsWithDetails(cart.getCartId())
                            .map(CartItemResponse::from)
                            .collectList();


                    Mono<CartSummary> summaryMono = cartItemRepository
                            .getCartSummary(cart.getCartId());

                   //both
                    return Mono.zip(itemsMono, summaryMono)
                            .map(tuple -> {
                                List<CartItemResponse> items = tuple.getT1();
                                CartSummary summary = tuple.getT2();

                                return CartResponse.builder()
                                        .cartId(cart.getCartId())
                                        .userId(cart.getUserId())
                                        .items(items)
                                        .totalItems(summary.getTotalitems().intValue())
                                        .totalPrice(summary.getTotalprice())
                                        .createdAt(cart.getCreatedAt())
                                        .updatedAt(cart.getUpdatedAt())
                                        .build();
                            });
                })
                .switchIfEmpty(Mono.error(new CartNotFoundException("Cart not found ")))
                .doOnSuccess(cartResponse -> log.info("Cart fetched successfully for user: {}", userId))
                .doOnError(error -> log.error("Error fetching cart for user: {}", userId, error));
    }

    @Override
    public Mono<CartItemResponse> addItemToCart(UUID userId, UUID bookId, Integer quantity) {
        log.info("Adding item to cart - userId: {}, bookId: {}, quantity: {}", userId, bookId, quantity);

        if (quantity <= 0) {
            return Mono.error(new IllegalArgumentException("Quantity must be greater than 0"));
        }

        return cartRepository.getOrCreateCart(userId)
                .switchIfEmpty(Mono.error(new CartNotFoundException("Cart not found ")))
                .flatMap(cart ->
                        cartItemRepository.addItemToCart(cart.getCartId(), bookId, quantity))

                .flatMap(cartItem -> {
                    return cartItemRepository.getCartItemsWithDetails(cartItem.getCartId())
                            .filter(item -> item.getCartItemId().equals(cartItem.getCartItemId()))
                            .next()
                            .map(CartItemResponse::from);

                })
                .doOnSuccess(response -> log.info("Item added to cart: {}", response))
                .doOnError(error -> log.error("Error adding item to cart", error));
    }

    @Override
    public Mono<CartItemResponse> updateItemQuantity(UUID userId, UUID cartItemId, Integer quantity) {
        log.info("Updating cart item - userId: {}, cartItemId: {}, newQuantity: {}",
                userId, cartItemId, quantity);

        if (quantity <= 0) {
            return Mono.error(new IllegalArgumentException("Quantity must be greater than 0"));
        }

        return cartItemRepository.updateQuantity(cartItemId, quantity)
                .switchIfEmpty(Mono.error(new CartItemNotFoundException("Cart not found ")))
                .flatMap(cartItem -> {

                    return cartItemRepository.getCartItemsWithDetails(cartItem.getCartId())
                            .filter(item -> item.getCartItemId().equals(cartItem.getCartItemId()))
                            .next()
                            .map(CartItemResponse::from);
                })
                .doOnSuccess(response -> log.info("Cart item updated: {}", response))
                .doOnError(error -> log.error("Error updating cart item", error));
    }

    @Override
    public Mono<Void> removeItem(UUID userId, UUID cartItemId) {
        log.info("Removing cart item - userId: {}, cartItemId: {}", userId, cartItemId);

        return cartItemRepository.removeItem(cartItemId)
                .flatMap(deleted -> {
                    if (Boolean.TRUE.equals(deleted)) {
                        log.info("Cart item removed successfully: {}", cartItemId);
                        return Mono.empty();
                    }
                    return Mono.error(new CartItemNotFoundException("Cart item not found: " + cartItemId));
                })
                .then()
                .doOnError(error -> log.error("Error removing cart item", error));
    }

    @Override
    public Mono<Void> clearCart(UUID userId) {
        log.info("Clearing cart for user: {}", userId);

        return cartRepository.findCartByUserId(userId)
                .switchIfEmpty(Mono.error(new CartNotFoundException("Cart not found ")))
                .flatMap(cart -> cartRepository.clearCart(cart.getCartId()))
                .flatMap(cleared -> {
                    if (Boolean.TRUE.equals(cleared)) {
                        log.info("Cart cleared successfully for user: {}", userId);
                        return Mono.empty();
                    }
                    log.info("Cart was already empty for user: {}", userId);
                    return Mono.empty();
                })
                .then()
                .doOnError(error -> log.error("Error clearing cart", error));
    }
}