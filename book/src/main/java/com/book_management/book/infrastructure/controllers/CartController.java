package com.book_management.book.infrastructure.controllers;

import com.book_management.book.application.interfaces.CartService;
import com.book_management.book.application.usecases.CartServiceImpl;
import com.book_management.book.domain.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;


    @GetMapping
    public Mono<ResponseEntity<ApiResponse<CartResponse>>> getCart(
            @AuthenticationPrincipal Mono<String> principalMono
    ) {
        return principalMono.flatMap(userId -> {
            UUID userUuid = UUID.fromString(userId);

            return cartService.getCart(userUuid)
                    .map(cartResponse -> ResponseEntity.ok(
                            new ApiResponse<>(true, "Cart retrieved successfully", cartResponse)
                    ))
                    .doOnError(err -> log.error("failed to retrieve cart", err));
        });
    }


    @PostMapping("/items")
    public Mono<ResponseEntity<ApiResponse<CartItemResponse>>> addItemToCart(
            @AuthenticationPrincipal Mono<String> principalMono,
            @Valid
            @RequestBody AddItemRequest request
            ) {
        return principalMono.flatMap(userId -> {
            UUID userUuid = UUID.fromString(userId);

            return cartService.addItemToCart(userUuid, request.getBookId(), request.getQuantity())
                    .map(cartItemResponse -> ResponseEntity
                            .status(HttpStatus.CREATED)
                            .body(new ApiResponse<>(true, "Item added to cart", cartItemResponse))
                    )
                    .doOnError(error -> log.error("Error adding item to cart", error));
        });
    }


    @PutMapping("/items/{cartItemId}")
    public Mono<ResponseEntity<ApiResponse<CartItemResponse>>> updateItemQuantity(
            @AuthenticationPrincipal Mono<String> principalMono,
            @PathVariable UUID cartItemId,
            @Valid
            @RequestBody UpdateQuantityRequest request
    ) {
        return principalMono.flatMap(userId -> {
            UUID userUuid = UUID.fromString(userId);

            return cartService.updateItemQuantity(userUuid, cartItemId, request.getQuantity())
                    .map(cartItemResponse -> ResponseEntity.ok(
                            new ApiResponse<>(true, "Item quantity updated", cartItemResponse)
                    ))
                    .doOnError(err -> log.error("error updating the quantity", err));
        });
    }


    @DeleteMapping("/items/{cartItemId}")
    public Mono<ResponseEntity<ApiResponse<Void>>> removeItem(
            @AuthenticationPrincipal Mono<String> principalMono,
            @PathVariable UUID cartItemId
    ) {
        return principalMono.flatMap(userId -> {
            UUID userUuid = UUID.fromString(userId);

            return cartService.removeItem(userUuid, cartItemId)
                    .then(Mono.just(ResponseEntity.ok(
                            new ApiResponse<Void>(true, "Item removed from cart", null)
                    )))
                    .doOnError(error -> log.error("error removing the item", error));
        });
    }


    @DeleteMapping
    public Mono<ResponseEntity<ApiResponse<Void>>> clearCart(
            @AuthenticationPrincipal Mono<String> principalMono
    ) {
        return principalMono.flatMap(userId -> {
            UUID userUuid = UUID.fromString(userId);

            return cartService.clearCart(userUuid)
                    .then(Mono.just(ResponseEntity.ok(
                            new ApiResponse<Void>(true, "Cart cleared successfully", null)
                    )))
                    .doOnError(err -> log.error("failed to clear cart", err));
        });
    }

}