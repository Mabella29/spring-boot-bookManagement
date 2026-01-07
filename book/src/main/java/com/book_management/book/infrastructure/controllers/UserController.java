package com.book_management.book.infrastructure.controllers;


import com.book_management.book.application.interfaces.UserService;
import com.book_management.book.domain.dto.ApiResponse;
import com.book_management.book.domain.dto.LoginRequest;
import com.book_management.book.domain.dto.LoginResponse;
import com.book_management.book.domain.dto.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public Mono<ResponseEntity<ApiResponse<UserDto>>> registerUser(
            @Valid @RequestBody UserDto userDto
    ) {
        log.info("Register request received: {}", userDto);

        return userService.registerUser(userDto)
                .doOnSubscribe(s -> log.info("Subscription started"))
                .doOnNext(user -> log.info("User created: {}", user))
                .doOnError(e -> log.error(" Error creating user", e))
                .map(user -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(new ApiResponse<>(true, "User registered successfully", user))
                )
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Service returned EMPTY Mono");
                    return Mono.just(
                            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(new ApiResponse<>(false, "User created but response empty", null))
                    );
                }));
    }


    @PostMapping("/login")
    public Mono<ResponseEntity<ApiResponse<LoginResponse>>> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        return userService.login(loginRequest)
                .map(response -> ResponseEntity.ok(
                        new ApiResponse<>(true, "Login successful", response)
                ))
                .onErrorResume(ex -> Mono.just(
                        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new ApiResponse<>(false, ex.getMessage(), null))
                ));
    }

    @GetMapping("/{username}")
    public Mono<ResponseEntity<ApiResponse<UserDto>>> getUserByName(@PathVariable String username) {
        return userService.GetUserByName(username)
                .map(user -> ResponseEntity.ok(
                        new ApiResponse<>(true, "User fetched successfully", user)
                ))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }


}
