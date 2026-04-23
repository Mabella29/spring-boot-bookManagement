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
                    .onErrorResume(error -> {
                        log.error("Error retrieving cart", error);
                        return Mono.just(ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ApiResponse<>(false, "Failed to retrieve cart", null))
                        );
                    });
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
//                    .onErrorResume(error -> {
//                        log.error("Error adding item to cart", error);
//                        if (error instanceof IllegalArgumentException) {
//                            return Mono.just(ResponseEntity
//                                    .status(HttpStatus.BAD_REQUEST)
//                                    .body(new ApiResponse<>(false, error.getMessage(), null))
//                            );
//                        }
//
//                        // anything else
//                        return Mono.just(ResponseEntity
//                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                                .body(new ApiResponse<>(false, "Failed to add item to cart", null))
//                        );
//                    });
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
                    .onErrorResume(error -> {
                        log.error("Error updating item quantity", error);
                        if(error instanceof IllegalArgumentException){
                            return Mono.just(ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(false, error.getMessage(), null)));
                        }
                        return  Mono.just(
                                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(false,"failed to update quantity",null))
                        );
                    });
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
                    .onErrorResume(error -> {
                        log.error("Error removing item", error);
                        if(error instanceof RuntimeException){
                            return Mono.just(ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(false, error.getMessage(), null))
                            );
                        }
                        return Mono.just(
                                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(false,"failed to remove item", null))
                        );
                    });
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
                    .onErrorResume(error -> {
                        log.error("Error clearing cart", error);
                        return Mono.just(ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ApiResponse<>(false, "failed to clear cart", null))
                        );
                    });
        });
    }

}