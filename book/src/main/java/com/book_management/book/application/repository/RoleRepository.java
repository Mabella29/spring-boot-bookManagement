package com.book_management.book.application.repository;

import com.book_management.book.domain.dto.RoleDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RoleRepository extends R2dbcRepository<RoleDto, UUID> {

    @Query("""
        SELECT "RoleUuid" as "RoleUuid", 
               "RoleName" as "RoleName", 
               "IsActive" as "IsActive"
        FROM book_management."Roles"
        WHERE LOWER("RoleName") = LOWER(:roleName) 
        AND "IsActive" = TRUE
        """)
    Mono<RoleDto> findByRoleName(@Param("roleName") String roleName);
}