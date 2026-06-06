package com.book_management.book.application.interfaces;

import com.book_management.book.domain.dto.*;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserService {

    public  Mono<LoginResponse> login(LoginRequest request);
    public Mono<UserResponse> GetUserByName(String userName);
    public Mono<UserResponse> registerUser(RegisterUserRequest request);
}
