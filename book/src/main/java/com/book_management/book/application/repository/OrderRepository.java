package com.book_management.book.application.repository;

import com.book_management.book.domain.dto.Order;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderRepository extends R2dbcRepository<Order, UUID> {
    @Query("""
      SELECT * FROM book_management."createOrder"($1)
      """
    )
    Mono<Order> createOrder(UUID userId);

    @Query("""
     SELECT * FROM book_management."getOrderById"($1)
     """)
    Mono<Order> getOrderById(UUID orderId);

    @Query("""
    SELECT * FROM book_management."getOrdersByUserId"($1)
    """)
    Flux<Order> getAllOrdersByUserId(UUID userId);

    @Query("""
    SELECT * FROM book_management."updateOrderStatus"($1,$2::book_management.orderStatus)
    """)
    Mono<Order> updateOrderStatus(UUID orderId, String status);
}
