package com.book_management.book.application.interfaces;

import com.book_management.book.domain.dto.OrderResponse;
import com.book_management.book.domain.dto.UpdateStatusRequest;
import com.book_management.book.domain.enums.OrderStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderService {
   Mono<OrderResponse> createOrder(UUID userId);
   Mono<OrderResponse> getOrderById(UUID orderId);
   Flux<OrderResponse> getOrdersByUserId(UUID userId);
   Mono<OrderResponse> updateOrderStatus(UUID orderId, OrderStatus status);

}
