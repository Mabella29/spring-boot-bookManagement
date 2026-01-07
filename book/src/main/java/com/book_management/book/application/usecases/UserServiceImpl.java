package com.book_management.book.application.usecases;

import com.book_management.book.application.interfaces.UserService;
import com.book_management.book.application.repository.RoleRepository;
import com.book_management.book.application.repository.UserRepository;
import com.book_management.book.domain.dto.LoginRequest;
import com.book_management.book.domain.dto.LoginResponse;
import com.book_management.book.domain.dto.UserDto;
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
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
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
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")));
    }

    @Override
    public Mono<UserDto> registerUser(UserDto user) {
        log.info("Starting registration for user: {}", user.getEmail());

        String hashedPassword = encoder.encode(user.getUserPassword());

        if (user.getRoleName() == null || user.getRoleName().isBlank()) {
            user.setRoleName("USER");
        }

        return roleRepository.findByRoleName(user.getRoleName())
                .doOnNext(role -> log.info("Found role: {} with UUID: {}", role.getRoleName(), role.getRoleUuid()))
                .switchIfEmpty(Mono.error(new RuntimeException("Role not found: " + user.getRoleName())))
                .flatMap(role -> {
                    log.info("Calling registerUser with email={}, userName={}, roleUuid={}",
                            user.getEmail(), user.getUserName(), role.getRoleUuid());

                    return userRepository.registerUser(
                            user.getEmail(),
                            user.getUserName(),
                            hashedPassword,
                            role.getRoleUuid()
                    );
                })
                .doOnNext(createdUser -> log.info("Database returned user: {}", createdUser))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Database function returned empty, fetching user by email instead");

                    return userRepository.findByEmail(user.getEmail());
                }))
                .map(u -> {
                    log.info("Final user before removing password: {}", u);
                    u.setUserPassword(null);
                    return u;
                });
    }

    @Override
    public Mono<UserDto> GetUserByName(String userName) {
        return userRepository.GetUserByName(userName)
                .map(user -> {
                    user.setUserPassword(null);
                    return user;
                });
    }
}