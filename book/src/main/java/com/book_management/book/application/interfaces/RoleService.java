package com.book_management.book.application.interfaces;

import com.book_management.book.domain.dto.RoleDto;
import reactor.core.publisher.Mono;

public interface RoleService {
    public Mono<RoleDto> findByRoleName(String roleName);
}
