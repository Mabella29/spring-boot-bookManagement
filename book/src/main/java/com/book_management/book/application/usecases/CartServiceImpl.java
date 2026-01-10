package com.book_management.book.application.usecases;

import com.book_management.book.application.interfaces.CartService;
import com.book_management.book.application.repository.CartItemRepository;
import com.book_management.book.application.repository.CartRepository;

import com.book_management.book.domain.dto.CartItemResponse;
import com.book_management.book.domain.dto.CartItemWithBookDetails;
import com.book_management.book.domain.dto.CartResponse;
import com.book_management.book.domain.dto.CartSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
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
                    // cart items with book details
                    Mono<List<CartItemResponse>> itemsMono = cartItemRepository
                            .getCartItemsWithDetails(cart.getCartId())
                            .map(this::mapToCartItemResponse)
                            .collectList();

                    // cart summary
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
                                        .totalItems(summary.getTotalItems().intValue())
                                        .totalPrice(summary.getTotalPrice())
                                        .createdAt(cart.getCreatedAt())
                                        .updatedAt(cart.getUpdatedAt())
                                        .build();
                            });
                })
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
                .flatMap(cart -> cartItemRepository.addItemToCart(cart.getCartId(), bookId, quantity))
                .flatMap(cartItem -> {
                    return cartItemRepository.getCartItemsWithDetails(cartItem.getCartId())
                            .filter(item -> item.getCartItemId().equals(cartItem.getCartItemId()))
                            .next()
                            .map(this::mapToCartItemResponse);
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
                .flatMap(cartItem -> {
                    return cartItemRepository.getCartItemsWithDetails(cartItem.getCartId())
                            .filter(item -> item.getCartItemId().equals(cartItem.getCartItemId()))
                            .next()
                            .map(this::mapToCartItemResponse);
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
                    return Mono.error(new RuntimeException("Cart item not found: " + cartItemId));
                })
                .then()
                .doOnError(error -> log.error("Error removing cart item", error));
    }

    @Override
    public Mono<Void> clearCart(UUID userId) {
        log.info("Clearing cart for user: {}", userId);

        return cartRepository.findByUserId(userId)
                .flatMap(cart -> cartRepository.clearCart(cart.getCartId()))
                .flatMap(cleared -> {
                    if (Boolean.TRUE.equals(cleared)) {
                        log.info("Cart cleared successfully for user: {}", userId);
                        return Mono.empty();
                    }
                    return Mono.error(new RuntimeException("Failed to clear cart for user: " + userId));
                })
                .then()
                .doOnError(error -> log.error("Error clearing cart", error));
    }


    private CartItemResponse mapToCartItemResponse(CartItemWithBookDetails item) {
        boolean priceChanged = item.getPriceSnapshot().compareTo(item.getCurrentPrice()) != 0;

        return CartItemResponse.builder()
                .cartItemId(item.getCartItemId())
                .bookId(item.getBookId())
                .bookName(item.getBookName())
                .category(item.getCategory())
                .quantity(item.getQuantity())
                .priceSnapshot(item.getPriceSnapshot())
                .currentPrice(item.getCurrentPrice())
                .subtotal(item.getSubtotal())
                .priceChanged(priceChanged)
                .build();
    }
}