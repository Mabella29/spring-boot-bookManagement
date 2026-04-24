package com.book_management.book.application.repository;


import com.book_management.book.domain.dto.OrderItem;
import com.book_management.book.domain.dto.OrderItemDetails;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface OrderItemRepository extends R2dbcRepository<OrderItem, UUID> {
    @Query("""
    SELECT * FROM book_management."getOrderItems"($1)
    """)
    Flux<OrderItemDetails> getOrderItems(UUID orderId);
}
