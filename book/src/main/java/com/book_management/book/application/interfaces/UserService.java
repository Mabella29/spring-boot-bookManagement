package com.book_management.book.application.interfaces;

import com.book_management.book.domain.dto.LoginRequest;
import com.book_management.book.domain.dto.LoginResponse;
import com.book_management.book.domain.dto.UserDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserService {

    public  Mono<LoginResponse> login(LoginRequest request);
    public Mono<UserDto> GetUserByName(String userName);

    public Mono<UserDto> registerUser(UserDto user);
}
