package com.book_management.book.application.repository;

import com.book_management.book.domain.dto.CartDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface CartRepository extends R2dbcRepository<CartDto, UUID> {


    @Query("""
        SELECT * FROM book_management."getOrCreateCart"(:userId)
        """)
    Mono<CartDto> getOrCreateCart(@Param("userId") UUID userId);


    @Query("""
        SELECT 
            c."CartId",
            c."UserId",
            c."CreatedAt",
            c."UpdatedAt"
        FROM book_management."carts" c
        WHERE c."UserId" = :userId
        """)
    Mono<CartDto> findByUserId(@Param("userId") UUID userId);


    @Query("""
        SELECT * FROM book_management."clearCart"(:cartId)
        """)
    Mono<Boolean> clearCart(@Param("cartId") UUID cartId);
}