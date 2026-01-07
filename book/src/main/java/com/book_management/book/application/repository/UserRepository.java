package com.book_management.book.application.repository;

import com.book_management.book.domain.dto.UserDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface UserRepository extends R2dbcRepository<UserDto, UUID> {

    @Query("SELECT * FROM book_management.\"GetUserByName\"(:userName)")
    Mono<UserDto> GetUserByName(@Param("userName") String userName);

    @Query("SELECT * FROM book_management.\"CreateUser\"(:email, :userName, :hashedPassword, :roleUuid)")
    Mono<UserDto> registerUser(
            @Param("email") String email,
            @Param("userName") String userName,
            @Param("hashedPassword") String hashedPassword,
            @Param("roleUuid") UUID roleUuid
    );

    @Query("SELECT * FROM book_management.\"GetUserByEmail\"(:email)")
    Mono<UserDto> findByEmail(@Param("email") String email);
}