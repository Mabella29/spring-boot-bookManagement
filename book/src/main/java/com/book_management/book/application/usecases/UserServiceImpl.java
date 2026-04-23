package com.book_management.book.application.usecases;

import com.book_management.book.application.interfaces.UserService;
import com.book_management.book.application.repository.RoleRepository;
import com.book_management.book.application.repository.UserRepository;
import com.book_management.book.domain.dto.*;
import com.book_management.book.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;

    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(user -> {
                    if (!user.isActive()) {
                        return Mono.error(new RuntimeException("User is inactive"));
                    }

                    if (!encoder.matches(request.getUserPassword(), user.getUserPassword())) {
                        return Mono.error(new RuntimeException("Invalid credentials"));
                    }

                    String token = jwtUtil.generateToken(
                            user.getUserId().toString(),
                            List.of(user.getRoleName())
                    );

                    LoginResponse response = new LoginResponse(token, user.getUserName(), user.getRoleName());
                    return Mono.just(response);
                })
                .doOnSuccess(res -> log.info("User logged in successfully: {}", request.getEmail()))
                .doOnError(err -> log.error("Login failed for: {}", request.getEmail()));
    }

    @Override
    public Mono<UserResponse> registerUser(RegisterUserRequest request) {
        log.info("Starting registration for user: {}", request.getEmail());

        String hashedPassword = encoder.encode(request.getUserPassword());

        return roleRepository.findByRoleName("USER")
                .flatMap(role -> {
                    return userRepository.registerUser(
                            request.getEmail(),
                            request.getUserName(),
                            hashedPassword,
                            role.getRoleUuid()
                    );
                })
                .doOnNext(createdUser -> log.info("Database returned user: {}", createdUser))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Database function returned empty, fetching user by email instead");

                    return userRepository.findByEmail(request.getEmail());
                }))
                .map(UserResponse::from)
                .doOnSuccess(res -> log.info("successfully registered a user {}",res))
                .doOnError(err -> log.error("error registering a user", err));
    }

    @Override
    public Mono<UserResponse> GetUserByName(String userName) {
        return userRepository.GetUserByName(userName)
                .map(UserResponse::from)
                .doOnSuccess(res -> log.info("successfully fetched the user {}",res))
                .doOnError(err -> log.error("error fetching the user",err));
    }
}