package com.book_management.book.infrastructure.controllers;

import com.book_management.book.application.interfaces.OrderService;
import com.book_management.book.domain.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<OrderResponse>>> createOrder(
            @AuthenticationPrincipal Mono<String> userPrincipal,
            @Valid @RequestBody CreateOrderRequest request
    ){
        return userPrincipal.flatMap(extractId ->{
            UUID userId = UUID.fromString(extractId);


            return orderService.createOrder(userId, request.getDeliveryAddress())
                    .map(orderResponse ->ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ApiResponse<>(true,"Order created successfully",orderResponse)))
                    .doOnError(err -> log.error("unable to create order", err));
        });

    }

    @GetMapping("/{orderId}")
    public Mono<ResponseEntity<ApiResponse<OrderResponse>>> getOrderById(
            @AuthenticationPrincipal Mono<String> userPrincipal,
            @PathVariable UUID orderId
    ){

        return userPrincipal.flatMap(extractId ->{
            UUID userId = UUID.fromString(extractId);

            return orderService.getOrderById(orderId)
                    .map(orderResponse -> ResponseEntity
                            .ok(new ApiResponse<>(true,"successfully fetched the order",orderResponse)))
                    .doOnError(error -> log.error("failed to fetch order", error));
        });

    }

    @GetMapping
    public ResponseEntity<Flux<ApiResponse<OrderResponse>>> getOrdersByUserId(
            @AuthenticationPrincipal Mono<String> userMono
    ) {
        Flux<ApiResponse<OrderResponse>> response = userMono.flatMapMany(extractId -> {
            UUID userId = UUID.fromString(extractId);
            return orderService.getOrdersByUserId(userId)
                    .map(orderResponse -> new ApiResponse<>(true, "successfully fetched all orders", orderResponse));
        });

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public Mono<ResponseEntity<ApiResponse<PageResponse<OrderResponse>>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    )
    {
            return orderService.getAllOrders(page,size)
                    .map(res-> ResponseEntity.ok(
                            new ApiResponse<>(true,"Orders fetched",res)
                    ));
    }

    @PutMapping("/{orderId}/status")
    public Mono<ResponseEntity<ApiResponse<OrderResponse>>> updateOrderStatus(
            @AuthenticationPrincipal Mono<String> userMono,
            @PathVariable UUID orderId,
            @RequestBody UpdateStatusRequest request
            ){

        return userMono.flatMap(extractId ->{
           UUID userId = UUID.fromString(extractId);

           return  orderService.updateOrderStatus(orderId,request.getStatus())
                   .map(orderResponse ->
                           ResponseEntity.ok(new ApiResponse<>(true,"successfully updated order status", orderResponse)))
                   .doOnError(error -> log.error("failed to update status", error));

        });

    }

    @PutMapping("/{orderId}/payment")
    public Mono<ResponseEntity<ApiResponse<OrderResponse>>> updatePayment(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdatePaymentRequest request
    ) {
        return orderService.updatePayment(
                        orderId,
                        request.getPaymentStatus().name(),
                        request.getPaymentMethod().name(),
                        request.getPaymentReference(),
                        request.getAmountPaid()
                )
                .map(orderResponse -> ResponseEntity.ok(
                        new ApiResponse<>(true, "Payment updated successfully", orderResponse)));
    }
}
