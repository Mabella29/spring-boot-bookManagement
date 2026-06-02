package com.book_management.book.application.interfaces;

import com.book_management.book.domain.dto.DeliveryAddress;
import com.book_management.book.domain.dto.OrderResponse;
import com.book_management.book.domain.dto.PageResponse;
import com.book_management.book.domain.dto.UpdateStatusRequest;
import com.book_management.book.domain.enums.OrderStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

public interface OrderService {
   Mono<OrderResponse> createOrder(UUID userId, DeliveryAddress deliveryAddress);
   Mono<OrderResponse> getOrderById(UUID orderId);
   Flux<OrderResponse> getOrdersByUserId(UUID userId);
   Mono<OrderResponse> updateOrderStatus(UUID orderId, OrderStatus status);
   Mono<PageResponse<OrderResponse>> getAllOrders(int page, int size);
    Mono<OrderResponse> updatePayment(UUID orderId, String paymentStatus, String paymentMethod, String paymentReference, BigDecimal amountPaid);

}
