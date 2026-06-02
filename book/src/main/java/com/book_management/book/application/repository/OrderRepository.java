package com.book_management.book.application.repository;

import com.book_management.book.domain.dto.Order;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

public interface OrderRepository extends R2dbcRepository<Order, UUID> {
    @Query("""
      SELECT * FROM book_management."createOrder"($1,$2::jsonb)
      """
    )
    Mono<Order> createOrder(UUID userId, String deliveryAddress);

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

    @Query("""
   SELECT * FROM book_management."getAllOrders"($1,$2)
   """)
    Flux<Order> getAllOrders(int limit, int offset);

    @Query("""
    SELECT * FROM book_management."getOrderCount"()
    """)
    Mono<Long> getOrderCount();

    @Query("""
     SELECT * FROM book_management."updatePayment"($1,$2::book_management.paymentStatus,$3::book_management.paymentMethod,$4,$5)
     """)
    Mono<Order> updatePayment(UUID orderId, String paymentStatus, String paymentMethod, String paymentReference, BigDecimal amountPaid);
}
